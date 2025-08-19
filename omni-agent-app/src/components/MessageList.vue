<script setup lang="ts">
import type { Message } from '../types'

defineProps<{ messages: Message[] }>()
</script>

<template>
    <div>
        <div v-for="m in messages" :key="m.id" :class="m.role === 'user' ? 'bubble-user' : 'bubble-assistant'"
            style="margin-bottom:10px">
            <div class="bubble" :class="m.role">{{ m.content }}</div>
            <div style="opacity:.6;font-size:12px;margin-top:4px">{{ new Date(m.createdAt).toLocaleTimeString() }}</div>
        </div>
    </div>
</template>