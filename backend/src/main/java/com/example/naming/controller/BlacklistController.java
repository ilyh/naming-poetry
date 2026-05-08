package com.example.naming.controller;

import com.example.naming.config.BlacklistConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class BlacklistController {

    @Autowired
    private BlacklistConfig blacklistConfig;

    @GetMapping("/blacklist")
    public Map<String, Object> getBlacklist() {
        Set<Character> chars = blacklistConfig.getBadChars();
        return Map.of(
            "blacklistSize", chars.size(),
            "characters", chars.toString()
        );
    }

    @PostMapping("/blacklist/reload")
    public Map<String, String> reloadBlacklist() {
        try {
            blacklistConfig.reloadConfig();
            return Map.of("status", "success", "message", "黑名单重新加载成功");
        } catch (Exception e) {
            return Map.of("status", "error", "message", "重新加载失败: " + e.getMessage());
        }
    }
}