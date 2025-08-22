# Omni-Agent

> Omni-Agent 是一个基于 Spring AI 构建的企业级智能体开发框架，面向表单处理、报表分析等典型业务场景，具备强感知、上下文建模、流程驱动、工具可插拔等能力，支持开发可组合、可编排的专业智能体。

---

## 🧠 架构概览

Omni-Agent 采用“感知 - 决策 - 执行”的智能体三段式架构，整体分为三个核心模块：

```
┌────────────────────────┐
│ omni-agent-service     │ ← 感知层：对接用户输入，封装事件，触发 Agent
└────────┬───────────────┘
         ↓
┌────────┴───────────────┐
│ omni-agent-core        │ ← 智能体内核：负责上下文建模、图编排、事件调度与执行
└────────┬───────────────┘
         ↓
┌────────┴───────────────┐
│ omni-agent-mcp         │ ← 外部交互层：通过 Tool 与外部系统连接
└────────────────────────┘
```

---

## 📦 模块职责

### `omni-agent-core` — 智能体核心模块

负责核心智能体逻辑，包括智能体注册、上下文生命周期、事件处理模型、工作流编排和记忆管理等。

#### 📁 模块结构

```
core/
├── agents       // 智能体定义与执行逻辑
│   ├── OmniAgentEngine.java     // Agent 执行引擎
│
├── context      // 上下文体系（Context）
│   ├── IAgentStaticContext.java   // 静态上下文：注册表、会话管理器等
│   ├── IAgentRuntimeContext.java  // 动态上下文：事件状态、工作流状态等
│
├── event        // 感知事件建模
│   ├── IAgentEvent.java           // 输入事件
│   ├── IAgentRequest.java         // 输入事件请求
│   ├── IAgentResponse.java        // 输出事件响应
│   ├── IEventListener.java        // 事件监听器接口
│
├── graph        // 工作流定义与编排引擎
│   └── ...
│
├── memory       // 记忆子系统接口
│   └── ...
│
├── session      // 会话建模（Session）
│   ├── IAgentSessionItem.java
│   └── List<IAgentRuntimeContext> // 每次交互的上下文快照
```

#### 🧱 上下文建模设计

* **静态上下文（`IAgentStaticContext`）**：在 Agent 生命周期内不变的配置信息。

  * `agentRegistry`：系统中注册的所有 Agent
  * `chatClientBuilder`：LLM 客户端构建器
  * `memoryRepository`：记忆模块适配器
  * `agentEngine`：统一执行引擎接口
  * `graphSaverConfig`：流程记忆策略配置
  * `agentSessionManage`：会话管理接口

* **动态上下文（`IAgentRuntimeContext`）**：每次事件触发过程中动态变化的信息。

  * `IGraphState`：当前流程图执行状态
  * `List<IAgentEvent>`：事件轨迹链
  * `CompiledGraph`：已解析的图结构，驱动执行流程

#### 🔁 执行流程概览

```
用户输入
   ↓
[omni-agent-service] 封装为 IAgentEvent 并提交
   ↓
[OmniAgentEngine] 创建会话 → 路由到目标 Agent
   ↓
[Agent] 构建动态上下文 → 调用 Graph 执行
   ↓
[Graph] 根据节点逻辑调度 Think / Tool → 更新状态
   ↓
输出响应 IAgentResponse 返回给用户
```

---

### `omni-agent-mcp` — 外部交互模块

封装外部系统为 Tool 接口能力，供工作流动态调用。

* 所有工具必须实现统一 `Tool` 接口，具备参数校验与响应解析能力
* 支持通过配置或 SPI 实现自动注册
* 适配 REST API、数据库、知识库、表单系统等能力

---

### `omni-agent-service` — 事件感知模块

提供对外入口，接收用户请求并驱动 Agent 处理流程。

* 支持 HTTP / WebSocket / MQ 等事件接入方式
* 对输入进行结构化封装为 `IAgentRequest`
* 调用 `OmniAgentEngine` 完成整个生命周期的处理

---

## 🚀 快速开始

```bash
git clone https://github.com/beraising/omni-agent.git
cd omni-agent/omni-agent-service
./mvnw spring-boot:run
```

默认端口：`http://localhost:8080`

---

## 🔌 扩展说明

### ✅ 新增 Agent

1. 实现 `IAgent` 接口或继承抽象 Agent 类
2. 注册到 `AgentRegistry`
3. 编写对应的 Prompt 模板和 Graph 流程

### ✅ 新增 Tool

1. 实现 `Tool` 接口并定义 Tool Name
2. 注册至 MCP 模块配置中心
3. 在 Graph 中引用使用（支持参数动态绑定）

---

## 📚 示例智能体

| 智能体名称       | 功能说明          |
| ----------- | ------------- |
| FormAgent   | 智能引导用户填写表单并提交 |
| ReportAgent | 数据查询、报表生成与分析  |
| RouterAgent | 多智能体选择与转发     |

---

## 🛠 技术栈

* Java 17
* Spring Boot 3.x
* Spring AI（支持 Alibaba / OpenAI）
* WebFlux + SSE（流式返回）
* Redis / MySQL（状态与记忆存储）

---

## 📌 TODO

* [ ] Tool 插件机制支持（SPI 动态加载）
* [ ] 多 Agent 之间协同执行
* [ ] 可视化图流程编辑器
* [ ] 全配置驱动 Agent 定义能力

---

## 📌 版本约定

当前阶段为 开发中，版本号使用 0.x.x（例如 v0.1.0）

首个稳定版将从 v1.0.0 开始

版本遵循 语义化版本规范 (SemVer)

## 📄 License
Copyright 2025 Chongqing Beraising Technology Co., Ltd.
(重庆北睿星科技有限公司) www.beraising.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0


本项目采用 Apache License 2.0
 协议开源。

## 🌐 公司信息

公司名称：重庆北睿星科技有限公司

官网：www.beraising.com