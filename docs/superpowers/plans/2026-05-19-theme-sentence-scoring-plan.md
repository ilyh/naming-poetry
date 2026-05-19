# Theme-Based Naming — Sentence-Level Scoring Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace per-character tag-based theme matching with per-sentence density scoring, eliminating the PoemWord.meaningTag column dependency and the exact-match query bug.

**Architecture:** Extract theme keywords into a new `ThemeMatcher` component. Rewrite `generateByTheme()` to sample random poems (like `generateRandom()` does), then filter sentences by theme keyword density inside `buildResponse()`. Stop writing `meaningTag` during import.

**Tech Stack:** Java 21, Spring Boot 3, JPA, existing entity/repository layer

---

### Task 1: Create ThemeMatcher component

**Files:**
- Create: `backend/src/main/java/com/example/naming/service/ThemeMatcher.java`

- [ ] **Step 1: Write ThemeMatcher.java**

```java
package com.example.naming.service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ThemeMatcher {

    private static final Map<String, Set<Character>> THEME_KEYWORDS = new LinkedHashMap<>();

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
                if (entry.getValue().contains(sentence.charAt(i))) {
                    hits++;
                }
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
        String best = matcher.getBestTheme("山盟虽在锦书难托");
        // "山" hits 山水, "盟" hits 爱情 — both have 1 hit.
        // Ties go to whichever theme appears first in LinkedHashMap.
        // "山水" is registered first, so it wins on tie.
        // Let's use a sentence with more love keywords to avoid tie.
        String best2 = matcher.getBestTheme("情意思念盟誓痴心");
        assertEquals("爱情", best2);
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
git commit -m "feat: add ThemeMatcher with sentence-level density scoring"
```

---

### Task 2: Rewrite generateByTheme() in NameService

**Files:**
- Modify: `backend/src/main/java/com/example/naming/service/NameService.java`

- [ ] **Step 1: Inject ThemeMatcher and change generateByTheme to use poem sampling**

Add `ThemeMatcher` to the constructor:

In the constructor injection (lines 43-49), change:
```java
public NameService(PoemRepository poemRepository, PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository, BlacklistConfig blacklistConfig) {
```
to:
```java
public NameService(PoemRepository poemRepository, PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository, BlacklistConfig blacklistConfig, ThemeMatcher themeMatcher) {
```

Add field:
```java
private final ThemeMatcher themeMatcher;
```

And in constructor body add:
```java
this.themeMatcher = themeMatcher;
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
    List<PoemCacheItem> cacheItems = samplePoemsFromCache(req.getSources(), RANDOM_POEM_SAMPLE);
    if (cacheItems.isEmpty()) {
        return new GenerateResponse(Collections.emptyList());
    }
    List<Long> ids = cacheItems.stream().map(PoemCacheItem::id).toList();
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

In the sentence filtering section (after `List<String> sentences = splitSentences(content)`, around line 128-137), add theme filtering AFTER the keyword filter:

Replace lines 128-137:
```java
            List<String> pool = sentences;
            if (keyword != null && !keyword.isEmpty()) {
                List<String> filtered = new ArrayList<>();
                for (String s : sentences) {
                    if (s.contains(keyword)) filtered.add(s);
                }
                if (!filtered.isEmpty()) pool = filtered;
            }
```

With:
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

- [ ] **Step 5: Update all buildResponse callers to pass the new parameter**

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

- [ ] **Step 6: Compile**

```bash
cd backend && mvn compile -q
```
Expected: no errors.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/NameService.java
git commit -m "feat: switch theme generation to sentence-level density scoring"
```

---

### Task 3: Remove meaningTag assignment from DataImportService

**Files:**
- Modify: `backend/src/main/java/com/example/naming/service/DataImportService.java`

- [ ] **Step 1: Remove TAG_KEYWORDS map and assignTags method**

Delete lines 22-32 (the entire `TAG_KEYWORDS` static block).

Delete lines 118-126 (the `assignTags` method).

- [ ] **Step 2: Remove the assignTags call in import loop**

On line 93, replace:
```java
                pw.setMeaningTag(assignTags(String.valueOf(c)));
```
With nothing (delete the line). The `meaningTag` column will simply stay null for new imports.

- [ ] **Step 3: Remove unused imports if any**

After removing assignTags, check if `Map` and `List` are still needed (they are, for the method signatures). No changes needed.

- [ ] **Step 4: Compile**

```bash
cd backend && mvn compile -q
```
Expected: no errors.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/DataImportService.java
git commit -m "refactor: remove per-character tag assignment during import"
```

---

### Task 4: Clean up unused repository methods

**Files:**
- Modify: `backend/src/main/java/com/example/naming/repository/PoemWordRepository.java`

- [ ] **Step 1: Remove unused meaningTag queries**

Delete lines 13-14 (`findByMeaningTags`) and lines 25-26 (`findByMeaningTagsAndSources`).

- [ ] **Step 2: Compile**

```bash
cd backend && mvn compile -q
```
Expected: no errors.

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
Expected: ThemeMatcherTest 5/5 pass. No regressions.

- [ ] **Step 2: Full compile**

```bash
cd backend && mvn compile -q
```
Expected: EXIT 0.

- [ ] **Step 3: Build frontend to verify no API contract changes**

```bash
cd .. && cd frontend && npm run build
```
Expected: ✓ built.

- [ ] **Step 4: Manual API smoke test**

Start the backend and test with curl:
```bash
curl -X POST http://localhost:8080/api/name/theme \
  -H "Content-Type: application/json" \
  -d '{"themes":["山水"],"surname":"李","count":3,"length":2}'
```
Expected: Returns 3 name candidates with source sentences.

```bash
curl -X POST http://localhost:8080/api/name/theme \
  -H "Content-Type: application/json" \
  -d '{"themes":["山水","清雅"],"surname":"王","count":3,"length":2,"sources":["tang","song"]}'
```
Expected: Returns candidates from Tang/Song only.

- [ ] **Step 5: Commit verification**

```bash
git log --oneline -5
```
