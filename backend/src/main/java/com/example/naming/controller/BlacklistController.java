package com.example.naming.controller;

import com.example.naming.config.BlacklistConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class BlacklistController {

    @Autowired
    private BlacklistConfig blacklistConfig;

    @GetMapping("/blacklist")
    public Map<String, Object> getBlacklist() {
        Set<Character> chars = blacklistConfig.getBadChars();
        String charsStr = chars.stream().map(String::valueOf).collect(Collectors.joining(","));
        return Map.of(
            "blacklistSize", chars.size(),
            "characters", charsStr
        );
    }

    @PostMapping("/blacklist")
    public Map<String, Object> updateBlacklist(@RequestBody Map<String, String> body) {
        String chars = body.get("chars");
        if (chars == null || chars.trim().isEmpty()) {
            return Map.of("status", "error", "message", "chars 不能为空");
        }
        try {
            blacklistConfig.writeToFile(chars);
            blacklistConfig.reloadConfig();
            Set<Character> updated = blacklistConfig.getBadChars();
            String charsStr = updated.stream().map(String::valueOf).collect(Collectors.joining(","));
            return Map.of(
                "status", "success",
                "message", "黑名单已更新",
                "blacklistSize", updated.size(),
                "characters", charsStr
            );
        } catch (Exception e) {
            return Map.of("status", "error", "message", "更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/blacklist/reload")
    public Map<String, Object> reloadBlacklist() {
        try {
            blacklistConfig.reloadConfig();
            Set<Character> updated = blacklistConfig.getBadChars();
            String charsStr = updated.stream().map(String::valueOf).collect(Collectors.joining(","));
            return Map.of(
                "status", "success",
                "message", "黑名单重新加载成功",
                "blacklistSize", updated.size(),
                "characters", charsStr
            );
        } catch (Exception e) {
            return Map.of("status", "error", "message", "重新加载失败: " + e.getMessage());
        }
    }
}