export type Role = 'user' | 'assistant' | 'system'
export interface Message { id: string; role: Role; content: string; createdAt: number }
export interface Session { id: string; title: string; createdAt: number }
export interface StoreShape {
    sessions: Session[]
    messagesBySession: Record<string, Message[]>
    currentId: string | null
}