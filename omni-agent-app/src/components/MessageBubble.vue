<script setup lang="ts">
import type { Message } from '../types'


const props = defineProps<{ msg: Message }>()
</script>


<template>
    <div class="bubble" :class="msg.role">
        <div>{{ msg.content || '…' }}</div>
        <div class="meta">{{ msg.role === 'user' ? '你' : '助手' }} · {{ new Date(msg.createdAt).toLocaleTimeString() }}
        </div>
    </div>
</template>