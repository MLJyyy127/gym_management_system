<template>
  <div style="padding: 24px">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2 style="margin: 0">会员主页</h2>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>

    <el-card v-if="member">
      <h3 style="margin: 0 0 12px 0">{{ member.memberName }} 的主页</h3>
      <div style="color: #666">会员账号/卡号：{{ member.memberAccount }}</div>
    </el-card>
    <el-card v-else>未登录或会话失效。</el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const member = ref(null)

async function load() {
  const resp = await api.get('/api/toUserMain')
  const data = resp.data || {}
  member.value = data.member || null
}

async function logout() {
  await postForm('/api/logout', {})
  router.push('/toUserLogin')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

