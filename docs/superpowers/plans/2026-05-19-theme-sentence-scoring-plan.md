# Theme-Based Naming — Sentence-Level Scoring Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace per-character tag-based theme matching with per-sentence density scoring + a pre-built ThemeIndex, eliminating the PoemWord.meaningTag column dependency and the exact-match query bug.

**Architecture:** Extract theme keywords into a new `ThemeMatcher` component. At startup, `ThemeMatcher` loads all poems, scores every sentence against every theme, and builds a `Map<String, Set<Long>>` index (theme → matching poem IDs). `generateByTheme()` queries the index for relevant poems, then delegates to `buildResponse()` for sentence-level theme filtering (same filter as keyword mode). Stop writing `meaningTag` during import.

**Tech Stack:** Java 21, Spring Boot 3, JPA, existing entity/repository layer

---

### Task 1: Create ThemeMatcher component

**Files:**
- Create: `backend/src/main/java/com/example/naming/service/ThemeMatcher.java`
- Create: `backend/src/test/java/com/example/naming/service/ThemeMatcherTest.java`

- [ ] **Step 1: Write ThemeMatcher.java**

```java
package com.example.naming.service;

import com.example.naming.dto.PoemCacheItem;
import com.example.naming.entity.Poem;
import com.example.naming.repository.PoemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ThemeMatcher {

    private static final Logger log = LoggerFactory.getLogger(ThemeMatcher.class);

    static final Map<String, Set<Character>> THEME_KEYWORDS = new LinkedHashMap<>();

    static {
        THEME_KEYWORDS.put("山水", toCharSet("山水云溪泉峰江河海湖石谷涧涛"));
        THEME_KEYWORDS.put("豪迈", toCharSet("剑龙鹏虎雷霆乾坤雄壮威猛霸刚"));
        THEME_KEYWORDS.put("婉约", toCharSet("花柳燕莺蝶絮丝纱帘屏枕泪愁"));
        THEME_KEYWORDS.put("清雅", toCharSet("清雅幽素静逸闲淡远高洁兰竹梅菊"));
        THEME_KEYWORDS.put("离别", toCharSet("别离送归去行远望思念忆怀"));
        THEME_KEYWORDS.put("田园", toCharSet("田园村桑麻豆瓜耕牧渔樵锄"));
        THEME_KEYWORDS.put("志向", toCharSet("志道德仁义忠信诚正直贤圣君士"));
        THEME_KEYWORDS.put("爱情", toCharSet("情爱恋慕思念心意缘盟誓痴"));
    }

    private static Set<Character> toCharSet(String s) {
        return s.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
    }

    // theme → set of poem IDs that have at least one matching sentence
    private final Map<String, Set<Long>> poemIndex = new LinkedHashMap<>();
    // poemId → source (for source filtering in queries)
    private final Map<Long, String> poemSourceMap = new HashMap<>();

    private volatile boolean built = false;

    /**
     * Build the theme index by loading all poems, scoring every sentence.
     * Called once at startup.
     */
    public void buildIndex(PoemRepository poemRepository) {
        if (built) return;
        for (String theme : THEME_KEYWORDS.keySet()) {
            poemIndex.put(theme, new HashSet<>());
        }
        List<PoemCacheItem> cacheItems = poemRepository.findAllCacheItems();
        log.info("ThemeMatcher: loading {} poems to build theme index...", cacheItems.size());

        // Load poems in batches to avoid OOM
        int batchSize = 500;
        int totalMatches = 0;
        for (int i = 0; i < cacheItems.size(); i += batchSize) {
            int end = Math.min(i + batchSize, cacheItems.size());
            List<Long> batchIds = cacheItems.subList(i, end).stream()
                .map(PoemCacheItem::id).toList();
            List<Poem> batch = poemRepository.findAllByIdIn(batchIds);

            for (Poem poem : batch) {
                poemSourceMap.put(poem.getId(), poem.getSource());
                String content = poem.getContent();
                if (content == null || content.isEmpty()) continue;

                List<String> sentences = splitSentences(content);
                for (String sentence : sentences) {
                    String clean = cleanPunctuation(sentence);
                    if (clean.length() < 3) continue;
                    for (var entry : THEME_KEYWORDS.entrySet()) {
                        int hits = 0;
                        for (int j = 0; j < clean.length(); j++) {
                            if (entry.getValue().contains(clean.charAt(j))) hits++;
                        }
                        if (hits > 0) {
                            poemIndex.get(entry.getKey()).add(poem.getId());
                            totalMatches++;
                        }
                    }
                }
            }
        }
        built = true;
        log.info("ThemeMatcher: index built — {} theme-poem matches across {} poems",
            totalMatches, cacheItems.size());
    }

    /**
     * Returns a set of poem IDs that have sentences matching any of the given themes.
     * Optionally filters by source.
     */
    public Set<Long> getPoemIdsForThemes(List<String> themes, List<String> sources) {
        if (!built) return Collections.emptySet();
        Set<Long> result = new HashSet<>();
        for (String theme : themes) {
            Set<Long> ids = poemIndex.get(theme);
            if (ids != null) result.addAll(ids);
        }
        if (sources != null && !sources.isEmpty()) {
            result.removeIf(id -> {
                String src = poemSourceMap.get(id);
                return src == null || !sources.contains(src);
            });
        }
        return result;
    }

    /**
     * Returns the theme with the highest keyword density in the sentence,
     * or null if no theme has a single keyword match.
     */
    public String getBestTheme(String sentence) {
        String best = null;
        double bestScore = 0;
        int len = sentence.length();
        if (len == 0) return null;

        for (var entry : THEME_KEYWORDS.entrySet()) {
            int hits = 0;
            for (int i = 0; i < len; i++) {
                if (entry.getValue().contains(sentence.charAt(i))) hits++;
            }
            if (hits > 0) {
                double score = (double) hits / len;
                if (score > bestScore) {
                    bestScore = score;
                    best = entry.getKey();
                }
            }
        }
        return best;
    }

    /**
     * Returns true if the sentence's best theme is in the given list.
     */
    public boolean matchesAny(String sentence, Collection<String> themes) {
        String best = getBestTheme(sentence);
        return best != null && themes.contains(best);
    }

    public Set<String> getAllThemes() {
        return Collections.unmodifiableSet(THEME_KEYWORDS.keySet());
    }

    private List<String> splitSentences(String content) {
        String str = content.replaceAll("[\\s　\"'（）《》\\[\\]<>brp/]", "");
        String[] parts = str.split("[，。！？；：]");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String clean = cleanPunctuation(part);
            if (clean.length() >= 3 && clean.length() <= 14) {
                result.add(part.trim());
            }
        }
        if (result.isEmpty()) {
            for (String part : parts) {
                String clean = cleanPunctuation(part);
                if (clean.length() >= 3 && clean.length() <= 18) {
                    result.add(part.trim());
                }
            }
        }
        return result;
    }

    private String cleanPunctuation(String str) {
        return str.replaceAll("[<>《》！*^()$%~!@#…&%￥—+=、。，？；：'\"`·\\[\\]]", "");
    }
}
```

- [ ] **Step 2: Write unit test**

Create `backend/src/test/java/com/example/naming/service/ThemeMatcherTest.java`:

```java
package com.example.naming.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ThemeMatcherTest {

    private final ThemeMatcher matcher = new ThemeMatcher();

    @Test
    void shouldIdentifyMountainWaterTheme() {
        String best = matcher.getBestTheme("山气日夕佳飞鸟相与还");
        assertEquals("山水", best);
    }

    @Test
    void shouldIdentifyLoveTheme() {
        String best = matcher.getBestTheme("情意思念盟誓痴心");
        assertEquals("爱情", best);
    }

    @Test
    void shouldReturnNullForNoMatch() {
        String best = matcher.getBestTheme("一二三四五六七八");
        assertNull(best);
    }

    @Test
    void shouldMatchAgainstThemeList() {
        assertTrue(matcher.matchesAny("山气日夕佳", List.of("山水", "田园")));
        assertFalse(matcher.matchesAny("山气日夕佳", List.of("爱情", "豪迈")));
    }

    @Test
    void shouldHandleEmptySentence() {
        assertNull(matcher.getBestTheme(""));
    }
}
```

- [ ] **Step 3: Compile and run tests**

```bash
cd backend && mvn test -Dtest=ThemeMatcherTest -q
```
Expected: 5/5 pass.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/ThemeMatcher.java \
        backend/src/test/java/com/example/naming/service/ThemeMatcherTest.java
git commit -m "feat: add ThemeMatcher with sentence-level density scoring and ThemeIndex"
```

