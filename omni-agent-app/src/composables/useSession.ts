import type { AgentSessionDTO, Session } from '../types/types'
import { convertAgentSessionToSession } from '../types/types'
import { userStore } from '../composables/useUser'

const API_URL = '/agent/api'

// 会话状态
let sessions: Session[] = []
let currentId: string | null = null

export function setCurrentSession(id: string) {
    if (sessions.find(s => s.id === id)) {
        currentId = id
    }
}

// ================= 初始化：根据 userId 获取所有 session =================
export async function loadSessions(): Promise<Session[]> {
    try {
        const res = await fetch(`${API_URL}/sessions/${userStore.getUserId()}`)
        if (res.ok) {
            let agentSessions: AgentSessionDTO[] = await res.json()
            // 转换为前端 Session
            sessions = agentSessions.map(convertAgentSessionToSession).sort((a, b) => new Date(b.editTime).getTime() - new Date(a.editTime).getTime())
            if (currentId === null) {
                currentId = sessions[0]?.id ?? null
            }
            if (sessions.length > 0) {
                return sessions
            }
        }
    } catch (err) {
        console.error('加载失败:', err)
    }

    // 后端没有返回数据，初始化默认 session

    let defaultSession = {
        id: crypto.randomUUID(),
        title: '新会话',
        createTime: new Date().toISOString(),
        editTime: new Date().toISOString(),
        messages: []
    }

    sessions = [defaultSession]

    currentId = defaultSession.id

    return sessions
}

export function addNewSession(): void {

    let newSession = {
        id: crypto.randomUUID(),
        title: '新会话',
        createTime: new Date().toISOString(),
        editTime: new Date().toISOString(),
        messages: []
    }

    sessions.push(newSession)
    currentId = newSession.id
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
