# Spring AI RAG 学习项目设计文档

## 1. 项目概述

### 1.1 项目目标
创建一个基于 Spring AI 的 RAG（检索增强生成）学习项目，实现一个通用的知识库聊天机器人框架。该项目旨在帮助开发者理解 RAG 的核心概念和实现流程，同时提供一个可扩展的基础架构，支持后续添加不同领域的知识库。

### 1.2 核心功能
- **文档管理**：支持上传多种格式文档（PDF、TXT、Markdown），自动解析、分片和向量化
- **知识库管理**：通过集合（Collection）机制实现多知识库隔离，可创建、切换和删除知识库
- **智能问答**：基于 RAG 技术的对话接口，结合向量检索和大语言模型生成回答
- **对话历史**：保存用户对话记录，支持查看历史对话

### 1.3 技术选型理由
- **Spring AI**：Spring 官方 AI 集成框架，提供统一的 API 抽象
- **Ollama**：本地运行的 AI 模型服务，免费、隐私安全、适合学习
- **Milvus**：专业的向量数据库，性能优秀，有完善的 Spring AI 集成
- **Vue 3**：现代化的前端框架，与现有项目技术栈保持一致

## 2. 系统架构

### 2.1 整体架构

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Vue 3     │ ◄─────► │ Spring Boot  │ ◄─────► │   Ollama    │
│  Frontend   │  HTTP   │   Backend    │  REST   │  (LLM +     │
│             │         │              │         │  Embedding) │
└─────────────┘         └──────────────┘         └─────────────┘
                               │
                               │ Vector Operations
                               ▼
                        ┌──────────────┐
                        │   Milvus     │
                        │ (Vector DB)  │
                        └──────────────┘
```

### 2.2 后端分层架构

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
External Services (Ollama, Milvus)
```

## 3. 核心组件设计

### 3.1 文档处理流程

**文档上传与索引流程：**
1. 用户上传文档（PDF/TXT/MD）
2. 文档解析器提取文本内容
3. 文本分片器将长文本切分为合适的片段（Chunk）
4. 嵌入模型将文本片段转换为向量
5. 向量和元数据存储到 Milvus

**关键参数：**
- Chunk Size: 500-1000 字符
- Chunk Overlap: 50-100 字符
- Embedding Model: nomic-embed-text（通过 Ollama）

### 3.2 RAG 问答流程

**问答流程：**
1. 用户输入问题
2. 使用嵌入模型将问题转换为向量
3. 在 Milvus 中检索最相关的 Top-K 文档片段
4. 将检索到的片段作为上下文，与问题一起发送给 LLM
5. LLM 生成基于上下文的回答
6. 返回回答及引用的文档片段

**Prompt 模板示例：**
```
你是一个知识助手。请根据以下上下文信息回答问题。如果上下文中没有相关信息，请明确说明。

上下文：
{context}

问题：{question}

回答：
```

### 3.3 知识库管理

**集合（Collection）设计：**
- 每个知识库对应 Milvus 中的一个 Collection
- Collection 命名规范：`kb_{knowledge_base_id}`
- 元数据字段：
  - `document_id`: 文档唯一标识
  - `file_name`: 原始文件名
  - `upload_time`: 上传时间
  - `chunk_index`: 片段索引
  - `content`: 文本内容

## 4. API 设计

### 4.1 知识库管理 API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/knowledge-bases` | 创建知识库 |
| GET | `/api/knowledge-bases` | 获取知识库列表 |
| GET | `/api/knowledge-bases/{id}` | 获取知识库详情 |
| DELETE | `/api/knowledge-bases/{id}` | 删除知识库 |

### 4.2 文档管理 API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/knowledge-bases/{kbId}/documents` | 上传文档 |
| GET | `/api/knowledge-bases/{kbId}/documents` | 获取文档列表 |
| DELETE | `/api/knowledge-bases/{kbId}/documents/{docId}` | 删除文档 |

### 4.3 对话 API

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/knowledge-bases/{kbId}/chat` | 发送问题并获取回答 |
| GET | `/api/knowledge-bases/{kbId}/conversations` | 获取对话历史 |
| DELETE | `/api/knowledge-bases/{kbId}/conversations/{conversationId}` | 删除对话 |

### 4.4 请求/响应示例

**上传文档请求：**
```
POST /api/knowledge-bases/kb-001/documents
Content-Type: multipart/form-data

