<script setup lang="ts">
import { ref } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { useStore } from '../composables/useStore'

const emit = defineEmits<{ (e: 'select', id?: string): void }>()
const { state, newSession, switchSession, renameSession, deleteSession } = useStore()

const renamingId = ref<string | null>(null)
const renameText = ref('')

function startRename(id: string, title: string) { renamingId.value = id; renameText.value = title }
function confirmRename() { if (renamingId.value) { renameSession(renamingId.value, renameText.value.trim() || '未命名'); renamingId.value = null } }
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

    .el-menu-item:hover {
        background-color: #333333;
    }
}

/* 提取的内联样式（已改名） */
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
            <el-button type="primary" @click="newSession" :icon="Plus">新建会话</el-button>
        </div>

        <el-scrollbar>
            <el-menu :default-active="state.currentId || ''" class="el-menu"
                @select="(id: string) => { switchSession(id); emit('select', id) }">
                <template v-for="s in state.sessions" :key="s.id">
                    <el-menu-item class="el-menu-item" :index="s.id">
                        <template #title>
                            <div class="session-item-content">
                                <div class="session-title" v-if="renamingId !== s.id">{{ s.title }}</div>
                                <el-input v-else v-model="renameText" size="small" @keyup.enter="confirmRename"
                                    @blur="confirmRename" class="session-rename-input" />
                                <div class="session-actions">
                                    <el-tooltip content="重命名">
                                        <el-button class="el-button" @click.stop="startRename(s.id, s.title)"
                                            :icon="Edit" circle size="small" />
                                    </el-tooltip>
                                    <el-popconfirm title="确定删除该会话？" confirm-button-text="删除" cancel-button-text="取消"
                                        @confirm="deleteSession(s.id)">
                                        <template #reference>
                                            <el-button class="el-button" @click.stop :icon="Delete" circle
                                                size="small" />
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
