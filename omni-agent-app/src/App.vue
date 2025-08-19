<script setup lang="ts">
import ChatLayout from './components/ChatLayout.vue'
import SessionSidebar from './components/SessionSidebar.vue'
</script>


<template>
  <div class="container">
    <div class="header">
      <div class="dot" />
      <div>
        <div class="title">ChatGPT UI</div>
        <div class="subtle">Vue 3 + TypeScript · 多会话</div>
      </div>
    </div>


    <div class="main">
      <SessionSidebar />
      <ChatLayout />
    </div>
  </div>
</template>