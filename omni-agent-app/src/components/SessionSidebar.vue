<script setup lang="ts">
import { useChat } from '../composables/useChat'


const { sessions, currentSessionId, newSession, switchSession } = useChat()
</script>


<template>
    <div class="sidebar">
        <div class="sidebar-header">
            <div class="title">会话</div>
            <button class="icon-btn" @click="newSession">＋</button>
        </div>
        <div class="session-list">
            <div v-for="s in sessions" :key="s.id" class="session-item" :class="{ active: s.id === currentSessionId }"
                @click="switchSession(s.id)">
                {{ s.title }}
            </div>
        </div>
    </div>
</template>