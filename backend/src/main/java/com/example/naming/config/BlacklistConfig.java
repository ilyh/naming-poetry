package com.example.naming.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "bad")
public class BlacklistConfig {

    private String chars;

    private Set<Character> badChars = new HashSet<>();

    @PostConstruct
    public void init() {
        if (chars != null && !chars.trim().isEmpty()) {
            for (String c : chars.split(",")) {
                if (!c.trim().isEmpty()) {
                    badChars.add(c.trim().charAt(0));
                }
            }
        }
    }

    public Set<Character> getBadChars() {
        return Collections.unmodifiableSet(badChars);
    }

    public void setChars(String chars) {
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