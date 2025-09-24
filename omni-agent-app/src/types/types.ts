// ================= 前端 UI 类型 =================

// 聊天角色
export type Role = 'user' | 'assistant' | 'system'

// 单条消息（前端 UI 用）
export interface Message {
    id: string
    role: Role
    content: string
    createdAt: number
}

// 会话（前端 UI 用）
export interface Session {
    id: string
    title: string
    createTime: string
    editTime: string
    messages: Message[]
}

// ================= 后端 DTO 类型 =================

export interface AgentRequestDTO {
    requestType: number
    requestData: string
    createTime: string
    editTime: string
}

export interface AgentResponseDTO {
    responseType: number
    responseData: string
    createTime: string
    editedAt: string
}

export interface AgentSessionItemDTO {
    agentRequest: AgentRequestDTO
    agentResponse: AgentResponseDTO
    agentName: string
}

export interface AgentSessionDTO {
    agentSessionId: string
    userId: string
    agentSessionItems: AgentSessionItemDTO[]
    editTime: string
    createTime: string
}


export interface AgentEventDTO {
    agentRequest: AgentRequestDTO
    agentResponse: AgentResponseDTO
    agentSessionId: string
    userId: string
}

// ================= 转换方法 =================

/**
 * 把后端的 AgentSessionDTO 转成前端的 Session
 */
export function convertAgentSessionToSession(dto: AgentSessionDTO): Session {
    return {
        id: dto.agentSessionId,
        title: dto.agentSessionItems?.[0]?.agentRequest.requestData || `会话 ${dto.agentSessionId.slice(0, 6)}`, // 优先取 requestData，没值才用默认
        createTime: dto.createTime ?? new Date().toISOString(),
        editTime: dto.editTime ?? new Date().toISOString(),
        messages: convertItemsToMessages(dto.agentSessionItems) // 补充 messages 字段
    }
}

/**
 * 把后端的会话项转成前端的 Message[]
 */
export function convertItemsToMessages(items: AgentSessionItemDTO[]): Message[] {
    const msgs: Message[] = []
    items.forEach(item => {
        if (item.agentRequest) {
            // 用户消息
            msgs.push({
                id: crypto.randomUUID(),
                role: 'user',
                content: item.agentRequest.requestData,
                createdAt: Date.now()
            })
        }
        if (item.agentResponse) {
            // 助手消息
            msgs.push({
                id: crypto.randomUUID(),
                role: 'assistant',
                content: item.agentResponse.responseData,
                createdAt: Date.now()
            })
        }

    })
    return msgs
}
