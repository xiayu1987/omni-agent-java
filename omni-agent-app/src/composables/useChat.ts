import { ref } from 'vue'


function uid() { return Math.random().toString(36).slice(2) }


export function useChat() {
    const state = ref<ChatState>({ messages: [], loading: false })
    const sessions = ref<Session[]>([])
    const currentSessionId = ref<string>('')


    function newSession() {
        const id = uid()
        const s: Session = { id, title: '新会话', createdAt: Date.now() }
        sessions.value.unshift(s)
        currentSessionId.value = id
        state.value = { messages: [], loading: false }
    }


    function switchSession(id: string) {
        currentSessionId.value = id
        // 简单演示：实际可存储每个会话的消息
        state.value = { messages: [], loading: false }
    }


    async function send(prompt: string) {
        if (!prompt.trim()) return
        const userMsg: Message = { id: uid(), role: 'user', content: prompt.trim(), createdAt: Date.now() }
        const botMsg: Message = { id: uid(), role: 'assistant', content: '', createdAt: Date.now() }
        state.value.messages.push(userMsg)
        state.value.messages.push(botMsg)
        state.value.loading = true


        try {
            const res = await fetch('/api/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ messages: state.value.messages.map(({ role, content }) => ({ role, content })) })
            })


            if (!res.ok || !res.body) {
                throw new Error(`HTTP ${res.status}`)
            }


            const reader = res.body.getReader()
            const decoder = new TextDecoder()
            let partial = ''
            while (true) {
                const { done, value } = await reader.read()
                if (done) break
                partial += decoder.decode(value, { stream: true })
                const parts = partial.split(/\n\n|\r\n\r\n|\n/)
                if (parts.length > 1) {
                    botMsg.content += parts.slice(0, -1).join('\n')
                    partial = parts[parts.length - 1]
                }
            }
            if (partial) botMsg.content += partial
        } catch (err: any) {
            botMsg.content = `⚠️ 出错了：${err?.message || err}`
        } finally {
            state.value.loading = false
        }
    }


    function reset() {
        state.value = { messages: [], loading: false }
    }


    return { state, sessions, currentSessionId, newSession, switchSession, send, reset }
}