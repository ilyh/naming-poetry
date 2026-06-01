# 诗词取名小程序 — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a web app that generates Chinese given names from classical poetry (Tang, Song, Shijing, Chuci), with three modes: random, keyword-filtered, and theme-based.

**Architecture:** Vue 3 SPA frontend communicates via REST JSON to a SpringBoot 3.x backend. MySQL stores poem metadata and character-level indices. Name generation queries the `poem_word` table with various filters, assembles candidate characters, and returns results with poetic source attribution.

**Tech Stack:** Vue 3 + Vite + Tailwind CSS + Axios | SpringBoot 3.x + JPA/Hibernate | MySQL 8.x

---

### Task 1: Initialize SpringBoot backend project

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/example/naming/NamingApplication.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: Create pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>naming-poetry</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>naming-poetry</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create NamingApplication.java**

```java
package com.example.naming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NamingApplication {
    public static void main(String[] args) {
        SpringApplication.run(NamingApplication.class, args);
    }
}
```

- [ ] **Step 3: Create application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/naming_poetry?useUnicode=true&characterEncoding=utf-8&createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

- [ ] **Step 4: Verify backend compiles**

```bash
cd backend && mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/pom.xml backend/src/main/java/com/example/naming/NamingApplication.java backend/src/main/resources/application.yml
git commit -m "feat: initialize SpringBoot backend project"
```

---

### Task 2: Initialize Vue 3 frontend project

**Files:**
- Create: frontend project via `npm create vite`

- [ ] **Step 1: Scaffold Vue 3 + Vite project**

```bash
cd /home/gxn/project/naming-poetry
npm create vite@latest frontend -- --template vue
```

- [ ] **Step 2: Install dependencies**

```bash
cd frontend && npm install && npm install axios tailwindcss @tailwindcss/vite
```

- [ ] **Step 3: Configure Tailwind in vite.config.js**

