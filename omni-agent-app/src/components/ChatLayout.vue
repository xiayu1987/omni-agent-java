<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import MessageList from './MessageList.vue'
import ChatInput from './ChatInput.vue'
import { useChat } from '../composables/useChat'
import { Message } from '../types/types'
import {
  getState
} from '../composables/useSession'

// === 本地状态 ===
const messages = ref<Message[]>([])
const loading = ref(false)
const props = defineProps({
  sessionId: {
    type: String,
    required: true
  }
})

// 监听 sessionId 的变化，更新当前消息
watch(
  () => props.sessionId,
  (newSessionId) => {
    if (newSessionId) {
      const session = getState().sessions.find(s => s.id === newSessionId)
      if (session) {
        // 直接从 session 中获取消息数据
        messages.value = session.messages || [] // 假设 session 中有一个 messages 数组
      }
    }
  },
  { immediate: true } // 组件加载时也会触发一次，初始化消息
)

// === Chat 实例 ===
const chat = useChat() // 传入会话 ID

// 事件监听
chat.on("start", (userMsg, botMsg) => {
    messages.value.push(userMsg)
    messages.value.push(botMsg)
    loading.value = true
})

chat.on("chunk", (text, botMsg) => {
    // 找到对应的 botMsg 并更新
    const idx = messages.value.findIndex(m => m.id === botMsg.id)
    if (idx >= 0) {
        messages.value[idx] = { ...botMsg }
    }
})

chat.on("done", (botMsg) => {
    const idx = messages.value.findIndex(m => m.id === botMsg.id)
    if (idx >= 0) {
        messages.value[idx] = { ...botMsg }
    }
    loading.value = false
})

chat.on("error", (err) => {
    const last = messages.value[messages.value.length - 1]
    if (last) {
        last.content += `\n⚠️ 出错：${err.message}`
    }
    loading.value = false
})

// === 滚动逻辑 ===
const scroller = ref<HTMLElement | null>(null)
function scrollToBottom() {
    const el = scroller.value
    if (!el) return
    el.scrollTop = el.scrollHeight
}

watch(() => messages.value.length, () => nextTick(scrollToBottom))
onMounted(scrollToBottom)


function handleSend(text: string) {
    chat.send(text, props.sessionId)
}
</script>

<template>
    <el-card class="chat-card" shadow="never"
        body-style="padding:0;height:calc(100vh - 110px);display:flex;flex-direction:column">

        <div
            style="padding:8px 12px;display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #1f2937">
            <div style="opacity:.8;font-size:12px">{{ messages.length }} 条消息</div>
        </div>

        <el-scrollbar ref="scroller" class="chat-scroller">
            <MessageList :messages="messages" />
        </el-scrollbar>

        <div v-if="!messages.length" style="opacity:.7;text-align:center;margin-top:12px">开始对话吧～</div>

        <div class="footer-input">
            <ChatInput :loading="loading" @send="handleSend" />
        </div>
    </el-card>
</template>
