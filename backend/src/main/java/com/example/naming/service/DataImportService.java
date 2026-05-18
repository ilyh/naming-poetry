package com.example.naming.service;

import com.example.naming.entity.Poem;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.PoemRepository;
import com.example.naming.repository.PoemWordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class DataImportService {

    private final PoemRepository poemRepository;
    private final PoemWordRepository poemWordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, List<String>> TAG_KEYWORDS = new LinkedHashMap<>();
    static {
        TAG_KEYWORDS.put("山水", List.of("山", "水", "云", "溪", "泉", "峰", "江", "河", "海", "湖", "石", "谷", "涧", "涛"));
        TAG_KEYWORDS.put("豪迈", List.of("剑", "龙", "鹏", "虎", "雷", "霆", "乾", "坤", "雄", "壮", "威", "猛", "霸", "刚"));
        TAG_KEYWORDS.put("婉约", List.of("花", "柳", "燕", "莺", "蝶", "絮", "丝", "纱", "帘", "屏", "枕", "泪", "愁"));
        TAG_KEYWORDS.put("清雅", List.of("清", "雅", "幽", "素", "静", "逸", "闲", "淡", "远", "高", "洁", "兰", "竹", "梅", "菊"));
        TAG_KEYWORDS.put("离别", List.of("别", "离", "送", "归", "去", "行", "远", "望", "思", "念", "忆", "怀"));
        TAG_KEYWORDS.put("田园", List.of("田", "园", "村", "桑", "麻", "豆", "瓜", "耕", "牧", "渔", "樵", "锄"));
        TAG_KEYWORDS.put("爱情", List.of("情", "爱", "恋", "慕", "思", "念", "心", "意", "缘", "盟", "誓", "痴"));
        TAG_KEYWORDS.put("志向", List.of("志", "道", "德", "仁", "义", "忠", "信", "诚", "正", "直", "贤", "圣", "君", "士"));
    }

    public DataImportService(PoemRepository poemRepository, PoemWordRepository poemWordRepository) {
        this.poemRepository = poemRepository;
        this.poemWordRepository = poemWordRepository;
    }

    public String importData() {
        return importFromResource("data/sample_poems.json");
    }

    private String importFromResource(String resourcePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) { return "No " + resourcePath + " found, skipping import"; }
            JsonNode root = objectMapper.readTree(is);

        Set<String> existingKeys = poemRepository.findAllKeys();
        System.out.println("Existing poems in DB: " + existingKeys.size());

        int imported = 0, skipped = 0;
        List<PoemWord> wordBatch = new ArrayList<>(100);

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

                if (wordBatch.size() >= 100) {
                    poemWordRepository.saveAll(wordBatch);
                    wordBatch.clear();
                    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
            }
            imported++;
            if (imported % 1000 == 0) {
                System.out.println("Progress: " + imported + " poems imported...");
            }
        }

        if (!wordBatch.isEmpty()) {
            poemWordRepository.saveAll(wordBatch);
        }

        return "Import done: " + imported + " new poems, " + skipped + " skipped (already exist)";
        } catch (IOException e) {
            throw new RuntimeException("Failed to read import data: " + e.getMessage(), e);
        }
    }

    private String assignTags(String word) {
        List<String> matched = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : TAG_KEYWORDS.entrySet()) {
            if (entry.getValue().contains(word)) {
                matched.add(entry.getKey());
            }
        }
        return matched.isEmpty() ? null : String.join(",", matched);
    }
}
