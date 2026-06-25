package com.example.naming.controller;

import com.example.naming.config.PhraseBlacklistConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class PhraseBlacklistController {

    @Autowired
    private PhraseBlacklistConfig phraseBlacklistConfig;

    @GetMapping("/phrase-blacklist")
    public Map<String, Object> getPhraseBlacklist() {
        Set<String> phrases = phraseBlacklistConfig.getBadPhrases();
        String phrasesStr = phrases.stream().collect(Collectors.joining(","));
        return Map.of(
            "blacklistSize", phrases.size(),
            "phrases", phrasesStr
        );
    }

    @PostMapping("/phrase-blacklist")
    public Map<String, Object> updatePhraseBlacklist(@RequestBody Map<String, String> body) {
        String phrases = body.get("phrases");
        if (phrases == null) {
            return Map.of("status", "error", "message", "phrases 不能为空");
        }
        try {
            phraseBlacklistConfig.writeToFile(phrases);
            phraseBlacklistConfig.reloadConfig();
            Set<String> updated = phraseBlacklistConfig.getBadPhrases();
            String phrasesStr = updated.stream().collect(Collectors.joining(","));
            return Map.of(
                "status", "success",
                "message", "词组黑名单已更新",
                "blacklistSize", updated.size(),
                "phrases", phrasesStr
            );
        } catch (Exception e) {
            return Map.of("status", "error", "message", "更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/phrase-blacklist/reload")
    public Map<String, Object> reloadPhraseBlacklist() {
        try {
            phraseBlacklistConfig.reloadConfig();
            Set<String> updated = phraseBlacklistConfig.getBadPhrases();
            String phrasesStr = updated.stream().collect(Collectors.joining(","));
            return Map.of(
                "status", "success",
                "message", "词组黑名单重新加载成功",
                "blacklistSize", updated.size(),
                "phrases", phrasesStr
            );
        } catch (Exception e) {
            return Map.of("status", "error", "message", "重新加载失败: " + e.getMessage());
        }
    }
}
