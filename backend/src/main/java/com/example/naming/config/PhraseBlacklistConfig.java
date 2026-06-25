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
@ConfigurationProperties(prefix = "bad-phrase")
public class PhraseBlacklistConfig {

    private String phrases;

    @Value("${phrase-blacklist.file-path:./application-phrase-blacklist.yml}")
    private String filePath;

    private volatile Set<String> badPhrases = new HashSet<>();

    @PostConstruct
    public void init() {
        if (phrases != null && !phrases.trim().isEmpty()) {
            for (String p : phrases.split(",")) {
                String t = p.trim();
                if (!t.isEmpty()) {
                    badPhrases.add(t);
                }
            }
        }
    }

    public Set<String> getBadPhrases() {
        return Collections.unmodifiableSet(badPhrases);
    }

    public void setPhrases(String phrases) {
        this.phrases = phrases;
        badPhrases.clear();
        init();
    }

    public void reloadConfig() {
        File file = new File(filePath);
        if (!file.exists()) {
            Set<String> newSet = new HashSet<>();
            if (phrases != null && !phrases.trim().isEmpty()) {
                for (String p : phrases.split(",")) {
                    String t = p.trim();
                    if (!t.isEmpty()) {
                        newSet.add(t);
                    }
                }
            }
            this.badPhrases = newSet;
            return;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(in);
            Set<String> newSet = new HashSet<>();
            if (data != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> bad = (Map<String, Object>) data.get("bad-phrase");
                if (bad != null && bad.containsKey("phrases")) {
                    String phrasesStr = (String) bad.get("phrases");
                    if (phrasesStr != null && !phrasesStr.trim().isEmpty()) {
                        for (String p : phrasesStr.split(",")) {
                            String t = p.trim();
                            if (!t.isEmpty()) {
                                newSet.add(t);
                            }
                        }
                    }
                }
            }
            this.badPhrases = newSet;
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload phrase blacklist from file: " + filePath, e);
        }
    }

    public void writeToFile(String phrases) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("# 词组黑名单配置\n");
            writer.write("# 格式：bad-phrase.phrases=词组1,词组2,词组3,...\n");
            writer.write("# 用于过滤不适合作为人名的多字组合，单字仍可与其他字组合出好名字\n");
            writer.write("bad-phrase:\n");
            writer.write("  phrases: " + phrases + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write phrase blacklist file: " + filePath, e);
        }
    }

    public boolean contains(String phrase) {
        if (phrase == null || phrase.isEmpty()) return false;
        return badPhrases.contains(phrase);
    }
}
