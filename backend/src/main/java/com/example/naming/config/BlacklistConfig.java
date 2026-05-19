package com.example.naming.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "bad")
public class BlacklistConfig {

    private String chars;

    @Value("${blacklist.file-path:./application-blacklist.yml}")
    private String filePath;

    private volatile Set<Character> badChars = new HashSet<>();

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

    // 从外部文件重新加载配置
    public void reloadConfig() {
        File file = new File(filePath);
        if (!file.exists()) {
            Set<Character> newSet = new HashSet<>();
            if (chars != null && !chars.trim().isEmpty()) {
                for (String c : chars.split(",")) {
                    if (!c.trim().isEmpty()) {
                        newSet.add(c.trim().charAt(0));
                    }
                }
            }
            this.badChars = newSet;
            return;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(in);
            Set<Character> newSet = new HashSet<>();
            if (data != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> bad = (Map<String, Object>) data.get("bad");
                if (bad != null && bad.containsKey("chars")) {
                    String charsStr = (String) bad.get("chars");
                    if (charsStr != null && !charsStr.trim().isEmpty()) {
                        for (String c : charsStr.split(",")) {
                            if (!c.trim().isEmpty()) {
                                newSet.add(c.trim().charAt(0));
                            }
                        }
                    }
                }
            }
            this.badChars = newSet;
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload blacklist from file: " + filePath, e);
        }
    }

    // 写入外部文件
    public void writeToFile(String chars) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("# 黑名单字符配置\n");
            writer.write("# 格式：bad.chars=字符1,字符2,字符3,...\n");
            writer.write("bad:\n");
            writer.write("  chars: " + chars + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write blacklist file: " + filePath, e);
        }
    }

    public boolean contains(char c) {
        return badChars.contains(c);
    }
}