# Import Performance Optimization Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Optimize DataImportService import speed by adding @Transactional and batch-saving PoemWord records with saveAll().

**Architecture:** Single-file change to DataImportService.java. Poems are still saved individually (needed for ID generation to link PoemWords), but PoemWords are accumulated and flushed every 500 via saveAll(). Entire import runs in one @Transactional boundary.

**Tech Stack:** Spring Boot 3.2.5, Spring Data JPA (Hibernate), MySQL

---

### Task 1: Refactor DataImportService with batch PoemWord saving

**Files:**
- Modify: `backend/src/main/java/com/example/naming/service/DataImportService.java`

- [ ] **Step 1: Add @Transactional import and annotation**

Add to imports:
```java
import org.springframework.transaction.annotation.Transactional;
```

Add `@Transactional` to `importFromResource`:
```java
@Transactional
private String importFromResource(String resourcePath) {
```

- [ ] **Step 2: Rewrite importFromResource to use batch PoemWord saving**

Replace the entire `importFromResource` and `importPoem` methods with the batch version below.

The key changes:
1. `@Transactional` on the method — single transaction for all work
2. Accumulate PoemWord entities into `wordBatch` list
3. Flush via `poemWordRepository.saveAll(wordBatch)` every 500 words
4. Final flush for remaining words
5. Poem saves unchanged (need managed entity with ID for PoemWord linkage)

```java
@Transactional
private String importFromResource(String resourcePath) {
    try {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) { return "No " + resourcePath + " found, skipping import"; }
        JsonNode root = objectMapper.readTree(is);

        Set<String> existingKeys = poemRepository.findAllKeys();
        System.out.println("Existing poems in DB: " + existingKeys.size());

        int imported = 0, skipped = 0;
        List<PoemWord> wordBatch = new ArrayList<>(500);

        for (JsonNode node : root) {
            String title = node.has("title") ? node.get("title").asText() : "无题";
            String author = node.has("author") ? node.get("author").asText() : "佚名";
            String source = node.has("source") ? node.get("source").asText() : "tang";
            String key = title + "|" + author + "|" + source;

            if (existingKeys.contains(key)) {
                skipped++;
                continue;
            }

            Poem poem = new Poem();
            poem.setTitle(title);
            poem.setAuthor(author);
            poem.setSource(source);
            poem.setDynasty(node.has("dynasty") ? node.get("dynasty").asText() : "");
            String content = node.has("content") ? node.get("content").asText() : "";
            poem.setContent(content);
            poem = poemRepository.save(poem);
            existingKeys.add(key);

            String cleanContent = content.replaceAll("[，。！？；：、\"'（）《》\\[\\]\\s]", "");
            for (int i = 0; i < cleanContent.length(); i++) {
                char c = cleanContent.charAt(i);
                if (c < 0x4E00 || c > 0x9FFF) continue;

                PoemWord pw = new PoemWord();
                pw.setPoem(poem);
                pw.setWord(String.valueOf(c));
                pw.setPosition(i);

                int start = Math.max(0, i - 5);
                int end = Math.min(cleanContent.length(), i + 6);
                pw.setContext(cleanContent.substring(start, end));

                if (i > 0) pw.setPrevWord(String.valueOf(cleanContent.charAt(i - 1)));
                if (i < cleanContent.length() - 1) pw.setNextWord(String.valueOf(cleanContent.charAt(i + 1)));

                pw.setMeaningTag(assignTags(String.valueOf(c)));
                wordBatch.add(pw);

                if (wordBatch.size() >= 500) {
                    poemWordRepository.saveAll(wordBatch);
                    wordBatch.clear();
                }
            }
            imported++;
        }

        if (!wordBatch.isEmpty()) {
            poemWordRepository.saveAll(wordBatch);
        }

        return "Import done: " + imported + " new poems, " + skipped + " skipped (already exist)";
    } catch (Exception e) {
        return "Data import failed: " + e.getMessage();
    }
}
```

- [ ] **Step 3: Remove the old `importPoem` method**

Delete the `importPoem(JsonNode, Set<String>)` method entirely — its logic is now inlined into `importFromResource`.

- [ ] **Step 4: Remove unused import**

After removing `importPoem`, `java.util.*` (line 12) is still needed. No change.

- [ ] **Step 5: Build and verify compilation**

Run: `cd backend && ./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/DataImportService.java
git commit -m "perf: batch PoemWord saves with saveAll and @Transactional for import optimization"
```
