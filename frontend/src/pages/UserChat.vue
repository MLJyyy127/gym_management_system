<template>
  <div class="chat-page">
    <div class="chat-header">
      <h2 class="chat-title">聊天</h2>
      <div class="chat-subtitle">与你的健身房智能助手对话</div>
    </div>

    <el-card class="chat-card" shadow="never">
      <el-scrollbar ref="scrollbarRef" class="chat-scroll">
        <div class="chat-list">
          <div v-for="m in messages" :key="m.id" class="chat-row" :class="m.role">
            <div class="bubble">
              <div class="bubble-meta">{{ m.role === 'user' ? '我' : 'AI' }}</div>
              <div class="bubble-text">{{ m.text }}</div>
            </div>
          </div>
        </div>
      </el-scrollbar>

      <div class="chat-input">
        <el-input
          v-model="draft"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 4 }"
          placeholder="输入你想问的问题，例如：如何安排每周训练计划？"
          @keydown.enter.exact.prevent="send()"
        />
        <div class="chat-actions">
          <el-button :disabled="isSending || !draft.trim()" type="primary" @click="send()">
            {{ isSending ? '发送中…' : '发送' }}
          </el-button>
          <el-button :disabled="isSending || messages.length <= 1" @click="clearChat()">清空</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref } from 'vue'
import { postForm } from '../api/client'

type ChatRole = 'user' | 'assistant'
type ChatMessage = {
  id: string
  role: ChatRole
  text: string
  createdAt: number
}

const draft = ref('')
const isSending = ref(false)
const scrollbarRef = ref<any>(null)
const messages = ref<ChatMessage[]>([
  {
    id: crypto.randomUUID(),
    role: 'assistant',
    text: '你好，我是系统内置的 AI 助手。你可以问我训练计划、饮食建议或课程安排相关的问题。',
    createdAt: Date.now()
  }
])

function pushMessage(role: ChatRole, text: string) {
  messages.value.push({
    id: crypto.randomUUID(),
    role,
    text,
    createdAt: Date.now()
  })
}

async function scrollToBottom() {
  await nextTick()
  if (scrollbarRef.value) {
    const wrap = scrollbarRef.value.wrapRef
    if (wrap) {
      wrap.scrollTop = wrap.scrollHeight
    }
  }
}

async function send() {
  const content = draft.value.trim()
  if (!content || isSending.value) return

  draft.value = ''
  pushMessage('user', content)
  isSending.value = true
  try {
    const resp = await postForm('/api/chat/query', { content, model: 'deepseek-chat' })
    const payload = resp.data
    if (payload?.success && payload?.reply) {
      pushMessage('assistant', payload.reply)
    } else {
      const msg = payload?.message || '后端未返回 reply'
      pushMessage('assistant', `接口返回错误：${msg}`)
    }
  } catch (e: any) {
    // 让你能直接看到失败原因，而不是永远落到固定占位回复
    const msg = e?.response?.data?.message || e?.message || '请求失败'
    console.error('chat query failed:', e)
    pushMessage('assistant', `请求失败：${msg}`)
  } finally {
    isSending.value = false
    await scrollToBottom()
  }
}

function clearChat() {
  messages.value = [messages.value[0]].filter(Boolean) as ChatMessage[]
}

function buildPlaceholderReply(userText: string) {
  const t = userText.toLowerCase()
  if (t.includes('训练') || userText.includes('计划')) {
    return '你可以按“力量 + 有氧 + 休息”循环来安排：每周 3 次力量训练（全身/上下肢分化都可以）+ 2 次 20-30 分钟中低强度有氧。告诉我你的目标（减脂/增肌/塑形）和每周可训练天数，我可以给你更具体的方案。'
  }
  if (t.includes('饮食') || userText.includes('吃') || userText.includes('热量')) {
    return '饮食上建议先保证蛋白质摄入（每公斤体重约 1.6g 左右作为常见起点），再根据目标调节总热量：减脂略微热量缺口，增肌略微盈余。你可以告诉我身高体重、目标和日常活动量，我再帮你估算。'
  }
  if (t.includes('课程') || userText.includes('报名')) {
    return '课程方面你可以先在“报名选课”里查看可报名课程，再到“我的课程”确认已报名。你想咨询的是课程时间冲突、取消报名，还是推荐课程类型？'
  }
  return '收到。我目前是前端占位的 AI 回复。你可以继续描述更具体的问题（目标、时间安排、身体基础），我会按你的情况给出建议。'
}

onMounted(() => {
  if (typeof crypto?.randomUUID !== 'function') {
    // 极少数环境不支持 randomUUID；这里不做降级也不会影响主流程
  }
})
</script>

<style scoped>
.chat-page {
  padding: 24px;
}

.chat-header {
  margin-bottom: 12px;
}

.chat-title {
  margin: 0;
}

.chat-subtitle {
  color: #666;
  margin-top: 6px;
  font-size: 13px;
}

.chat-card {
  border: 1px solid #ebeef5;
}

.chat-scroll {
  height: min(60svh, 520px);
  padding: 8px 8px 0 8px;
}

.chat-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 8px;
}

.chat-row {
  display: flex;
}

.chat-row.user {
  justify-content: flex-end;
}

.chat-row.assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: min(680px, 88%);
  padding: 10px 12px;
  border-radius: 12px;
  line-height: 1.5;
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: #ffffff;
}

.chat-row.assistant .bubble {
  background: #f8f9fa;
}

.chat-row.user .bubble {
  background: #ecf5ff;
  border-color: rgba(64, 158, 255, 0.25);
}

.bubble-meta {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
  margin-bottom: 4px;
}

.chat-row.user .bubble-meta {
  text-align: right;
}

.chat-row.assistant .bubble-meta {
  text-align: left;
}

.bubble-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-input {
  border-top: 1px solid #ebeef5;
  padding: 12px;
  display: grid;
  gap: 10px;
}

.chat-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
</style>