<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import { useStore } from '../composables/useStore'
import { useChat } from '../composables/useChat'
import MessageList from './MessageList.vue'
import ChatInput from './ChatInput.vue'

const { currentMessages, clearMessages } = useStore()
const { loading, send } = useChat()

const scroller = ref<HTMLElement | null>(null)
function scrollToBottom() {
    const el = scroller.value; if (!el) return
    el.scrollTop = el.scrollHeight
}

watch(() => currentMessages().length, () => nextTick(scrollToBottom))

onMounted(scrollToBottom)
</script>

<template>
    <el-card class="chat-card" shadow="never"
        body-style="padding:0;height:calc(100vh - 110px);display:flex;flex-direction:column">
        <div
            style="padding:8px 12px;display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #1f2937">
            <div style="opacity:.8;font-size:12px">{{ currentMessages().length }} 条消息</div>
            <div style="display:flex;gap:8px">
                <el-button @click="clearMessages" :icon="Delete">清空</el-button>
            </div>
        </div>

        <div ref="scroller" class="chat-scroller">
            <MessageList :messages="currentMessages()" />
            <div v-if="!currentMessages().length" style="opacity:.7;text-align:center;margin-top:12px">开始对话吧～</div>
        </div>

        <div class="footer-input">
            <ChatInput :loading="loading" @send="send" />
        </div>
    </el-card>
</template>