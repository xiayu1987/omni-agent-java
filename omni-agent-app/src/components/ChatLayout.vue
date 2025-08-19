<script setup lang="ts">
import { onMounted, onUpdated, ref } from 'vue'
import { useChat } from '../composables/useChat'
import MessageList from './MessageList.vue'
import ChatInput from './ChatInput.vue'


const { state, send, reset } = useChat()
const scroller = ref<HTMLDivElement | null>(null)


function scrollToBottom() {
    const el = scroller.value
    if (!el) return
    el.scrollTop = el.scrollHeight
}


onMounted(scrollToBottom)
onUpdated(scrollToBottom)
</script>


<template>
    <div class="chat">
        <div class="toolbar">
            <div class="subtle">共 {{ state.messages.length }} 条消息</div>
            <div style="display:flex; gap:8px;">
                <button class="icon-btn" @click="reset">清空</button>
            </div>
        </div>


        <div ref="scroller" class="scroller">
            <MessageList :messages="state.messages" />
            <div v-if="!state.messages.length" class="empty">开始对话吧，问我任何问题～</div>
        </div>


        <ChatInput :loading="state.loading" @send="send" />
    </div>
</template>