Create/edit `frontend/vite.config.js`:
```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

- [ ] **Step 4: Add Tailwind directives**

Create `frontend/src/style.css`:
```css
@import "tailwindcss";
```

- [ ] **Step 5: Verify frontend starts**

```bash
cd frontend && npm run dev
```
Expected: dev server starts on http://localhost:5173

- [ ] **Step 6: Commit**

```bash
git add frontend/
git commit -m "feat: initialize Vue 3 + Vite + Tailwind frontend project"
```

---

### Task 3: Create JPA entities

**Files:**
- Create: `backend/src/main/java/com/example/naming/entity/Poem.java`
- Create: `backend/src/main/java/com/example/naming/entity/PoemWord.java`
- Create: `backend/src/main/java/com/example/naming/entity/NameRecord.java`

- [ ] **Step 1: Create Poem.java**

```java
package com.example.naming.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poem")
public class Poem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String dynasty;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Poem() {}

    public Poem(String title, String author, String source, String content, String dynasty) {
        this.title = title;
        this.author = author;
        this.source = source;
        this.content = content;
        this.dynasty = dynasty;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDynasty() { return dynasty; }
    public void setDynasty(String dynasty) { this.dynasty = dynasty; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

- [ ] **Step 2: Create PoemWord.java**

```java
package com.example.naming.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "poem_word", indexes = {
    @Index(name = "idx_word", columnList = "word"),
    @Index(name = "idx_meaning_tag", columnList = "meaningTag"),
    @Index(name = "idx_poem_id", columnList = "poem_id")
})
public class PoemWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poem_id", nullable = false)
    private Poem poem;

    @Column(nullable = false, length = 1)
    private String word;

    @Column(nullable = false)
    private Integer position;

    @Column(length = 50)
    private String context;

    @Column(length = 1)
    private String prevWord;

    @Column(length = 1)
    private String nextWord;

    @Column(length = 50)
    private String meaningTag;

    public PoemWord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Poem getPoem() { return poem; }
    public void setPoem(Poem poem) { this.poem = poem; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getPrevWord() { return prevWord; }
    public void setPrevWord(String prevWord) { this.prevWord = prevWord; }
    public String getNextWord() { return nextWord; }
    public void setNextWord(String nextWord) { this.nextWord = nextWord; }
    public String getMeaningTag() { return meaningTag; }
    public void setMeaningTag(String meaningTag) { this.meaningTag = meaningTag; }
}
```

- [ ] **Step 3: Create NameRecord.java**

```java
package com.example.naming.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "name_record")
public class NameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1)
    private String surname;

    @Column(name = "given_name", nullable = false, length = 4)
    private String givenName;

    @Column(name = "full_name", nullable = false, length = 5)
    private String fullName;

    @Column(name = "source1_id")
    private Long source1Id;

    @Column(name = "source2_id")
    private Long source2Id;

    @Column(name = "source3_id")
    private Long source3Id;

    @Column(length = 20)
    private String mode;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public NameRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getSource1Id() { return source1Id; }
    public void setSource1Id(Long source1Id) { this.source1Id = source1Id; }
    public Long getSource2Id() { return source2Id; }
    public void setSource2Id(Long source2Id) { this.source2Id = source2Id; }
    public Long getSource3Id() { return source3Id; }
    public void setSource3Id(Long source3Id) { this.source3Id = source3Id; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

- [ ] **Step 4: Verify compilation**

```bash
cd backend && mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/example/naming/entity/
git commit -m "feat: add JPA entities (Poem, PoemWord, NameRecord)"
```

---

### Task 4: Create JPA repositories

**Files:**
- Create: `backend/src/main/java/com/example/naming/repository/PoemRepository.java`
- Create: `backend/src/main/java/com/example/naming/repository/PoemWordRepository.java`
- Create: `backend/src/main/java/com/example/naming/repository/NameRecordRepository.java`

- [ ] **Step 1: Create PoemRepository.java**

```java
package com.example.naming.repository;

import com.example.naming.entity.Poem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PoemRepository extends JpaRepository<Poem, Long> {
    List<Poem> findBySource(String source);
}
```

- [ ] **Step 2: Create PoemWordRepository.java**

```java
package com.example.naming.repository;

import com.example.naming.entity.PoemWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PoemWordRepository extends JpaRepository<PoemWord, Long> {

    List<PoemWord> findByWord(String word);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.meaningTag IN :tags")
    List<PoemWord> findByMeaningTags(@Param("tags") List<String> tags);

    @Query(value = "SELECT * FROM poem_word ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<PoemWord> findRandom(@Param("limit") int limit);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE p.source IN :sources")
    List<PoemWord> findBySources(@Param("sources") List<String> sources);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.word = :word AND p.source IN :sources")
    List<PoemWord> findByWordAndSources(@Param("word") String word, @Param("sources") List<String> sources);

    @Query("SELECT pw FROM PoemWord pw JOIN pw.poem p WHERE pw.meaningTag IN :tags AND p.source IN :sources")
    List<PoemWord> findByMeaningTagsAndSources(@Param("tags") List<String> tags, @Param("sources") List<String> sources);
}
```

- [ ] **Step 3: Create NameRecordRepository.java**

```java
package com.example.naming.repository;

import com.example.naming.entity.NameRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NameRecordRepository extends JpaRepository<NameRecord, Long> {
    Page<NameRecord> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
```

- [ ] **Step 4: Verify compilation**

```bash
cd backend && mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/example/naming/repository/
git commit -m "feat: add JPA repositories"
```

---

### Task 5: Create DTOs and NameService (core generation logic)

**Files:**
- Create: `backend/src/main/java/com/example/naming/dto/GenerateRequest.java`
- Create: `backend/src/main/java/com/example/naming/dto/GenerateResponse.java`
- Create: `backend/src/main/java/com/example/naming/service/NameService.java`

- [ ] **Step 1: Create GenerateRequest.java**

```java
package com.example.naming.dto;

import java.util.List;

public class GenerateRequest {

    private String surname;
    private String keyword;
    private List<String> themes;
    private List<String> sources;
    private Integer count = 5;
    private Integer length = 2;

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }
    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }
}
```

- [ ] **Step 2: Create GenerateResponse.java**

```java
package com.example.naming.dto;

import java.util.List;

public class GenerateResponse {
    private List<NameItem> names;

    public GenerateResponse(List<NameItem> names) { this.names = names; }
    public List<NameItem> getNames() { return names; }
    public void setNames(List<NameItem> names) { this.names = names; }

    public static class NameItem {
        private String text;
        private String surname;
        private String givenName;
        private String source;
        private List<String> themes;

        public NameItem(String text, String surname, String givenName, String source, List<String> themes) {
            this.text = text;
            this.surname = surname;
            this.givenName = givenName;
            this.source = source;
            this.themes = themes;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public String getGivenName() { return givenName; }
        public void setGivenName(String givenName) { this.givenName = givenName; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public List<String> getThemes() { return themes; }
        public void setThemes(List<String> themes) { this.themes = themes; }
    }
}
```

- [ ] **Step 3: Create NameService.java**

```java
package com.example.naming.service;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.NameRecordRepository;
import com.example.naming.repository.PoemWordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NameService {

    private final PoemWordRepository poemWordRepository;
    private final NameRecordRepository nameRecordRepository;
    private final Random random = new Random();

    public NameService(PoemWordRepository poemWordRepository, NameRecordRepository nameRecordRepository) {
        this.poemWordRepository = poemWordRepository;
        this.nameRecordRepository = nameRecordRepository;
    }

    public GenerateResponse generateRandom(GenerateRequest req) {
        List<PoemWord> candidates = getCandidates(req.getSources());
        return buildResponse(req, candidates);
    }

    public GenerateResponse generateByKeyword(GenerateRequest req) {
        String keyword = req.getKeyword();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByWordAndSources(keyword, req.getSources());
        } else {
            candidates = poemWordRepository.findByWord(keyword);
        }
        return buildResponse(req, candidates);
    }

    public GenerateResponse generateByTheme(GenerateRequest req) {
        List<String> themes = req.getThemes();
        List<PoemWord> candidates;
        if (req.getSources() != null && !req.getSources().isEmpty()) {
            candidates = poemWordRepository.findByMeaningTagsAndSources(themes, req.getSources());
        } else {
            candidates = poemWordRepository.findByMeaningTags(themes);
        }
        return buildResponse(req, candidates);
    }

    private List<PoemWord> getCandidates(List<String> sources) {
        if (sources != null && !sources.isEmpty()) {
            return poemWordRepository.findBySources(sources);
        }
        return poemWordRepository.findAll();
    }

    private GenerateResponse buildResponse(GenerateRequest req, List<PoemWord> candidates) {
        if (candidates.isEmpty()) {
            return new GenerateResponse(Collections.emptyList());
        }

        List<GenerateResponse.NameItem> names = new ArrayList<>();
        int maxAttempts = req.getCount() * 10;
        int attempts = 0;

        while (names.size() < req.getCount() && attempts < maxAttempts) {
            attempts++;
            StringBuilder givenName = new StringBuilder();
            List<String> sources = new ArrayList<>();
            Set<Long> usedPoemIds = new HashSet<>();

            for (int i = 0; i < req.getLength(); i++) {
                PoemWord pw = candidates.get(random.nextInt(candidates.size()));
                if (usedPoemIds.contains(pw.getPoem().getId())) continue;
                usedPoemIds.add(pw.getPoem().getId());
                givenName.append(pw.getWord());
                if (pw.getPoem() != null && pw.getPoem().getContent() != null) {
                    sources.add(pw.getPoem().getContent());
                }
            }

            if (givenName.length() != req.getLength()) continue;

            String surname = req.getSurname() != null ? req.getSurname() : "";
            String fullName = surname + givenName.toString();
            String sourceStr = sources.isEmpty() ? "" : sources.get(0);

            GenerateResponse.NameItem item = new GenerateResponse.NameItem(
                fullName, surname, givenName.toString(), sourceStr, null
            );
            names.add(item);

            NameRecord record = new NameRecord();
            record.setSurname(surname);
            record.setGivenName(givenName.toString());
            record.setFullName(fullName);
            record.setMode("random");
            nameRecordRepository.save(record);
        }

        return new GenerateResponse(names);
    }

    public Page<NameRecord> getHistory(int page, int size) {
        return nameRecordRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }
}
```

- [ ] **Step 4: Verify compilation**

```bash
cd backend && mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/example/naming/dto/ backend/src/main/java/com/example/naming/service/
git commit -m "feat: add DTOs and NameService with generation logic"
```

---

### Task 6: Create REST controller and CORS config

**Files:**
- Create: `backend/src/main/java/com/example/naming/controller/NameController.java`
- Create: `backend/src/main/java/com/example/naming/config/CorsConfig.java`

- [ ] **Step 1: Create NameController.java**

```java
package com.example.naming.controller;

import com.example.naming.dto.GenerateRequest;
import com.example.naming.dto.GenerateResponse;
import com.example.naming.entity.NameRecord;
import com.example.naming.service.NameService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/name")
public class NameController {

    private final NameService nameService;

    public NameController(NameService nameService) {
        this.nameService = nameService;
    }

    @PostMapping("/random")
    public ResponseEntity<GenerateResponse> random(@RequestBody GenerateRequest req) {
        return ResponseEntity.ok(nameService.generateRandom(req));
    }

    @PostMapping("/keyword")
    public ResponseEntity<GenerateResponse> keyword(@RequestBody GenerateRequest req) {
        return ResponseEntity.ok(nameService.generateByKeyword(req));
    }

    @PostMapping("/theme")
    public ResponseEntity<GenerateResponse> theme(@RequestBody GenerateRequest req) {
        return ResponseEntity.ok(nameService.generateByTheme(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NameRecord> detail(@PathVariable Long id) {
        return nameService.getHistory(0, 1).getContent().stream()
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<NameRecord>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(nameService.getHistory(page, size));
    }
}
```

- [ ] **Step 2: Create CorsConfig.java**

```java
package com.example.naming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
```

- [ ] **Step 3: Verify compilation**

```bash
cd backend && mvn compile -q
```
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/example/naming/controller/ backend/src/main/java/com/example/naming/config/
git commit -m "feat: add REST controller and CORS config"
```

---

### Task 7: Create data import service and seed data

**Files:**
- Create: `backend/src/main/java/com/example/naming/service/DataImportService.java`
- Create: `backend/src/main/resources/data/sample_poems.json`

- [ ] **Step 1: Create DataImportService.java**

```java
package com.example.naming.service;

import com.example.naming.entity.Poem;
import com.example.naming.entity.PoemWord;
import com.example.naming.repository.PoemRepository;
import com.example.naming.repository.PoemWordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class DataImportService {

    private final PoemRepository poemRepository;
    private final PoemWordRepository poemWordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, List<String>> TAG_KEYWORDS = new LinkedHashMap<>();
    static {
        TAG_KEYWORDS.put("山水", List.of("山", "水", "云", "溪", "泉", "峰", "江", "河", "海", "湖", "石", "谷", "涧", "涛"));
        TAG_KEYWORDS.put("豪迈", List.of("剑", "龙", "鹏", "虎", "雷", "霆", "乾", "坤", "雄", "壮", "威", "猛", "霸", "刚"));
        TAG_KEYWORDS.put("婉约", List.of("花", "柳", "燕", "莺", "蝶", "絮", "丝", "纱", "帘", "屏", "枕", "泪", "愁"));
        TAG_KEYWORDS.put("清雅", List.of("清", "雅", "幽", "素", "静", "逸", "闲", "淡", "远", "高", "洁", "兰", "竹", "梅", "菊"));
        TAG_KEYWORDS.put("离别", List.of("别", "离", "送", "归", "去", "行", "远", "望", "思", "念", "忆", "怀"));
        TAG_KEYWORDS.put("田园", List.of("田", "园", "村", "桑", "麻", "豆", "瓜", "耕", "牧", "渔", "樵", "锄"));
        TAG_KEYWORDS.put("爱情", List.of("情", "爱", "恋", "慕", "思", "念", "心", "意", "缘", "盟", "誓", "痴"));
        TAG_KEYWORDS.put("志向", List.of("志", "道", "德", "仁", "义", "忠", "信", "诚", "正", "直", "贤", "圣", "君", "士"));
    }

    public DataImportService(PoemRepository poemRepository, PoemWordRepository poemWordRepository) {
        this.poemRepository = poemRepository;
        this.poemWordRepository = poemWordRepository;
    }

    @PostConstruct
    public void importOnStartup() {
        if (poemRepository.count() > 0) return;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("data/sample_poems.json");
            if (is == null) { System.out.println("No sample_poems.json found, skipping import"); return; }
            JsonNode root = objectMapper.readTree(is);
            for (JsonNode node : root) {
                importPoem(node);
            }
            System.out.println("Data import complete: " + poemRepository.count() + " poems");
        } catch (Exception e) {
            System.err.println("Data import failed: " + e.getMessage());
        }
    }

    private void importPoem(JsonNode node) {
        Poem poem = new Poem();
        poem.setTitle(node.has("title") ? node.get("title").asText() : "无题");
        poem.setAuthor(node.has("author") ? node.get("author").asText() : "佚名");
        poem.setSource(node.has("source") ? node.get("source").asText() : "tang");
        poem.setDynasty(node.has("dynasty") ? node.get("dynasty").asText() : "");
        String content = node.has("content") ? node.get("content").asText() : "";
        poem.setContent(content);
        poem = poemRepository.save(poem);

        String cleanContent = content.replaceAll("[，。！？；：、\"'（）《》\\[\\]\\s]", "");
        for (int i = 0; i < cleanContent.length(); i++) {
            char c = cleanContent.charAt(i);
            if (c < 0x4E00 || c > 0x9FFF) continue;

            PoemWord pw = new PoemWord();
            pw.setPoem(poem);
            pw.setWord(String.valueOf(c));
            pw.setPosition(i);

            int start = Math.max(0, i - 5);
            int end = Math.min(cleanContent.length(), i + 6);
            pw.setContext(cleanContent.substring(start, end));

            if (i > 0) pw.setPrevWord(String.valueOf(cleanContent.charAt(i - 1)));
            if (i < cleanContent.length() - 1) pw.setNextWord(String.valueOf(cleanContent.charAt(i + 1)));

            pw.setMeaningTag(assignTags(String.valueOf(c)));
            poemWordRepository.save(pw);
        }
    }

    private String assignTags(String word) {
        List<String> matched = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : TAG_KEYWORDS.entrySet()) {
            if (entry.getValue().contains(word)) {
                matched.add(entry.getKey());
            }
        }
        return matched.isEmpty() ? null : String.join(",", matched);
    }
}
```

- [ ] **Step 2: Create sample_poems.json**

```json
[
  {"title": "静夜思", "author": "李白", "source": "tang", "dynasty": "唐", "content": "床前明月光，疑是地上霜。举头望明月，低头思故乡。"},
  {"title": "登鹳雀楼", "author": "王之涣", "source": "tang", "dynasty": "唐", "content": "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。"},
  {"title": "春晓", "author": "孟浩然", "source": "tang", "dynasty": "唐", "content": "春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。"},
  {"title": "江雪", "author": "柳宗元", "source": "tang", "dynasty": "唐", "content": "千山鸟飞绝，万径人踪灭。孤舟蓑笠翁，独钓寒江雪。"},
  {"title": "相思", "author": "王维", "source": "tang", "dynasty": "唐", "content": "红豆生南国，春来发几枝。愿君多采撷，此物最相思。"},
  {"title": "望庐山瀑布", "author": "李白", "source": "tang", "dynasty": "唐", "content": "日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。"},
  {"title": "山居秋暝", "author": "王维", "source": "tang", "dynasty": "唐", "content": "空山新雨后，天气晚来秋。明月松间照，清泉石上流。"},
  {"title": "送元二使安西", "author": "王维", "source": "tang", "dynasty": "唐", "content": "渭城朝雨浥轻尘，客舍青青柳色新。劝君更尽一杯酒，西出阳关无故人。"},
  {"title": "关雎", "author": "佚名", "source": "shijing", "dynasty": "先秦", "content": "关关雎鸠，在河之洲。窈窕淑女，君子好逑。"},
  {"title": "蒹葭", "author": "佚名", "source": "shijing", "dynasty": "先秦", "content": "蒹葭苍苍，白露为霜。所谓伊人，在水一方。"},
  {"title": "离骚（节选）", "author": "屈原", "source": "chuci", "dynasty": "先秦", "content": "长太息以掩涕兮，哀民生之多艰。余虽好修姱以鞿羁兮，謇朝谇而夕替。"},
  {"title": "水调歌头", "author": "苏轼", "source": "song", "dynasty": "宋", "content": "明月几时有，把酒问青天。不知天上宫阙，今夕是何年。"},
  {"title": "念奴娇·赤壁怀古", "author": "苏轼", "source": "song", "dynasty": "宋", "content": "大江东去，浪淘尽，千古风流人物。故垒西边，人道是，三国周郎赤壁。"},
  {"title": "声声慢", "author": "李清照", "source": "song", "dynasty": "宋", "content": "寻寻觅觅，冷冷清清，凄凄惨惨戚戚。乍暖还寒时候，最难将息。"},
  {"title": "青玉案·元夕", "author": "辛弃疾", "source": "song", "dynasty": "宋", "content": "东风夜放花千树，更吹落，星如雨。宝马雕车香满路。"},
  {"title": "雨霖铃", "author": "柳永", "source": "song", "dynasty": "宋", "content": "寒蝉凄切，对长亭晚，骤雨初歇。都门帐饮无绪，留恋处，兰舟催发。"},
  {"title": "将进酒", "author": "李白", "source": "tang", "dynasty": "唐", "content": "君不见黄河之水天上来，奔流到海不复回。君不见高堂明镜悲白发，朝如青丝暮成雪。"},
  {"title": "枫桥夜泊", "author": "张继", "source": "tang", "dynasty": "唐", "content": "月落乌啼霜满天，江枫渔火对愁眠。姑苏城外寒山寺，夜半钟声到客船。"},
  {"title": "鹿柴", "author": "王维", "source": "tang", "dynasty": "唐", "content": "空山不见人，但闻人语响。返景入深林，复照青苔上。"},
  {"title": "竹里馆", "author": "王维", "source": "tang", "dynasty": "唐", "content": "独坐幽篁里，弹琴复长啸。深林人不知，明月来相照。"}
]
```

- [ ] **Step 3: Verify backend compiles and starts**

```bash
cd backend && mvn compile -q && mvn spring-boot:run
```
Expected: application starts, data import runs on first startup

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/example/naming/service/DataImportService.java backend/src/main/resources/data/
git commit -m "feat: add data import service with sample poems"
```

---

### Task 8: Build Vue frontend — App shell and shared components

**Files:**
- Modify: `frontend/src/App.vue`
- Create: `frontend/src/components/NavBar.vue`
- Create: `frontend/src/components/SurnameInput.vue`
- Create: `frontend/src/components/LengthSelector.vue`
- Create: `frontend/src/components/NameTabs.vue`
- Create: `frontend/src/api/index.js`

- [ ] **Step 1: Create API layer**

```js
// frontend/src/api/index.js
import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

export function generateRandom(params) {
  return api.post('/name/random', params)
}

export function generateKeyword(params) {
  return api.post('/name/keyword', params)
}

export function generateTheme(params) {
  return api.post('/name/theme', params)
}

export function getHistory(page = 0, size = 20) {
  return api.get('/name/history', { params: { page, size } })
}
```

- [ ] **Step 2: Create NavBar.vue**

```vue
<template>
  <header class="bg-stone-900 text-stone-100 py-4 px-6 flex items-center justify-between">
    <h1 class="text-xl font-bold tracking-widest">诗词取名</h1>
    <button @click="$emit('toggle-history')" class="text-sm text-stone-400 hover:text-stone-200 transition">
      历史
    </button>
  </header>
</template>

<script setup>
defineEmits(['toggle-history'])
</script>
```

- [ ] **Step 3: Create SurnameInput.vue**

```vue
<template>
  <div class="flex items-center gap-3">
    <label class="text-stone-600 text-sm whitespace-nowrap">姓氏</label>
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      maxlength="1"
      placeholder="李"
      class="w-16 text-center text-lg border-b-2 border-stone-300 focus:border-amber-500 outline-none py-1 bg-transparent"
    />
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: '' } })
defineEmits(['update:modelValue'])
</script>
```

- [ ] **Step 4: Create LengthSelector.vue**

```vue
<template>
  <div class="flex items-center gap-3">
    <span class="text-stone-600 text-sm">名字字数</span>
    <button
      v-for="n in [1, 2]"
      :key="n"
      @click="$emit('update:modelValue', n)"
      :class="[
        'px-3 py-1 rounded text-sm transition',
        modelValue === n ? 'bg-amber-600 text-white' : 'bg-stone-200 text-stone-600 hover:bg-stone-300'
      ]"
    >{{ n }}字</button>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: Number, default: 2 } })
