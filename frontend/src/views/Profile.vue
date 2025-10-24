<template>
  <div class="profile">
    <h1 class="page-title">个人中心</h1>
    
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">
          {{ userStore.userInfo?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="昵称">
          {{ userStore.userInfo?.nickname || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ userStore.userInfo?.phone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ userStore.userInfo?.email || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatTime(userStore.userInfo?.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="userStore.userInfo?.status === 0 ? 'success' : 'danger'">
            {{ userStore.userInfo?.status === 0 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  if (!userStore.userInfo) {
    userStore.getUserInfoAction()
  }
})
</script>

<style scoped>
.profile {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
  font-size: 32px;
  color: #333;
}

.info-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>

