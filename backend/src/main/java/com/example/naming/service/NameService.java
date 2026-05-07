package com.example.naming.service;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.entity.Poem;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.NameRecordRepository;
import com.example.naming.repository.PoemWordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NameService {

    private final PoemWordRepository poemWordRepository;
    private final NameRecordRepository nameRecordRepository;
    private final Random random = new Random();

    private static final Map<String, String> SOURCE_NAMES = Map.of(
        "shijing", "诗经",
        "chuci", "楚辞",
        "tang", "唐诗",
        "song", "宋词",
        "yuefu", "乐府诗集",
        "gushi", "古诗",
        "cifu", "著名辞赋"
    );

    private static final Set<Character> BAD_CHARS = new HashSet<>(Arrays.asList(
        '胸','鬼','懒','禽','鸟','鸡','我','邪','罪','凶','丑','仇','鼠','蟋','蟀','淫',
        '秽','妹','狐','鸡','鸭','蝇','悔','鱼','肉','苦','犬','吠','窥','血','丧','饥',
        '女','搔','父','母','昏','狗','蟊','疾','病','痛','死','潦','哀','痒','害','蛇',
        '牲','妇','狸','鹅','穴','畜','烂','兽','靡','爪','氓','劫','鬣','螽','毛','婚',
        '姻','匪','婆','羞','辱'
    ));

    public NameService(PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository) {
        this.poemWordRepository = poemWordRepository;
        this.nameRecordRepository = nameRecordRepository;
    }

    public GenerateResponse generateRandom(GenerateRequest req) {
        List<PoemWord> candidates = getCandidates(req.getSources());
        return buildResponse(req, candidates, null);
    }

    public GenerateResponse generateByKeyword(GenerateRequest req) {
        String keyword = req.getKeyword();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByWordAndSources(keyword, req.getSources());
        } else {
            candidates = poemWordRepository.findByWord(keyword);
        }
        return buildResponse(req, candidates, keyword);
    }

    public GenerateResponse generateByTheme(GenerateRequest req) {
        List<String> themes = req.getThemes();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByMeaningTagsAndSources(themes, req.getSources());
        } else {
            candidates = poemWordRepository.findByMeaningTags(themes);
        }
        return buildResponse(req, candidates, null);
    }

    private List<PoemWord> getCandidates(List<String> sources) {
        if (sources != null && !sources.isEmpty()) {
            return poemWordRepository.findBySources(sources);
        }
        return poemWordRepository.findAll();
    }

    private GenerateResponse buildResponse(GenerateRequest req, List<PoemWord> candidates, String keyword) {
        if (candidates.isEmpty()) {
            return new GenerateResponse(Collections.emptyList());
        }

        Map<Long, Poem> poemMap = new LinkedHashMap<>();
        for (PoemWord pw : candidates) {
            poemMap.putIfAbsent(pw.getPoem().getId(), pw.getPoem());
        }
        List<Poem> poems = new ArrayList<>(poemMap.values());

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

            NameRecord record = new NameRecord();
            record.setSurname(surname);
            record.setGivenName(givenName);
            record.setFullName(fullName);
            record.setMode("random");
            nameRecordRepository.save(record);
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
            if (!BAD_CHARS.contains(c) && c >= 0x4E00 && c <= 0x9FFF) {
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
