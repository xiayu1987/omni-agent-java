import { ref, watch } from 'vue'
import type { Message, Session, StoreShape } from '../types'
import { uid } from '../utils/uid'

const KEY = 'chatgpt-ui-store-v1'

function load(): StoreShape {
    try {
        const raw = localStorage.getItem(KEY)
        if (raw) return JSON.parse(raw)
    } catch { }
    // 初始状态：一个默认会话
    const first: Session = { id: uid(), title: '新会话', createdAt: Date.now() }
    return { sessions: [first], messagesBySession: { [first.id]: [] }, currentId: first.id }
}

const state = ref<StoreShape>(load())

watch(state, val => localStorage.setItem(KEY, JSON.stringify(val)), { deep: true })

export function useStore() {
    function currentMessages(): Message[] {
        const id = state.value.currentId!
        return state.value.messagesBySession[id] || (state.value.messagesBySession[id] = [])
    }

    function newSession() {
        const s: Session = { id: uid(), title: '新会话', createdAt: Date.now() }
        state.value.sessions.unshift(s)
        state.value.currentId = s.id
        state.value.messagesBySession[s.id] = []
    }

    function switchSession(id: string) { state.value.currentId = id }

    function renameSession(id: string, title: string) {
        const s = state.value.sessions.find(x => x.id === id); if (s) s.title = title
    }

    function deleteSession(id: string) {
        const idx = state.value.sessions.findIndex(x => x.id === id)
        if (idx >= 0) state.value.sessions.splice(idx, 1)
        delete state.value.messagesBySession[id]
        if (state.value.currentId === id) {
            state.value.currentId = state.value.sessions[0]?.id ?? null
        }
        if (!state.value.currentId) newSession()
    }

    function pushMessage(m: Message) { currentMessages().push(m) }

    function clearMessages() { state.value.messagesBySession[state.value.currentId!] = [] }

    return { state, currentMessages, newSession, switchSession, renameSession, deleteSession, pushMessage, clearMessages }
}