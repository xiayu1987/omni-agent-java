<script setup lang="ts">
import type { Message } from '../types'
import MessageBubble from './MessageBubble.vue'


defineProps<{ messages: Message[] }>()
</script>


<template>
    <div>
        <div v-for="m in messages" :key="m.id" class="row" :class="m.role">
            <MessageBubble :msg="m" />
        </div>
    </div>
</template>