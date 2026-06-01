package com.example.naming.service;

import com.example.naming.dto.PoemCacheItem;
import com.example.naming.entity.Poem;
import com.example.naming.repository.PoemRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ThemeMatcher {

    private static final Map<String, String> THEME_KEYWORDS = new LinkedHashMap<>();

    static {
        THEME_KEYWORDS.put("山水", "山水云溪泉峰江河海湖石谷涧涛");
        THEME_KEYWORDS.put("豪迈", "剑龙鹏虎雷霆乾坤雄壮威猛霸刚");
        THEME_KEYWORDS.put("婉约", "花柳燕莺蝶絮丝纱帘屏枕泪愁");
        THEME_KEYWORDS.put("清雅", "清雅幽素静逸闲淡远高洁兰竹梅菊");
        THEME_KEYWORDS.put("离别", "别离送归去行远望思念忆怀");
        THEME_KEYWORDS.put("田园", "田园村桑麻豆瓜耕牧渔樵锄");
        THEME_KEYWORDS.put("志向", "志道德仁义忠信诚正直贤圣君士");
        THEME_KEYWORDS.put("爱情", "情爱恋慕思念心意缘盟誓痴");
    }

    private volatile boolean built = false;
    private final Map<String, Set<Long>> index = new HashMap<>();
    private final Map<Long, String> poemSourceMap = new HashMap<>();

    /**
     * Returns the theme with the highest keyword density score for the given sentence,
     * or null if no theme matches.
     */
    public String getBestTheme(String sentence) {
        String clean = cleanPunctuation(sentence);
        if (clean.isEmpty()) return null;

        String best = null;
        double bestScore = 0;
        for (Map.Entry<String, String> entry : THEME_KEYWORDS.entrySet()) {
            int hits = 0;
            String keywords = entry.getValue();
            for (int i = 0; i < clean.length(); i++) {
                if (keywords.indexOf(clean.charAt(i)) != -1) hits++;
            }
            if (hits > 0) {
                double score = (double) hits / clean.length();
                if (score > bestScore) {
                    bestScore = score;
                    best = entry.getKey();
                }
            }
        }
        return best;
    }

    /**
     * Returns true if the best theme for the given sentence is in the provided list of themes.
     */
    public boolean matchesAny(String sentence, Collection<String> themes) {
        String best = getBestTheme(sentence);
        return best != null && themes.contains(best);
    }

    /**
     * Builds the theme index by loading all poems in batches of 500,
     * splitting each poem's content into sentences and scoring each sentence.
     */
    public void buildIndex(PoemRepository repo) {
        if (built) return;
        synchronized (this) {
            if (built) return;

            List<PoemCacheItem> cacheItems = repo.findAllCacheItems();
            index.clear();
            poemSourceMap.clear();

            for (int i = 0; i < cacheItems.size(); i += 500) {
                int end = Math.min(i + 500, cacheItems.size());
                List<PoemCacheItem> batch = cacheItems.subList(i, end);
                List<Long> ids = batch.stream().map(PoemCacheItem::id).toList();
                List<Poem> poems = repo.findAllByIdIn(ids);

                Map<Long, Poem> poemMap = new HashMap<>();
                for (Poem p : poems) {
                    poemMap.put(p.getId(), p);
                    poemSourceMap.put(p.getId(), p.getSource());
                }

                for (PoemCacheItem item : batch) {
                    Poem poem = poemMap.get(item.id());
                    if (poem == null || poem.getContent() == null) continue;

                    List<String> sentences = splitSentences(poem.getContent());
                    for (String sentence : sentences) {
                        String clean = cleanPunctuation(sentence);
                        String best = getBestTheme(clean);
                        if (best != null) {
                            index.computeIfAbsent(best, k -> new HashSet<>()).add(item.id());
                        }
                    }
                }
            }

            built = true;
        }
    }

    /**
     * Returns the union of poem IDs from the index for the given themes,
     * optionally filtered by sources.
     */
    public Set<Long> getPoemIdsForThemes(List<String> themes, List<String> sources) {
        Set<Long> result = new HashSet<>();
        for (String theme : themes) {
            Set<Long> ids = index.get(theme);
            if (ids != null) {
                result.addAll(ids);
            }
        }
        if (sources != null && !sources.isEmpty()) {
            result.removeIf(id -> {
                String source = poemSourceMap.get(id);
                return source == null || !sources.contains(source);
            });
        }
        return result;
    }

    /**
     * Splits poem content into sentences, keeping only those with 3-14 clean chars
     * (fallback to 3-18 if none found).
     */
    List<String> splitSentences(String content) {
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

    /**
     * Removes punctuation characters from a string, returning only content characters.
     */
    String cleanPunctuation(String str) {
        return str.replaceAll("[<>《》！*^()$%~!@#…&%￥—+=、。，？；：'\"`·\\[\\]]", "");
    }
}
