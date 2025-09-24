import { uid } from '../utils/uid'
import type { Message, AgentEventDTO } from '../types/types'
import { userStore } from '../composables/useUser'

const API_URL = '/agent/api'

/** 定义事件类型 */
export interface ChatEvents {
    start: (userMsg: Message, botMsg: Message) => void
    session: (sessionId: string) => void
    chunk: (text: string, botMsg: Message) => void
    done: (botMsg: Message) => void
    error: (err: Error) => void
}

// 通用事件总线
export function createEventBus<Events>() {
    const listeners: Partial<{ [K in keyof Events]: Events[K][] }> = {}

    function on<K extends keyof Events>(event: K, handler: Events[K]) {
        const arr = (listeners[event] as Events[K][] | undefined) || []
        arr.push(handler)
        listeners[event] = arr
    }

    function off<K extends keyof Events>(event: K, handler: Events[K]) {
        listeners[event] = ((listeners[event] as Events[K][] | undefined) || []).filter(h => h !== handler) as Events[K][]
    }

    function emit<K extends keyof Events>(event: K, ...args: Events[K] extends (...args: infer P) => any ? P : never) {
        const arr = listeners[event] as (Events[K] extends (...args: any[]) => any ? Events[K][] : undefined)
        if (!arr) return
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

        abortController?.abort()
        abortController = new AbortController()

        try {
            const eventPayload: AgentEventDTO = {
                agentRequest: {
                    requestType: 0,
                    requestData: text.trim(),
                    createTime: new Date().toISOString(),
                    editTime: new Date().toISOString()

                },
                agentResponse: {
                    responseType: 0,
                    responseData: '',
                    createTime: new Date().toISOString(),
                    editedAt: new Date().toISOString()
                },
                agentSessionId: sessionId,
                userId: userStore.getUserId()
            }

            const res = await fetch(`${API_URL}/invoke`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(eventPayload),
                signal: abortController.signal
            })

            if (!res.ok || !res.body) throw new Error(`HTTP ${res.status}`)

            const reader = res.body.getReader()
            const decoder = new TextDecoder()
            let buffer = ''
            const gotSession = { value: false }

            const processBuffer = (buf: string) => {
                const lines = buf.split(/\r?\n/)
                let remainder = ''
                for (let line of lines) {
                    line = line.trim()
                    if (!line) continue
                    if (line.startsWith('data:')) {
                        const payload = line.slice(5).trim()
                        if (payload === '[DONE]') continue
                        const parsed = parseSSEChunk(payload)
                        if (!parsed) {
                            remainder += payload
                            continue
                        }

                        if (parsed.sessionId && !gotSession.value) {
                            gotSession.value = true
                            bus.emit('session', parsed.sessionId)
                        }

                        if (parsed.text) {
                            botMsg.content += parsed.text
                            bus.emit('chunk', parsed.text, botMsg)
                        }
                    } else {
                        remainder += line
                    }
                }
                return remainder
            }

            while (true) {
                const { done, value } = await reader.read()
                if (done) {
                    buffer += decoder.decode()
                    break
                }
                buffer += decoder.decode(value, { stream: true })
                buffer = processBuffer(buffer)
            }

            // flush 剩余 buffer
            if (buffer.trim()) {
                buffer = processBuffer(buffer)
                if (buffer.trim()) {
                    botMsg.content += buffer.trim()
                    bus.emit('chunk', buffer.trim(), botMsg)
                }
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

/** SSE JSON 解析，匹配 AgentEventDTO 中的 agentSessionId/text */
function parseSSEChunk(raw: string): { text?: string; sessionId?: string } | null {
    try {
        const obj: AgentEventDTO = JSON.parse(raw)

        return {
            // 第一帧获取 sessionId
            sessionId: obj.agentSessionId,
            // 文本内容来自 agentResponse.responseData
            text: obj.agentResponse?.responseData
        }
    } catch {
        return null
    }
}