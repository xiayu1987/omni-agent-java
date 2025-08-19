<script setup lang="ts">
import { ref } from 'vue'


const props = defineProps<{ loading: boolean }>()
const emit = defineEmits<{ (e: 'send', text: string): void }>()


const text = ref('')


function onSubmit() {
    const t = text.value.trim()
    if (!t) return
    emit('send', t)
    text.value = ''
}
</script>


<template>
    <div class="inputbar">
        <textarea v-model="text" placeholder="输入你的问题，Shift+Enter 换行，Enter 发送" @keydown.enter.exact.prevent="onSubmit"
            @keydown.enter.shift.exact.stop />
        <button class="send" :disabled="loading" @click="onSubmit">发送</button>
    </div>
</template>