defineEmits(['update:modelValue'])
</script>
```

- [ ] **Step 5: Create NameTabs.vue**

```vue
<template>
  <div class="flex border-b border-stone-200">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      @click="$emit('update:modelValue', tab.key)"
      :class="[
        'px-4 py-2 text-sm transition border-b-2 -mb-[2px]',
        modelValue === tab.key
          ? 'border-amber-600 text-amber-700 font-medium'
          : 'border-transparent text-stone-500 hover:text-stone-700'
      ]"
    >{{ tab.label }}</button>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: 'random' } })
defineEmits(['update:modelValue'])

const tabs = [
  { key: 'random', label: '随机生成' },
  { key: 'keyword', label: '关键词筛选' },
  { key: 'theme', label: '主题意境' },
]
</script>
```

- [ ] **Step 6: Replace App.vue**

```vue
<template>
  <div class="min-h-screen bg-stone-50">
    <NavBar @toggle-history="showHistory = !showHistory" />
    <main class="max-w-2xl mx-auto px-4 py-8">
      <!-- Global inputs -->
      <div class="flex items-center gap-8 mb-6">
        <SurnameInput v-model="surname" />
        <LengthSelector v-model="nameLength" />
      </div>

      <!-- Tabs -->
      <NameTabs v-model="activeTab" />

      <!-- Tab panels -->
      <div class="mt-6">
        <RandomPanel
          v-if="activeTab === 'random'"
          :surname="surname"
          :length="nameLength"
          :sources="selectedSources"
        />
        <KeywordPanel
          v-if="activeTab === 'keyword'"
          :surname="surname"
          :length="nameLength"
          :sources="selectedSources"
        />
        <ThemePanel
          v-if="activeTab === 'theme'"
          :surname="surname"
          :length="nameLength"
          :sources="selectedSources"
        />
      </div>
    </main>

    <!-- History drawer (simplified as modal) -->
    <HistoryDrawer v-if="showHistory" @close="showHistory = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import SurnameInput from './components/SurnameInput.vue'
