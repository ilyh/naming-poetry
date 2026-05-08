package com.example.naming.service;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.entity.Poem;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.NameRecordRepository;
import com.example.naming.repository.PoemRepository;
import com.example.naming.repository.PoemWordRepository;
import com.example.naming.config.BlacklistConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NameService {

    private final PoemRepository poemRepository;
    private final PoemWordRepository poemWordRepository;
    private final NameRecordRepository nameRecordRepository;
    private final BlacklistConfig blacklistConfig;
    private final Random random = new Random();

    private static final int RANDOM_POEM_SAMPLE = 300;

    private final List<Poem> cachedPoems;
    private final List<Long> allPoemIds;

    private static final Map<String, String> SOURCE_NAMES = Map.of(
        "shijing", "诗经",
        "chuci", "楚辞",
        "tang", "唐诗",
        "song", "宋词",
        "yuefu", "乐府诗集",
        "gushi", "古诗",
        "cifu", "著名辞赋"
    );

    
    public NameService(PoemRepository poemRepository, PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository, BlacklistConfig blacklistConfig) {
        this.poemRepository = poemRepository;
        this.poemWordRepository = poemWordRepository;
        this.nameRecordRepository = nameRecordRepository;
        this.allPoemIds = new ArrayList<>();
        this.cachedPoems = new ArrayList<>();
        this.blacklistConfig = blacklistConfig;
        loadPoemsToCache();
    }

    private void loadPoemsToCache() {
        List<Poem> all = poemRepository.findAll();
        this.cachedPoems.addAll(all);
        for (Poem p : all) {
            this.allPoemIds.add(p.getId());
        }
    }

    public GenerateResponse generateRandom(GenerateRequest req) {
        List<String> sources = req.getSources();
        List<Poem> poems = samplePoemsFromCache(sources, RANDOM_POEM_SAMPLE);
        return buildResponse(req, poems, null, "random");
    }

    private List<Poem> samplePoemsFromCache(List<String> sources, int count) {
        List<Poem> pool = sources != null && !sources.isEmpty()
            ? cachedPoems.stream().filter(p -> sources.contains(p.getSource())).toList()
            : cachedPoems;
        if (pool.isEmpty()) return List.of();
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < Math.min(count, pool.size())) {
            indices.add(random.nextInt(pool.size()));
        }
        return indices.stream().map(pool::get).toList();
    }

    public GenerateResponse generateByKeyword(GenerateRequest req) {
        String keyword = req.getKeyword();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByWordAndSources(keyword, req.getSources());
        } else {
            candidates = poemWordRepository.findByWord(keyword);
        }
        List<Poem> poems = extractPoems(candidates);
        return buildResponse(req, poems, keyword, "keyword");
    }

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

    private List<Poem> extractPoems(List<PoemWord> words) {
        Map<Long, Poem> map = new LinkedHashMap<>();
        for (PoemWord pw : words) {
            map.putIfAbsent(pw.getPoem().getId(), pw.getPoem());
        }
        return new ArrayList<>(map.values());
    }

    private GenerateResponse buildResponse(GenerateRequest req, List<Poem> poems, String keyword, String mode) {
        if (poems.isEmpty()) {
            return new GenerateResponse(Collections.emptyList());
        }

        List<GenerateResponse.NameItem> names = new ArrayList<>();
        int maxAttempts = req.getCount() * 30;
        int attempts = 0;

        while (names.size() < req.getCount() && attempts < maxAttempts) {
            attempts++;

            Poem poem = poems.get(random.nextInt(poems.size()));
            String content = poem.getContent();
            if (content == null || content.isEmpty()) continue;

            List<String> sentences = splitSentences(content);
            if (sentences.isEmpty()) continue;

            List<String> pool = sentences;
            if (keyword != null && !keyword.isEmpty()) {
                List<String> filtered = new ArrayList<>();
                for (String s : sentences) {
                    if (s.contains(keyword)) filtered.add(s);
                }
                if (!filtered.isEmpty()) pool = filtered;
            }

            String sentence = pool.get(random.nextInt(pool.size()));
            String clean = cleanBadChars(cleanPunctuation(sentence));
            if (clean.length() < 2) continue;

            int pos1 = random.nextInt(clean.length());
            int pos2 = random.nextInt(clean.length());
            int guard = 0;
            while (pos2 == pos1 && guard < 50) {
                pos2 = random.nextInt(clean.length());
                guard++;
            }
            if (pos1 == pos2) continue;

            if (pos1 > pos2) { int tmp = pos1; pos1 = pos2; pos2 = tmp; }

            String givenName = String.valueOf(clean.charAt(pos1)) + clean.charAt(pos2);
            String surname = req.getSurname() != null ? req.getSurname() : "";
            String fullName = surname + givenName;

            List<String> sources = List.of(sentence);

            GenerateResponse.NameItem item = new GenerateResponse.NameItem(
                fullName, surname, givenName, sources, null
            );
            item.setSourceNote(formatSourceNote(poem));
            names.add(item);
        }

        if (!names.isEmpty()) {
            List<NameRecord> records = names.stream()
                .map(n -> {
                    NameRecord r = new NameRecord();
                    r.setSurname(n.getSurname());
                    r.setGivenName(n.getGivenName());
                    r.setFullName(n.getText());
                    r.setMode(mode);
                    return r;
                })
                .toList();
            nameRecordRepository.saveAll(records);
        }
        return new GenerateResponse(names);
    }

    private List<String> splitSentences(String content) {
        String str = content.replaceAll("[\\s　\"'（）《》\\[\\]<>brp/]", "");
        String[] parts = str.split("[，。！？；：]");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String clean = cleanBadChars(cleanPunctuation(part));
            if (clean.length() >= 3 && clean.length() <= 14) {
                result.add(part.trim());
            }
        }
        if (result.isEmpty()) {
            for (String part : parts) {
                String clean = cleanBadChars(cleanPunctuation(part));
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

    private String cleanBadChars(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!blacklistConfig.contains(c) && c >= 0x4E00 && c <= 0x9FFF) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public Page<NameRecord> getHistory(int page, int size) {
        return nameRecordRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    private String formatSourceNote(Poem poem) {
        String sourceName = SOURCE_NAMES.getOrDefault(poem.getSource(), poem.getSource());
        String note = sourceName + "·" + poem.getTitle();
        if (poem.getAuthor() != null && !poem.getAuthor().isEmpty()) {
            note += " 作者 " + poem.getAuthor();
        }
        return note;
    }
}
