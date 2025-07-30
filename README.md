# Omni-Agent

> 基于 Spring AI 打造的企业级智能体开发框架，支持表单智能体、报表智能体等多种 AI Agent 场景，具备强感知、可编排、可拓展等特性。

---

## ᵀᵄ 构架总览

Omni-Agent 采用“感知 - 决策 - 行动”的智能体架构，分为三个核心模块：

```
┌────────────────────────────────────────────┌
│ omni-agent-service     │ ← 感知层：接收事件输入
└────────────────────────────────┘
            ↓
┌────────────────────────────────┌
│ omni-agent-core        │ ← Agent核心：处理逻辑编排、上下文、记忆、图执行
└────────────────────────────────┘
            ↓
┌────────────────────────────────┌
│ omni-agent-mcp         │ ← 外界交互层：通过 Tool 与外部系统通信
└────────────────────────────────┘
```

---

## 📆 项目模块

### omni-agent-core

智能体核心，负责 Agent 组织、流程编排、上下文传递、记忆管理

### omni-agent-mcp

外部能力集成层，将外部调用装装为 Tool ，可扩展、可配置

### omni-agent-service

感知层，接受事件，转换为 Agent 执行输入，并返回结果

---

## 🚀 快速开始

```bash
git clone https://github.com/your-org/omni-agent.git
cd omni-agent
cd omni-agent-service
./mvnw spring-boot:run
```

默认端口：`http://localhost:8080`

---

## 🔧 扩展方式

### 新增 Agent

* 实现 `Agent` 接口
* 配置 prompt 模板 & Graph 流程

### 新增 Tool

* 实现 `Tool` 接口
* 通过 MCP 注册成功
* 可在 Graph 中使用 Tool

---

## 📃 实例场景

| 智能体名称       | 功能说明        |
| ----------- | ----------- |
| FormAgent   | 引导用户填写并提交表单 |
| ReportAgent | 查询报表数据并生成摘要 |

---

## 📊 技术栏

* Java 17
* Spring Boot 3.x
* Spring AI (Alibaba/OpenAI)
* WebFlux + SSE
* Redis / MySQL

---

## 🚧 TODO

* [ ] Tool 挂载 SPI
* [ ] 规则培育 & 身份校验
* [ ] 图形化流程编排
* [ ] Agent 配置化创建

---

## 📄 License

MIT © 重庆北睿星科技有限公司