import LengthSelector from './components/LengthSelector.vue'
import NameTabs from './components/NameTabs.vue'
import RandomPanel from './components/RandomPanel.vue'
import KeywordPanel from './components/KeywordPanel.vue'
import ThemePanel from './components/ThemePanel.vue'
import HistoryDrawer from './components/HistoryDrawer.vue'

const surname = ref('李')
const nameLength = ref(2)
const activeTab = ref('random')
const showHistory = ref(false)
const selectedSources = ref([])
</script>
```

- [ ] **Step 7: Verify frontend compiles**

```bash
cd frontend && npm run dev
```
Expected: dev server starts without errors (panels will be empty for now)

- [ ] **Step 8: Commit**

```bash
git add frontend/src/
git commit -m "feat: add App shell, NavBar, SurnameInput, LengthSelector, NameTabs, API layer"
```

---

### Task 9: Build Vue frontend — NameCard and mode panels

**Files:**
- Create: `frontend/src/components/NameCard.vue`
- Create: `frontend/src/components/NameDetailModal.vue`
- Create: `frontend/src/components/HistoryDrawer.vue`
- Create: `frontend/src/components/RandomPanel.vue`
- Create: `frontend/src/components/KeywordPanel.vue`
- Create: `frontend/src/components/ThemePanel.vue`

- [ ] **Step 1: Create NameCard.vue**

```vue
<template>
  <div class="bg-white rounded-lg shadow-sm border border-stone-200 p-4 text-center hover:shadow-md transition">
    <div class="text-2xl mb-1">
      <span class="text-stone-500 text-base">{{ name.surname }}</span>
      <span class="text-stone-900 font-bold tracking-wide">{{ name.givenName }}</span>
    </div>
    <div class="text-xs text-stone-400 truncate mb-2" :title="name.source">{{ name.source || '—' }}</div>
    <button @click="$emit('detail', name)" class="text-xs text-amber-600 hover:text-amber-800 transition">
      溯源
    </button>
  </div>
