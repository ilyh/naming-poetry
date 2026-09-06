package com.example.naming.service;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.dto.PoemCacheItem;
import com.example.naming.dto.BookStat;
import com.example.naming.entity.NameRecord;
import com.example.naming.entity.Poem;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.NameRecordRepository;
import com.example.naming.repository.PoemRepository;
import com.example.naming.repository.PoemWordRepository;
import com.example.naming.config.BlacklistConfig;
import com.example.naming.config.PhraseBlacklistConfig;
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
    private final PhraseBlacklistConfig phraseBlacklistConfig;
    private final Random random = new Random();

    private static final int RANDOM_POEM_SAMPLE = 300;

    private final List<PoemCacheItem> cachedPoems;

    private static final Map<String, String[]> SOURCE_META;
    private static final Map<String, String> SOURCE_NAMES;

    static {
        Map<String, String[]> meta = new LinkedHashMap<>();
        meta.put("shijing", new String[]{"诗经", "先秦风雅，适合温润清朗的名字。"});
        meta.put("chuci", new String[]{"楚辞", "瑰丽浪漫，适合大气华美的名字。"});
        meta.put("tang", new String[]{"唐诗", "意象明朗，适合开阔俊逸的名字。"});
        meta.put("song", new String[]{"宋词", "婉约含蓄，适合柔和灵秀的名字。"});
        meta.put("yuefu", new String[]{"乐府诗集", "语言生动，适合自然鲜活的名字。"});
        meta.put("gushi", new String[]{"古诗", "经典凝练，适合耐看沉静的名字。"});
        meta.put("cifu", new String[]{"著名辞赋", "铺陈华采，适合丰沛典雅的名字。"});
        meta.put("nalan", new String[]{"纳兰词", "清丽哀婉，适合深情隽秀的名字。"});
        SOURCE_META = Collections.unmodifiableMap(meta);

        Map<String, String> names = new LinkedHashMap<>();
        meta.forEach((k, v) -> names.put(k, v[0]));
        SOURCE_NAMES = Collections.unmodifiableMap(names);
    }

    
    public NameService(PoemRepository poemRepository, PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository, BlacklistConfig blacklistConfig, PhraseBlacklistConfig phraseBlacklistConfig) {
        this.poemRepository = poemRepository;
        this.poemWordRepository = poemWordRepository;
        this.nameRecordRepository = nameRecordRepository;
        this.cachedPoems = new ArrayList<>();
        this.blacklistConfig = blacklistConfig;
        this.phraseBlacklistConfig = phraseBlacklistConfig;
        loadPoemsToCache();
    }

    private void loadPoemsToCache() {
        this.cachedPoems.addAll(poemRepository.findAllCacheItems());
    }

    public List<BookStat> getBookStats() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : poemRepository.countBySource()) {
            counts.put((String) row[0], (Long) row[1]);
        }
        List<BookStat> result = new ArrayList<>();
        for (Map.Entry<String, String[]> e : SOURCE_META.entrySet()) {
            result.add(new BookStat(e.getKey(), e.getValue()[0], e.getValue()[1], counts.getOrDefault(e.getKey(), 0L)));
        }
        for (Map.Entry<String, Long> e : counts.entrySet()) {
            if (!SOURCE_META.containsKey(e.getKey())) {
                result.add(new BookStat(e.getKey(), SOURCE_NAMES.getOrDefault(e.getKey(), e.getKey()), "", e.getValue()));
            }
        }
        return result;
    }

    public GenerateResponse generateRandom(GenerateRequest req) {
        List<String> sources = req.getSources();
        List<PoemCacheItem> cacheItems = samplePoemsFromCache(sources, RANDOM_POEM_SAMPLE);
        if (cacheItems.isEmpty()) {
            return new GenerateResponse(Collections.emptyList());
        }
        List<Long> ids = cacheItems.stream().map(PoemCacheItem::id).toList();
        List<Poem> poems = poemRepository.findAllByIdIn(ids);
        return buildResponse(req, poems, null, "random");
    }

    private List<PoemCacheItem> samplePoemsFromCache(List<String> sources, int count) {
        List<PoemCacheItem> pool = sources != null && !sources.isEmpty()
            ? cachedPoems.stream().filter(p -> sources.contains(p.source())).toList()
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
            int targetLen = req.getLength() != null ? req.getLength() : 2;
            if (clean.length() < targetLen) continue;

            String givenName;
            if (keyword != null && !keyword.isEmpty()) {
                int kwIdx = clean.indexOf(keyword);
                if (kwIdx < 0) continue;
                if (targetLen == 1) {
                    givenName = keyword;
                } else {
                    int pos1 = kwIdx;
                    List<Integer> candidates = new ArrayList<>();
                    for (int i = 0; i < clean.length(); i++) {
                        if (i != pos1) candidates.add(i);
                    }
                    if (candidates.isEmpty()) continue;
                    int pos2 = candidates.get(random.nextInt(candidates.size()));
                    if (pos1 > pos2) { int tmp = pos1; pos1 = pos2; pos2 = tmp; }
                    givenName = String.valueOf(clean.charAt(pos1)) + clean.charAt(pos2);
                }
            } else if (targetLen == 1) {
                int pos = random.nextInt(clean.length());
                givenName = String.valueOf(clean.charAt(pos));
            } else {
                int pos1 = random.nextInt(clean.length());
                int pos2 = random.nextInt(clean.length());
                int guard = 0;
                while (pos2 == pos1 && guard < 50) {
                    pos2 = random.nextInt(clean.length());
                    guard++;
                }
                if (pos1 == pos2) continue;

                if (pos1 > pos2) { int tmp = pos1; pos1 = pos2; pos2 = tmp; }

                givenName = String.valueOf(clean.charAt(pos1)) + clean.charAt(pos2);
            }
            String surname = req.getSurname() != null ? req.getSurname() : "";
            String fullName = surname + givenName;

            if (phraseBlacklistConfig.contains(givenName) || phraseBlacklistConfig.contains(fullName)) {
                continue;
            }

            List<String> sources = List.of(sentence);

            GenerateResponse.NameItem item = new GenerateResponse.NameItem(
                fullName, surname, givenName, sources, null
            );
            item.setSourceNote(formatSourceNote(poem));
            item.setPoemId(poem.getId());
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
                    r.setSessionId(req.getSessionId());
                    return r;
                })
                .toList();
            nameRecordRepository.saveAll(records);
        }
        return new GenerateResponse(names);
    }

    private List<String> splitSentences(String content) {
        // 换行归一化后，直接在原文上按标点切分（不预删字符），确保结果是原文子串
        String normalized = content.replace('\n', '，');
        String[] parts = normalized.split("[，。！？；：]");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            String clean = cleanBadChars(cleanPunctuation(trimmed));
            if (clean.length() >= 3 && clean.length() <= 14) {
                result.add(trimmed);
            }
        }
        if (result.isEmpty()) {
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.isEmpty()) continue;
                String clean = cleanBadChars(cleanPunctuation(trimmed));
                if (clean.length() >= 3 && clean.length() <= 18) {
                    result.add(trimmed);
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

    public Page<NameRecord> getHistory(String sessionId, int page, int size) {
        if (sessionId != null && !sessionId.isEmpty()) {
            return nameRecordRepository.findBySessionIdOrderByCreatedAtDesc(sessionId, PageRequest.of(page, size));
        }
        return nameRecordRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public Optional<NameRecord> findById(Long id) {
        return nameRecordRepository.findById(id);
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
