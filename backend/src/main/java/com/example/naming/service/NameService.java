package com.example.naming.service;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.entity.Poem;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.NameRecordRepository;
import com.example.naming.repository.PoemRepository;
import com.example.naming.repository.PoemWordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NameService {

    private final PoemRepository poemRepository;
    private final PoemWordRepository poemWordRepository;
    private final NameRecordRepository nameRecordRepository;
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

    private static final Set<Character> BAD_CHARS = new HashSet<>(Arrays.asList(
        '不','丧','乱','亏','亵','仃','优','伶','佞','俘','倡','偷','冢','凄','凋','凶',
        '刑','勿','半','单','卜','叛','只','吊','否','咽','哀','哽','囚','坟','垒','堠',
        '墓','墙','夭','奠','奸','妒','妓','妖','妾','媚','媟','嫉','孑','孤','寂','寇',
        '寞','寥','巫','废','弗','徂','怅','怨','恨','恫','恸','悲','悼','惘','惨','愁',
        '戍','戎','戚','戮','放','无','晏','暴','更','未','朽','枭','枯','柝','桧','棘',
        '欠','殂','殃','殇','残','殒','殓','殡','毋','氓','汩','泣','泪','淫','溘','漏',
        '灭','灰','灾','烬','烽','燧','狂','狄','狡','独','狼','玄','疚','疠','疢','痌',
        '痗','痛','瘁','瘥','瘴','砧','碑','碣','祀','祭','祸','祼','禘','稗','窃','窜',
        '筮','素','缟','缺','羌','芜','苦','茨','荆','莠','莫','落','蒿','虱','蚊','蚓',
        '蚤','蛆','蜮','蝇','蝎','螂','蟆','觋','谄','谗','谪','豺','败','贬','贼','赭',
        '蹇','辕','迁','逐','逝','邅','非','驿','魅','魉','魍','魑','鸱','鹑','黔','老',
        '胸','鬼','懒','禽','鸟','鸡','我','邪','罪','凶','丑','仇','鼠','蟋','蟀','淫',
        '秽','妹','狐','鸡','鸭','蝇','悔','鱼','肉','苦','犬','吠','窥','血','丧','饥',
        '女','搔','父','母','昏','狗','蟊','疾','病','痛','死','潦','哀','痒','害','蛇',
        '牲','妇','狸','鹅','穴','畜','烂','兽','靡','爪','氓','劫','鬣','螽','毛','婚',
        '姻','匪','婆','羞','辱','蝱','悸','薨','谑','麕','怒','浇','憔','怕','呆','鞋',
        '孕','尸','駉','谮','罴','娈'
    ));

    public NameService(PoemRepository poemRepository, PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository) {
        this.poemRepository = poemRepository;
        this.poemWordRepository = poemWordRepository;
        this.nameRecordRepository = nameRecordRepository;
        this.allPoemIds = new ArrayList<>();
        this.cachedPoems = new ArrayList<>();
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