</template>

<script setup>
defineProps({ name: { type: Object, required: true } })
defineEmits(['detail'])
</script>
```

- [ ] **Step 2: Create NameDetailModal.vue**

```vue
<template>
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-white rounded-xl shadow-xl max-w-md w-full mx-4 p-6">
      <h3 class="text-xl font-bold text-stone-800 mb-4">
        {{ name.surname }}<span class="text-amber-700">{{ name.givenName }}</span>
      </h3>
      <div class="text-sm text-stone-500 mb-4">
        出处：{{ name.source || '未知' }}
      </div>
      <div class="flex gap-2 mb-4">
        <span
          v-for="char in name.givenName.split('')"
          :key="char"
          class="w-10 h-10 rounded-full bg-amber-100 text-amber-800 flex items-center justify-center text-lg font-bold"
        >{{ char }}</span>
      </div>
      <button @click="$emit('close')" class="w-full py-2 bg-stone-100 rounded-lg text-sm text-stone-600 hover:bg-stone-200 transition">
        关闭
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({ name: { type: Object, required: true } })
defineEmits(['close'])
</script>
```

- [ ] **Step 3: Create HistoryDrawer.vue**

```vue
<template>
  <div class="fixed inset-0 bg-black/40 flex justify-end z-50" @click.self="$emit('close')">
    <div class="bg-white w-80 h-full overflow-y-auto shadow-xl p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="font-bold text-stone-800">历史记录</h3>
        <button @click="$emit('close')" class="text-stone-400 hover:text-stone-600 text-lg">&times;</button>
      </div>
      <div v-if="records.length === 0" class="text-sm text-stone-400 text-center py-8">暂无记录</div>
      <div v-for="r in records" :key="r.id" class="py-2 border-b border-stone-100 text-sm">
        <span class="text-stone-500">{{ r.surname }}</span>
        <span class="font-medium text-stone-800">{{ r.givenName }}</span>
        <span class="text-stone-400 ml-2 text-xs">{{ r.mode }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHistory } from '../api/index.js'

