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

<template>
    <div style="padding:12px;color:#e2e8f0">
        <div style="display:flex;gap:8px;margin-bottom:8px">
            <el-button type="primary" @click="newSession" :icon="Plus">新建会话</el-button>
        </div>

        <el-scrollbar height="calc(100vh - 140px)">
            <el-menu :default-active="state.currentId || ''" class="el-menu-vertical-demo"
                @select="(id: string) => { switchSession(id); emit('select', id) }">
                <template v-for="s in state.sessions" :key="s.id">
                    <el-menu-item :index="s.id">
                        <template #title>
                            <div
                                style="display:flex;align-items:center;gap:8px;width:100%;justify-content:space-between">
                                <div v-if="renamingId !== s.id">{{ s.title }}</div>
                                <el-input v-else v-model="renameText" size="small" @keyup.enter="confirmRename"
                                    @blur="confirmRename" style="max-width:140px" />
                                <div style="display:flex;gap:6px">
                                    <el-tooltip content="重命名"><el-button @click.stop="startRename(s.id, s.title)"
                                            :icon="Edit" circle size="small" /></el-tooltip>
                                    <el-popconfirm title="确定删除该会话？" confirm-button-text="删除" cancel-button-text="取消"
                                        @confirm="deleteSession(s.id)">
                                        <template #reference>
                                            <el-button @click.stop :icon="Delete" circle size="small" />
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