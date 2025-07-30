# 🧠 omni-agent-java

> 一个以人类认知结构为灵感构建的多能力智能体框架，基于 Spring AI Alibaba 实现，通过配置驱动的方式，引导用户完成复杂的表单与任务处理流程。

---

## 🧬 项目目标

`omni-agent-java` 致力于构建一个**模块化、可配置、可扩展**的智能 Agent 系统，通过自然语言驱动用户与业务表单交互，适配企业中的各类自动化需求。

---

## 🧠 类人化架构设计

我们将整个 Agent 系统比喻为一个具有智能行为的“虚拟人”体系结构：

| 模块角色         | 模块名称             | 职责说明 |
|------------------|----------------------|----------|
| 🧠 大脑           | `omni-agent-core`     | 承载智能体的核心逻辑，如 Graph、Memory、状态机、引导流程等 |
| 🗣 命令中心       | `omni-agent-prompt`   | 管理所有 Prompt、语言模板、任务配置、表单字段定义等 |
| 🦾 四肢行动器     | `omni-agent-mcp`      | 执行访问外部世界的能力，如文件读取、RAG、知识库、API 调用等 |
| 🧰 工具辅助       | `omni-agent-tools`    | 提供调试工具、通用函数、模拟器等辅助开发组件 |
| 🗨 接口感官系统   | `omni-agent-service`  | 提供 HTTP / WebSocket 接口，供前端/用户系统集成使用 |

---

## 📦 模块结构

```text
omni-agent-java/
├── omni-agent-core       // 智能体大脑：Agent/Graph/Memory 等
├── omni-agent-prompt     // Prompt 配置、字段定义、话术模板
├── omni-agent-mcp        // 外部连接器（MCP）：文件、知识库、RAG 等
├── omni-agent-tools      // 日志、调试、测试辅助类
├── omni-agent-service    // 对外服务接口（REST、WebSocket）
└── README.md
```

---

## 💡 应用示例

> 用户输入：「我想明天请假」

系统自动完成以下流程：

1. **路由识别** → 提取意图并定位请假表单
2. **智能引导** → 引导用户补全开始时间、结束时间、请假类型等字段
3. **表单填报** → 支持自然语言解释、确认、修改字段
4. **表单提交** → 调用业务系统 API 完成落库

---

## 🔧 特性支持

- ✅ **基于配置的 Prompt / 表单模板**
- ✅ **多类型表单支持（通用/行业）**
- ✅ **智能字段引导与上下文记忆**
- ✅ **接入企业内部知识或外部文档（MCP）**
- ✅ **可插拔工具链、组件分离设计**
- ✅ **支持 REST/WebSocket 双接口模式**

---

## 🚀 技术栈

- Java 17+
- Spring Boot 3.x
- Spring AI Alibaba
- Spring AI MCP（File, RAG, VectorStore 等）
- Maven 多模块管理
- JSON/YAML 配置式 Prompt 模型

---

## 📌 使用方式

1. 克隆项目并导入 IDE
2. 统一构建所有模块：

```bash
./mvnw clean install -DskipTests
```

3. 启动 `omni-agent-service` 模块：

```bash
cd omni-agent-service
./mvnw spring-boot:run
```

---

## 📚 后续规划

- [ ] Agent DSL 编排引擎
- [ ] 表单学习功能（自动生成 Prompt）
- [ ] 企业知识库同步工具
- [ ] 支持模型切换（Qwen / Spark / ChatGLM）

---

## 👥 贡献者

- **主设计者**：xia yu
- **核心结构设计**：大脑 + 命令 + 四肢 + 感官 五大模块
- **GitHub地址**：_待补充_