---

### Task 2: Wire ThemeIndex into NameService

**Files:**
- Modify: `backend/src/main/java/com/example/naming/service/NameService.java`

- [ ] **Step 1: Inject ThemeMatcher and build index at startup**

Add `ThemeMatcher` field and constructor parameter. In the constructor (currently lines 43-49), change:

```java
public NameService(PoemRepository poemRepository, PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository, BlacklistConfig blacklistConfig, ThemeMatcher themeMatcher) {
    this.poemRepository = poemRepository;
    this.poemWordRepository = poemWordRepository;
    this.nameRecordRepository = nameRecordRepository;
    this.cachedPoems = new ArrayList<>();
    this.blacklistConfig = blacklistConfig;
    this.themeMatcher = themeMatcher;
    loadPoemsToCache();
    themeMatcher.buildIndex(poemRepository);
}
```

Add field declaration (next to `private final Random random`):

```java
private final ThemeMatcher themeMatcher;
```

- [ ] **Step 2: Rewrite generateByTheme method**

Replace lines 91-101:

```java
public GenerateResponse generateByTheme(GenerateRequest req) {
    List<String> themes = req.getThemes();
    List<PoemWord> candidates;
    if (req.getSources() != null && !req.getSources().isEmpty()) {
        candidates = poemWordRepository.findByMeaningTagsAndSources(themes, req.getSources());
    } else {
        candidates = poemWordRepository.findByMeaningTags(themes);
    }
    List<Poem> poems = extractPoems(candidates);
    return buildResponse(req, poems, null, "theme");
}
```

