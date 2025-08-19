import { ref } from 'vue'
import type { Message } from '../types'
import { useStore } from './useStore'
import { uid } from '../utils/uid'

export function useChat() {
    const { state, currentMessages, pushMessage } = useStore()
    const loading = ref(false)

    async function send(text: string) {
        if (!text.trim() || !state.value.currentId) return

        const userMsg: Message = { id: uid(), role: 'user', content: text.trim(), createdAt: Date.now() }
        const botMsg: Message = { id: uid(), role: 'assistant', content: '', createdAt: Date.now() }
        pushMessage(userMsg); pushMessage(botMsg)
        loading.value = true

        try {
            const res = await fetch('/api/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ messages: currentMessages().map(({ role, content }) => ({ role, content })) })
            })
            if (!res.ok || !res.body) throw new Error(`HTTP ${res.status}`)

            const reader = res.body.getReader(); const decoder = new TextDecoder(); let partial = ''
            while (true) {
                const { done, value } = await reader.read(); if (done) break
                partial += decoder.decode(value, { stream: true })
                const parts = partial.split(/\n\n|\r\n\r\n|\n/)
                if (parts.length > 1) { botMsg.content += parts.slice(0, -1).join('\n'); partial = parts[parts.length - 1] }
            }
            if (partial) botMsg.content += partial
        } catch (e: any) {
            const last = currentMessages()[currentMessages().length - 1]
            if (last && last.id) last.content = `⚠️ 出错：${e?.message || e}`
        } finally { loading.value = false }
    }

    return { loading, send }
}