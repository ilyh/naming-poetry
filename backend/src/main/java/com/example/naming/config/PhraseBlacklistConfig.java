package com.example.naming.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PhraseBlacklistConfig.class);

    private String phrases;

    @Value("${phrase-blacklist.file-path:./application-phrase-blacklist.yml}")
    private String filePath;

    private volatile Set<String> badPhrases = new HashSet<>();

    @PostConstruct
    public void init() {
        // 先用随包发布的默认值（classpath 上的 application-phrase-blacklist.yml）打底
        this.badPhrases = parseCsv(this.phrases);
        // 外部文件才是保存链路实际写入的那份，存在就以它为准。
        // 否则热更新的结果写进了外部文件，重启却按 classpath 的旧值重建，改动全部丢失。
        try {
            Set<String> fromFile = readExternalFile();
            if (fromFile == null) {
                log.info("词组黑名单：外部文件 {} 不存在，沿用内置默认值 {} 条", absoluteFilePath(), badPhrases.size());
            } else {
                this.badPhrases = fromFile;
                log.info("词组黑名单：已从 {} 加载 {} 条（覆盖内置默认值）", absoluteFilePath(), fromFile.size());
            }
        } catch (Exception e) {
            log.error("词组黑名单：外部文件 {} 读取失败，沿用内置默认值 {} 条", absoluteFilePath(), badPhrases.size(), e);
        }
    }

    public Set<String> getBadPhrases() {
        return Collections.unmodifiableSet(badPhrases);
    }

    public void setPhrases(String phrases) {
        this.phrases = phrases;
        this.badPhrases = parseCsv(phrases);
    }

    public void reloadConfig() {
        Set<String> fromFile;
        try {
            fromFile = readExternalFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload phrase blacklist from file: " + filePath, e);
        }
        // 文件不存在时退回内置默认值，与改动前的行为一致
        this.badPhrases = (fromFile != null) ? fromFile : parseCsv(this.phrases);
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

    private Set<String> parseCsv(String csv) {
        Set<String> set = new HashSet<>();
        if (csv == null || csv.trim().isEmpty()) return set;
        for (String p : csv.split(",")) {
            String t = p.trim();
            if (!t.isEmpty()) {
                set.add(t);
            }
        }
        return set;
    }

    /** 读取外部配置文件；文件不存在时返回 null，由调用方决定是否回退到内置默认值。 */
    private Set<String> readExternalFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return null;
        try (FileInputStream in = new FileInputStream(file)) {
            Map<String, Object> data = new Yaml().load(in);
            if (data == null) return new HashSet<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> bad = (Map<String, Object>) data.get("bad-phrase");
            if (bad == null) return new HashSet<>();
            return parseCsv((String) bad.get("phrases"));
        }
    }

    private String absoluteFilePath() {
        return new File(filePath).getAbsolutePath();
    }
}