file: [binary data]
```

**聊天请求：**
```json
POST /api/knowledge-bases/kb-001/chat
{
  "message": "什么是 RAG？",
  "conversationId": "conv-123" // 可选，用于延续对话
}
```

**聊天响应：**
```json
{
  "conversationId": "conv-123",
  "answer": "RAG 是检索增强生成的缩写...",
  "sources": [
    {
      "documentId": "doc-456",
      "fileName": "rag-intro.pdf",
      "content": "RAG 结合了检索和生成...",
      "score": 0.92
    }
  ]
}
```

## 5. 数据模型

### 5.1 实体类设计

**KnowledgeBase（知识库）**
```java
@Entity
@Table(name = "knowledge_base")
public class KnowledgeBase {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;
    
    private String name;
    private String description;
    private String collectionName; // Milvus Collection 名称
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 关联文档
    @OneToMany(mappedBy = "knowledgeBase", cascade = CascadeType.ALL)
    private List<Document> documents;
}
```

**Document（文档）**
```java
@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;
    
    private String fileName;
    private String fileType; // pdf, txt, md
    private Long fileSize;
    private String filePath; // 存储路径
    
    @ManyToOne
    @JoinColumn(name = "knowledge_base_id")
    private KnowledgeBase knowledgeBase;
    
    private Integer chunkCount; // 分片数量
    
    @CreatedDate
    private LocalDateTime uploadedAt;
    
    private ProcessingStatus status; // PENDING, PROCESSING, COMPLETED, FAILED
}
```

**Conversation（对话）**
```java
@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "knowledge_base_id")
    private KnowledgeBase knowledgeBase;
    
    private String title; // 对话标题（自动生成）
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<Message> messages;
}
```

**Message（消息）**
```java
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    private MessageType type; // USER, ASSISTANT
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String sources; // JSON 格式的引用来源
    
    @CreatedDate
    private LocalDateTime createdAt;
}
```

## 6. 配置设计

### 6.1 application.yml 配置项

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: llama3.2
      embedding:
        model: nomic-embed-text
    
    milvus:
      connection:
        host: localhost
        port: 19530
      
      vector-store:
        initialize-schema: true
        collection-name-prefix: kb_
        dimensions: 768  # nomic-embed-text 的维度

app:
  rag:
    chunk-size: 800
    chunk-overlap: 100
    top-k: 5  # 检索的最相关文档数量
    similarity-threshold: 0.7  # 相似度阈值
  
  file:
    upload-dir: ./uploads
    max-size: 10MB
    allowed-types: .pdf,.txt,.md
```

### 6.2 Docker Compose 配置

```yaml
version: '3.8'

services:
  milvus-standalone:
    image: milvusdb/milvus:v2.4.0
    container_name: milvus-standalone
    ports:
      - "19530:19530"
      - "9091:9091"
    environment:
      ETCD_USE_EMBED: true
      ETCD_DATA_DIR: /var/lib/milvus/etcd
    volumes:
      - milvus-data:/var/lib/milvus
    command: ["milvus", "run", "standalone"]

volumes:
  milvus-data:
```

## 7. 前端设计

### 7.1 页面结构

**主要页面：**
1. **首页/聊天页** (`/`)
   - 知识库选择器
   - 聊天界面（消息列表 + 输入框）
   - 引用来源展示

2. **知识库管理页** (`/knowledge-bases`)
   - 知识库列表
   - 创建/编辑知识库
   - 文档上传与管理

3. **对话历史页** (`/history`)
   - 历史对话列表
   - 查看/删除对话

### 7.2 核心组件

- `KnowledgeBaseSelector`: 知识库下拉选择器
- `ChatInterface`: 聊天界面（消息气泡、输入框）
- `DocumentUploader`: 文档上传组件（拖拽 + 进度条）
- `SourceViewer`: 引用来源展示面板
- `ConversationList`: 对话历史列表

### 7.3 状态管理

使用 Vue 3 Composition API + Pinia（可选）管理：
- 当前选中的知识库
- 当前对话 ID
- 消息列表
- 加载状态

## 8. 错误处理

### 8.1 常见错误场景