defineEmits(['close'])

const records = ref([])

onMounted(async () => {
  try {
    const { data } = await getHistory()
    records.value = data.content || []
  } catch (e) {
    console.error('Failed to load history', e)
  }
})
</script>
```

- [ ] **Step 4: Create RandomPanel.vue**

```vue
<template>
  <div>
    <div class="flex justify-center mb-6">
      <button @click="generate" :disabled="loading"
        class="px-8 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 transition">
        {{ loading ? '生成中...' : '生成名字' }}
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" @detail="detailName = name" />
    </div>
    <div v-else class="text-center text-stone-400 py-12">点击上方按钮开始生成</div>
    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateRandom } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const names = ref([])
const loading = ref(false)
const detailName = ref(null)

async function generate() {
  loading.value = true
  try {
    const { data } = await generateRandom({
      surname: props.surname,
      count: 6,
      length: props.length,
      sources: props.sources.length > 0 ? props.sources : null
    })
    names.value = data.names || []
  } catch (e) {
    console.error('Generate failed', e)
  } finally {
    loading.value = false
  }
}
</script>
```

- [ ] **Step 5: Create KeywordPanel.vue**

```vue
<template>
  <div>
    <div class="flex justify-center gap-4 mb-6">
      <input v-model="keyword" maxlength="1" placeholder="输入偏好字，如：清"
        class="w-24 text-center border-b-2 border-stone-300 focus:border-amber-500 outline-none py-1 bg-transparent text-lg" />
      <button @click="generate" :disabled="loading || !keyword"
        class="px-6 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 transition">
        {{ loading ? '生成中...' : '生成名字' }}
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" @detail="detailName = name" />
    </div>
    <div v-else class="text-center text-stone-400 py-12">输入一个字，点击生成</div>
    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateKeyword } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const keyword = ref('')