With:

```java
public GenerateResponse generateByTheme(GenerateRequest req) {
    Set<Long> poemIds = themeMatcher.getPoemIdsForThemes(req.getThemes(), req.getSources());
    if (poemIds.isEmpty()) {
        return new GenerateResponse(Collections.emptyList());
    }
    // Sample if the matched set is large
    List<Long> ids = new ArrayList<>(poemIds);
    if (ids.size() > RANDOM_POEM_SAMPLE) {
        Collections.shuffle(ids, random);
        ids = ids.subList(0, RANDOM_POEM_SAMPLE);
    }
    List<Poem> poems = poemRepository.findAllByIdIn(ids);
    return buildResponse(req, poems, null, req.getThemes(), "theme");
}
```

- [ ] **Step 3: Add themes parameter to buildResponse**

Change method signature from:

```java
private GenerateResponse buildResponse(GenerateRequest req, List<Poem> poems, String keyword, String mode) {
```

To:

```java
private GenerateResponse buildResponse(GenerateRequest req, List<Poem> poems, String keyword, List<String> themes, String mode) {
```

- [ ] **Step 4: Add theme-based sentence filtering inside buildResponse**

In the sentence filtering section (after the keyword filter block), add theme filtering. Replace the block starting at `List<String> pool = sentences;` through the keyword filter (lines 130-137):

```java
            List<String> pool = sentences;
            if (keyword != null && !keyword.isEmpty()) {
                List<String> filtered = new ArrayList<>();
                for (String s : sentences) {
                    if (s.contains(keyword)) filtered.add(s);
                }
                if (!filtered.isEmpty()) pool = filtered;
            }
            if (themes != null && !themes.isEmpty()) {
                List<String> themed = new ArrayList<>();
                for (String s : sentences) {
                    String clean = cleanBadChars(cleanPunctuation(s));
                    if (clean.length() >= 3 && themeMatcher.matchesAny(clean, themes)) {
                        themed.add(s);
                    }
                }
                if (!themed.isEmpty()) {
                    pool = themed;
                }
            }
```