1. **Ollama 服务未启动**
   - 检测连接状态
   - 返回友好错误提示
   
2. **Milvus 连接失败**
   - 重试机制
   - 降级处理

3. **文档解析失败**
   - 记录错误日志
   - 更新文档状态为 FAILED
   - 返回具体错误信息

4. **向量检索无结果**
   - 返回提示："未在知识库中找到相关信息"
   - 建议用户检查问题或添加更多文档

### 8.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<ErrorResponse> handleDocumentProcessingException(
            DocumentProcessingException ex) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("DOCUMENT_PROCESSING_ERROR", ex.getMessage()));
    }
    
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIServiceException(
            AIServiceException ex) {
        return ResponseEntity.status(503)
            .body(new ErrorResponse("AI_SERVICE_UNAVAILABLE", ex.getMessage()));
    }
}
```

## 9. 测试策略

### 9.1 单元测试

- 文档分片逻辑测试
- Prompt 构建测试
- 服务层业务逻辑测试

### 9.2 集成测试

- 文档上传完整流程测试
- RAG 问答流程测试
- Milvus 向量检索测试

### 9.3 手动测试场景

1. 上传不同类型的文档（PDF、TXT、MD）
2. 创建多个知识库并切换
3. 提问并验证回答质量
4. 查看引用来源的准确性
5. 测试边界情况（空知识库、无关问题等）

## 10. 部署与运行

### 10.1 前置要求

1. **安装 Ollama**
   ```bash
   # Windows
   # 从 https://ollama.com/download 下载安装
   
   # 拉取模型
   ollama pull llama3.2
   ollama pull nomic-embed-text
   ```

2. **启动 Milvus**
   ```bash
   docker-compose up -d
   ```

3. **Java 17+**
4. **Node.js 18+**

### 10.2 启动步骤

**后端：**
```bash
cd backend
mvn spring-boot:run
```

**前端：**
```bash
cd frontend
npm install
npm run dev
```

### 10.3 验证安装

1. 访问 http://localhost:5173
2. 创建一个新的知识库
3. 上传一个测试文档
4. 提问并验证回答

## 11. 扩展方向

### 11.1 短期扩展

- 支持更多文档格式（Word、Excel）
- 添加文档预览功能
- 实现流式响应（SSE）
- 添加评分反馈机制

### 11.2 长期扩展

- 支持多模态（图片、音频）
- 实现混合检索（关键词 + 向量）
- 添加权限管理（多用户）
- 集成更多 AI 模型提供商
- 实现增量索引（文档更新时只重新索引变化的部分）

## 12. 学习要点

通过这个项目，你将学习到：

1. **RAG 核心概念**
   - 为什么需要 RAG（解决 LLM 幻觉、知识时效性问题）
   - RAG 的工作流程和关键组件

2. **向量数据库**
   - 向量嵌入的原理
   - 相似度搜索算法
   - Milvus 的基本使用

3. **Spring AI 框架**
   - ChatClient 的使用
   - VectorStore 的集成
   - DocumentReader 和 TextSplitter

4. **工程实践**
   - 异步文档处理
   - 错误处理和重试机制
   - API 设计规范

## 13. 风险与挑战

### 13.1 技术风险

- **Ollama 模型质量**：本地模型可能不如云端模型准确
  - 缓解：提供模型切换选项，允许使用其他模型
  
- **Milvus 资源占用**：Milvus  standalone 模式占用较多内存
  - 缓解：配置合理的资源限制，考虑使用轻量级替代方案（如 Chroma）

- **文档解析复杂度**：PDF 解析可能遇到格式问题
  - 缓解：使用成熟的解析库（Apache PDFBox、Tika），提供清晰的错误提示

### 13.2 学习曲线

- Spring AI 相对较新，文档可能不够完善
  - 缓解：参考官方示例代码，查阅 GitHub Issues

## 14. 成功标准

项目成功的标志：

1. ✅ 能够成功上传文档并建立索引
2. ✅ 能够基于文档内容进行准确问答
3. ✅ 能够创建和管理多个知识库
4. ✅ 前端界面友好，操作流程清晰
5. ✅ 代码结构清晰，易于理解和扩展
6. ✅ 有完整的 README 和使用文档

---

**文档版本**: v1.0  
**创建日期**: 2026-05-19  
**作者**: AI Assistant