const names = ref([])
const loading = ref(false)
const detailName = ref(null)

async function generate() {
  if (!keyword.value) return
  loading.value = true
  try {
    const { data } = await generateKeyword({
      surname: props.surname,
      keyword: keyword.value,
      count: 6,
      length: props.length,
      sources: props.sources.length > 0 ? props.sources : null
    })
    names.value = data.names || []
  } catch (e) {
    console.error('Generate failed', e)
  } finally {
    loading.value = false
  }
}
</script>
```

- [ ] **Step 6: Create ThemePanel.vue**

```vue
<template>
  <div>
    <div class="flex flex-wrap justify-center gap-2 mb-6">
      <button
        v-for="theme in allThemes"
        :key="theme"
        @click="toggleTheme(theme)"
        :class="[
          'px-3 py-1 rounded-full text-sm transition',
          selectedThemes.includes(theme)
            ? 'bg-amber-600 text-white'
            : 'bg-stone-200 text-stone-600 hover:bg-stone-300'
        ]"
      >{{ theme }}</button>
    </div>
    <div class="flex justify-center mb-6">
      <button @click="generate" :disabled="loading || selectedThemes.length === 0"
        class="px-8 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 transition">
        {{ loading ? '生成中...' : '生成名字' }}
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" @detail="detailName = name" />
    </div>
    <div v-else class="text-center text-stone-400 py-12">选择意境标签，点击生成</div>
    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateTheme } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const allThemes = ['山水', '豪迈', '婉约', '清雅', '离别', '田园', '爱情', '志向']
