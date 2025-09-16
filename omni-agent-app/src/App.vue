<script setup lang="ts">
import { ref } from 'vue'
import { Menu } from '@element-plus/icons-vue'
import { useResponsive } from './composables/useResponsive'
import SessionSidebar from './components/SessionSidebar.vue'
import ChatLayout from './components/ChatLayout.vue'

const drawer = ref(false)
const { isMobile } = useResponsive()

// 选中的会话 ID
const selectedSessionId = ref<string | null>(null)

// 处理会话选择
function handleSessionSelect(id?: string) {
  if (id) {
    selectedSessionId.value = id
  }
  drawer.value = false
}
</script>

<template>
  <el-container class="full">
    <!-- 头部 -->
    <el-header height="54" class="header-bar">
      <div style="display:flex;align-items:center;gap:10px;height:54px;color:#e2e8f0">
        <el-button v-if="isMobile" @click="drawer = true" :icon="Menu" circle />
        <span style="font-weight:700">OMNI AGENT</span>
        <span style="opacity:.7;font-size:12px"></span>
      </div>
    </el-header>

    <el-container>
      <!-- 左侧会话（PC 显示） -->
      <el-aside v-if="!isMobile" class="sidebar">
        <SessionSidebar @select="handleSessionSelect" />
      </el-aside>

      <!-- 移动端抽屉 -->
      <el-drawer class="sidebar" v-model="drawer" title="会话" direction="ltr" size="80%">
        <SessionSidebar @select="handleSessionSelect" />
      </el-drawer>

      <!-- 主区域 -->
      <el-main class="main-panel">
        <ChatLayout :sessionId="selectedSessionId ?? ''" />
      </el-main>
    </el-container>
  </el-container>
</template>
