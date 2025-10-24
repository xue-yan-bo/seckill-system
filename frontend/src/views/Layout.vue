<template>
  <div class="layout">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-content">
          <div class="logo">
            <el-icon><ShoppingCart /></el-icon>
            <span>电商秒杀系统</span>
          </div>
          
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            :ellipsis="false"
            class="menu"
            router
          >
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/seckill">秒杀活动</el-menu-item>
            <el-menu-item index="/product">商品列表</el-menu-item>
            <el-menu-item index="/order" v-if="userStore.token">我的订单</el-menu-item>
          </el-menu>
          
          <div class="user-info">
            <template v-if="userStore.token">
              <el-dropdown>
                <span class="user-name">
                  <el-icon><User /></el-icon>
                  {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                    <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button type="primary" @click="router.push('/login')">登录</el-button>
              <el-button @click="router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>
      
      <!-- 主体内容 -->
      <el-main class="main">
        <router-view />
      </el-main>
      
      <!-- 底部 -->
      <el-footer class="footer">
        <p>© 2024 电商秒杀系统. All Rights Reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = ref(route.path)

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

onMounted(() => {
  if (userStore.token && !userStore.userInfo) {
    userStore.getUserInfoAction()
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.menu {
  flex: 1;
  border: none;
  margin: 0 40px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.main {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 200px);
}

.footer {
  background-color: #f5f5f5;
  text-align: center;
  color: #999;
  padding: 20px;
}
</style>

