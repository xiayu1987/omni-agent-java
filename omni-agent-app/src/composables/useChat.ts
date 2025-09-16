import { uid } from '../utils/uid'
import type { Message } from '../types/types'
import { userStore } from '../composables/useUser'

const API_URL = '/agent/api'

/** 定义事件类型 */
export interface ChatEvents {
    start: (userMsg: Message, botMsg: Message) => void
    chunk: (text: string, botMsg: Message) => void
    done: (botMsg: Message) => void
    error: (err: Error) => void
}

// 通用事件总线（不对 Events 做 Record<...> 索引签名约束）
export function createEventBus<Events>() {
    // listeners 的类型：每个事件名对应一个回调数组（可选）
    const listeners: Partial<{ [K in keyof Events]: Events[K][] }> = {}

    function on<K extends keyof Events>(event: K, handler: Events[K]) {
        // 取到当前数组或初始化一个空数组（需要类型断言以满足 TS）
        const arr = (listeners[event] as Events[K][] | undefined) || []
        arr.push(handler)
        listeners[event] = arr
    }

    function off<K extends keyof Events>(event: K, handler: Events[K]) {
        listeners[event] = ((listeners[event] as Events[K][] | undefined) || []).filter(h => h !== handler) as Events[K][]
    }

    function emit<K extends keyof Events>(
        event: K,
        // 从 Events[K] 中提取参数元组（如果不是函数则为 never）
        ...args: Events[K] extends (...args: infer P) => any ? P : never
    ) {
        const arr = listeners[event] as (Events[K] extends (...args: any[]) => any ? Events[K][] : undefined)
        if (!arr) return
            // 强制把每个 handler 当作 any 函数来调用（已由类型系统在 emit 的签名处保证参数类型）
            ; (arr as unknown as ((...a: any[]) => any)[]).forEach(fn => fn(...(args as any)))
    }

    return { on, off, emit }
}

export function useChat() {
    const bus = createEventBus<ChatEvents>()
    let abortController: AbortController | null = null

    async function send(text: string, sessionId: string) {
        if (!text.trim()) return

        const userMsg: Message = { id: uid(), role: 'user', content: text.trim(), createdAt: Date.now() }
        const botMsg: Message = { id: uid(), role: 'assistant', content: '', createdAt: Date.now() }

        bus.emit('start', userMsg, botMsg)

        try {
            abortController?.abort()
            abortController = new AbortController()

            const res = await fetch(`${API_URL}/invoke`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    agentRequest: { requestType: 0, requestData: text.trim() },
                    agentResponse: { responseType: 0, responseData: "" },
                    agentSessionId: sessionId,
                    userId: userStore.getUserId()
                }),
                signal: abortController.signal
            })

            if (!res.ok || !res.body) throw new Error(`HTTP ${res.status}`)

            const reader = res.body.getReader()
            const decoder = new TextDecoder()
            let buffer = ''

            while (true) {
                const { done, value } = await reader.read()
                if (done) break

                buffer += decoder.decode(value, { stream: true })
                buffer = handleResponseData(buffer, botMsg, bus)
            }

            if (buffer.trim()) {
                botMsg.content += buffer
                bus.emit('chunk', buffer, botMsg)
            }

            bus.emit('done', botMsg)

        } catch (e: any) {
            bus.emit('error', e instanceof Error ? e : new Error(String(e)))
        }
    }

    function stop() {
        abortController?.abort()
        abortController = null
    }

    return { send, stop, on: bus.on, off: bus.off }
}

/** SSE 数据处理 */
function handleResponseData(buffer: string, botMsg: Message, bus: ReturnType<typeof createEventBus<ChatEvents>>): string {
    try {
        const regex = /^data:(.*)$/gm
        let match: RegExpExecArray | null
        while ((match = regex.exec(buffer))) {
            const text = match[1].trim()
            botMsg.content += text
            bus.emit('chunk', text, botMsg)
        }
        return ''
    } catch (err) {
        bus.emit('error', err instanceof Error ? err : new Error(String(err)))
        return buffer
    }
}
