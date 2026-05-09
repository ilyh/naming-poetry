package com.example.naming.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "bad")
public class BlacklistConfig {

    private List<String> chars;

    private Set<Character> badChars = new HashSet<>();

    @PostConstruct
    public void init() {
        if (!CollectionUtils.isEmpty(chars)) {
            for (String c : chars) {
                if (!c.trim().isEmpty()) {
                    badChars.add(c.trim().charAt(0));
                }
            }
        }
    }

    public Set<Character> getBadChars() {
        return Collections.unmodifiableSet(badChars);
    }

    public List<String> getChars() {
        return chars;
    }

    public void setChars(List<String> chars) {
        this.chars = chars;
        // 重新加载
        badChars.clear();
        init();
    }

    // 手动重新加载配置
    public void reloadConfig() {
        badChars.clear();
        init();
    }

    public boolean contains(char c) {
        return badChars.contains(c);
    }
}