const selectedThemes = ref([])
const names = ref([])
const loading = ref(false)
const detailName = ref(null)

function toggleTheme(theme) {
  const idx = selectedThemes.value.indexOf(theme)
  if (idx >= 0) selectedThemes.value.splice(idx, 1)
  else selectedThemes.value.push(theme)
}

async function generate() {
  if (selectedThemes.value.length === 0) return
  loading.value = true
  try {
    const { data } = await generateTheme({
      surname: props.surname,
      themes: selectedThemes.value,
      count: 6,
      length: props.length,
      sources: props.sources.length > 0 ? props.sources : null
    })
    names.value = data.names || []
  } catch (e) {
    console.error('Generate failed', e)
  } finally {
    loading.value = false
  }
}
</script>
```

- [ ] **Step 7: Verify full build**

```bash
cd frontend && npm run build
```
Expected: BUILD SUCCESS (no errors)

- [ ] **Step 8: Commit**

```bash
git add frontend/src/components/
git commit -m "feat: add NameCard, panels (random/keyword/theme), modal, history drawer"
```

---

### Task 10: Integration test and polish

- [ ] **Step 1: Start MySQL if not running**

```bash
mysql -u root -proot -e "SELECT 1" 2>/dev/null || echo "MySQL not running, start it first"
```

- [ ] **Step 2: Start backend**

```bash
cd backend && mvn spring-boot:run &
```
Wait for "Data import complete" log message.

- [ ] **Step 3: Start frontend**

```bash
cd frontend && npm run dev &
```

- [ ] **Step 4: Verify end-to-end flow**
  - Open http://localhost:5173
  - Surname input defaults to "李"
  - Click "生成名字" on Random tab → 6 name cards appear
  - Switch to Keyword tab → type "清" → click generate → names appear
  - Switch to Theme tab → select "山水" → click generate → names appear
  - Click "溯源" on any name → modal shows with source poem
  - Click "历史" → drawer shows previous generations

- [ ] **Step 5: Commit any final fixes**

```bash
git add -A && git commit -m "chore: final polish and integration fixes"
```