- [ ] **Step 5: Update all buildResponse callers to pass themes parameter**

For `generateRandom` (line 64), change:

```java
return buildResponse(req, poems, null, "random");
```

To:

```java
return buildResponse(req, poems, null, null, "random");
```

For `generateByKeyword` (line 88), change:

```java
return buildResponse(req, poems, keyword, "keyword");
```

To:

```java
return buildResponse(req, poems, keyword, null, "keyword");
```

The `generateByTheme` call already passes `req.getThemes()` from Step 2.

- [ ] **Step 6: Compile**

```bash
cd backend && mvn compile -q
```
Expected: no errors.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/NameService.java
git commit -m "feat: switch theme generation to ThemeIndex lookup with sentence-level scoring"
```

---

### Task 3: Remove meaningTag assignment from DataImportService

**Files:**
- Modify: `backend/src/main/java/com/example/naming/service/DataImportService.java`

- [ ] **Step 1: Remove TAG_KEYWORDS map and assignTags method**

Delete lines 22-32 (the entire `TAG_KEYWORDS` static block).

Delete lines 118-126 (the `assignTags` method).

- [ ] **Step 2: Remove the assignTags call in import loop**

On line 93, delete:

```java
                pw.setMeaningTag(assignTags(String.valueOf(c)));
```

The `meaningTag` column will stay null for new imports.

- [ ] **Step 3: Compile**

```bash
cd backend && mvn compile -q
```
Expected: no errors.

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/DataImportService.java
git commit -m "refactor: remove per-character tag assignment during import"
```

---

### Task 4: Clean up unused repository methods

**Files:**
- Modify: `backend/src/main/java/com/example/naming/repository/PoemWordRepository.java`

- [ ] **Step 1: Remove unused meaningTag queries**

Delete the two unused query methods:

```java
    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.meaningTag IN :tags")
    List<PoemWord> findByMeaningTags(@Param("tags") List<String> tags);
```

and:

```java
    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.meaningTag IN :tags AND p.source IN :sources")
    List<PoemWord> findByMeaningTagsAndSources(@Param("tags") List<String> tags, @Param("sources") List<String> sources);
```

- [ ] **Step 2: Compile**

```bash
cd backend && mvn compile -q
```
Expected: no errors. Verify that `PoemWordRepository` is still referenced by `NameService` — it is, via `findByWord` and `findByWordAndSources` for keyword mode.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/example/naming/repository/PoemWordRepository.java
git commit -m "refactor: remove unused meaningTag repository queries"
```

---

### Task 5: End-to-end verification

- [ ] **Step 1: Run all backend tests**

```bash
cd backend && mvn test -q
```
Expected: ThemeMatcherTest 5/5 pass. No regressions in existing tests.

- [ ] **Step 2: Full compile**

```bash
cd backend && mvn compile -q
```
Expected: BUILD SUCCESS.

- [ ] **Step 3: Build frontend**

```bash
cd frontend && npm run build
```
Expected: built successfully (no API contract changes — `/api/name/theme` endpoint unchanged).

- [ ] **Step 4: Manual API smoke test**

Start the backend and test theme generation:

```bash
curl -s -X POST http://localhost:8080/api/name/theme \
  -H "Content-Type: application/json" \
  -d '{"themes":["山水"],"surname":"李","count":3,"length":2}' | python3 -m json.tool
```
Expected: Returns 3 name candidates with source sentences containing 山水-related keywords.

```bash
curl -s -X POST http://localhost:8080/api/name/theme \
  -H "Content-Type: application/json" \
  -d '{"themes":["山水","清雅"],"surname":"王","count":3,"length":2,"sources":["tang","song"]}' | python3 -m json.tool
```
Expected: Returns candidates from Tang/Song sources only.

- [ ] **Step 5: Commit verification log**

```bash
git log --oneline -6
```
Expected: 5 new commits on top of current HEAD.
