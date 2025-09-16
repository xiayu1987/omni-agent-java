import type { AgentSessionDTO, Session } from '../types/types'
import { convertAgentSessionToSession } from '../types/types'
import { userStore } from '../composables/useUser'

const API_URL = '/agent/api'

// 会话状态
let agentSessions: AgentSessionDTO[] = []
let sessions: Session[] = []
let currentId: string | null = null

// ================= 初始化：根据 userId 获取所有 session =================
export async function loadSessions(): Promise<Session[]> {
    try {
        const res = await fetch(`${API_URL}/sessions/${userStore.getUserId()}`)
        if (res.ok) {
            agentSessions = await res.json()
            // 转换为前端 Session
            debugger;
            sessions = agentSessions.map(convertAgentSessionToSession)
            currentId = sessions[0]?.id ?? null
            return sessions
        }
    } catch (err) {
        console.error('加载失败:', err)
    }

    // 后端没有返回数据，初始化默认 session
    const first: AgentSessionDTO = {
        agentSessionId: crypto.randomUUID(),
        userId: userStore.getUserId(),
        agentSessionItems: []
    }
    agentSessions = [first]
    sessions = [{
        id: first.agentSessionId,
        title: '新会话',
        createdAt: Date.now(),
        messages: []
    }]
    currentId = first.agentSessionId
    return sessions
}

// ================= 重命名会话 =================
export async function renameSession(id: string, title: string): Promise<void> {
    const idx = sessions.findIndex(s => s.id === id)
    if (idx === -1) throw new Error('会话不存在')

    try {
        const res = await fetch(`${API_URL}/sessions/${userStore.getUserId()}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title })
        })
        if (res.ok) {
            sessions[idx].title = title
        } else {
            console.error('重命名失败:', await res.text())
        }
    } catch (err) {
        console.error('重命名失败:', err)
    }
}

// ================= 删除会话 =================
export async function deleteSession(id: string): Promise<void> {
    const idx = sessions.findIndex(s => s.id === id)
    if (idx === -1) throw new Error('会话不存在')

    try {
        const res = await fetch(`${API_URL}/sessions/${userStore.getUserId()}/${id}`, { method: 'DELETE' })
        if (res.ok) {
            sessions.splice(idx, 1)
            agentSessions.splice(idx, 1)
            if (currentId === id) currentId = sessions[0]?.id ?? null
        } else {
            console.error('删除失败:', await res.text())
        }
    } catch (err) {
        console.error('删除失败:', err)
    }
}

// ================= 获取当前状态 =================
export function getState() {
    return { sessions, currentId }
}
