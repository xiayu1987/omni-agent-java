<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getState,
  setCurrentSession,
  loadSessions,
  addNewSession,
  renameSession,
  deleteSession
} from '../composables/useSession'

const emit = defineEmits<{ (e: 'select', id?: string): void }>()
defineExpose({
  updateSession
})

// 本地状态
const renamingId = ref<string | null>(null)
const renameText = ref('')

// 初始化 store
let sessionState = ref<ReturnType<typeof getState>>(getState())

onMounted(async () => {
  await loadSessions()
  sessionState.value = getState()
})

// 重命名逻辑
function startRename(id: string, title: string) {
  renamingId.value = id
  renameText.value = title
}

async function confirmRename() {
  if (!renamingId.value || !renameText.value) return
  await renameSession(renamingId.value, renameText.value)
  sessionState.value = getState() // 更新本地 state
  renamingId.value = null
  renameText.value = ''
}

// 选择会话
async function handleSelect(id: string) {
  setCurrentSession(id);
  emit('select', id)
}

// 新建会话
async function handleNewSession() {
  addNewSession()
  sessionState.value = getState()
}

async function updateSession(id: string) {
  await loadSessions()
  setCurrentSession(id);
  sessionState.value = getState()
}

</script>


<style lang="css" scoped>
.el-menu {
  background-color: transparent;



  .el-menu-item {
    color: white;

    .session-item-content {
      display: flex;
      align-items: center;
      gap: 10px;
      width: 100%;
      justify-content: space-between;

      .session-title {
        overflow: hidden;
      }

      .el-button {
        background-color: transparent;
        color: white;
        border: none;
        border-radius: 0%;
        padding: 0px;
        margin: 0px;
      }
    }
  }

  .el-menu-item.is-active {
    background-color: #232323;
  }

  .el-menu-item:hover {
    background-color: #333333;
  }
}

.session-list-wrapper {
  padding: 12px;
  color: #e2e8f0;
}

.session-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.session-rename-input {
  max-width: 140px;
}

.session-actions {
  display: flex;
  gap: 5px;
}
</style>

<template>
  <div class="session-list-wrapper">
    <div class="session-header">
      <el-button type="primary" @click="handleNewSession" :icon="Plus">新建会话</el-button>
    </div>

    <el-scrollbar>
      <el-menu :default-active="sessionState?.currentId || ''" class="el-menu" @select="handleSelect">
        <template v-for="session in sessionState?.sessions" :key="session.id">
          <el-menu-item class="el-menu-item" :index="session.id">
            <template #title>
              <div class="session-item-content">
                <div class="session-title" v-if="renamingId !== session.id">{{ session.title }}</div>
                <el-input v-else v-model="renameText" size="small" @keyup.enter="confirmRename" @blur="confirmRename"
                  class="session-rename-input" />
                <div class="session-actions">
                  <el-tooltip content="重命名">
                    <el-button class="el-button" @click.stop="startRename(session.id, session.title)" :icon="Edit"
                      circle size="small" />
                  </el-tooltip>
                  <el-popconfirm title="确定删除该会话？" confirm-button-text="删除" cancel-button-text="取消"
                    @confirm="deleteSession(session.id)">
                    <template #reference>
                      <el-button class="el-button" @click.stop :icon="Delete" circle size="small" />
                    </template>
                  </el-popconfirm>
                </div>
              </div>
            </template>
          </el-menu-item>
        </template>
      </el-menu>

    </el-scrollbar>
  </div>
</template>
