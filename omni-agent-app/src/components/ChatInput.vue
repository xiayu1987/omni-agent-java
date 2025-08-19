<script setup lang="ts">
import { ref } from 'vue'
import { Promotion } from '@element-plus/icons-vue'

const props = defineProps<{ loading: boolean }>()
const emit = defineEmits<{ (e: 'send', text: string): void }>()

const text = ref('')
function onSend() { const t = text.value.trim(); if (!t) return; emit('send', t); text.value = '' }
</script>

<template>
    <div style="display:flex;gap:8px;align-items:flex-end">
        <el-input v-model="text" type="textarea" :autosize="{ minRows: 1, maxRows: 6 }"
            placeholder="输入消息，Enter 发送，Shift+Enter 换行" @keydown.enter.exact.prevent="onSend"
            @keydown.enter.shift.exact.stop />
        <el-button type="primary" :loading="loading" @click="onSend" :icon="Promotion">发送</el-button>
    </div>
</template>