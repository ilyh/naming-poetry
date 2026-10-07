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
@ConfigurationProperties(prefix = "bad")
public class BlacklistConfig {

    private static final Logger log = LoggerFactory.getLogger(BlacklistConfig.class);

    private String chars;

    @Value("${blacklist.file-path:./application-blacklist.yml}")
    private String filePath;

    private volatile Set<Character> badChars = new HashSet<>();

    @PostConstruct
    public void init() {
        // 先用随包发布的默认值（classpath 上的 application-blacklist.yml）打底
        this.badChars = parseCsv(this.chars);
        // 外部文件才是保存链路实际写入的那份，存在就以它为准。
        // 否则热更新的结果写进了外部文件，重启却按 classpath 的旧值重建，改动全部丢失。
        try {
            Set<Character> fromFile = readExternalFile();
            if (fromFile == null) {
                log.info("字符黑名单：外部文件 {} 不存在，沿用内置默认值 {} 个", absoluteFilePath(), badChars.size());
            } else {
                this.badChars = fromFile;
                log.info("字符黑名单：已从 {} 加载 {} 个（覆盖内置默认值）", absoluteFilePath(), fromFile.size());
            }
        } catch (Exception e) {
            log.error("字符黑名单：外部文件 {} 读取失败，沿用内置默认值 {} 个", absoluteFilePath(), badChars.size(), e);
        }
    }

    public Set<Character> getBadChars() {
        return Collections.unmodifiableSet(badChars);
    }

    public void setChars(String chars) {
        this.chars = chars;
        this.badChars = parseCsv(chars);
    }

    // 从外部文件重新加载配置
    public void reloadConfig() {
        Set<Character> fromFile;
        try {
            fromFile = readExternalFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload blacklist from file: " + filePath, e);
        }
        // 文件不存在时退回内置默认值，与改动前的行为一致
        this.badChars = (fromFile != null) ? fromFile : parseCsv(this.chars);
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

    private Set<Character> parseCsv(String csv) {
        Set<Character> set = new HashSet<>();
        if (csv == null || csv.trim().isEmpty()) return set;
        for (String c : csv.split(",")) {
            if (!c.trim().isEmpty()) {
                set.add(c.trim().charAt(0));
            }
        }
        return set;
    }

    /** 读取外部配置文件；文件不存在时返回 null，由调用方决定是否回退到内置默认值。 */
    private Set<Character> readExternalFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return null;
        try (FileInputStream in = new FileInputStream(file)) {
            Map<String, Object> data = new Yaml().load(in);
            if (data == null) return new HashSet<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> bad = (Map<String, Object>) data.get("bad");
            if (bad == null) return new HashSet<>();
            return parseCsv((String) bad.get("chars"));
        }
    }

    private String absoluteFilePath() {
        return new File(filePath).getAbsolutePath();
    }
}
