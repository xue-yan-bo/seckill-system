import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register, getUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  // 登录
  const loginAction = async (loginForm) => {
    try {
      const res = await login(loginForm)
      if (res.code === 200) {
        token.value = res.data.token
        localStorage.setItem('token', res.data.token)
        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(res.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败')
      return false
    }
  }

  // 注册
  const registerAction = async (registerForm) => {
    try {
      const res = await register(registerForm)
      if (res.code === 200) {
        ElMessage.success('注册成功')
        return true
      } else {
        ElMessage.error(res.message || '注册失败')
        return false
      }
    } catch (error) {
      ElMessage.error('注册失败')
      return false
    }
  }

  // 获取用户信息
  const getUserInfoAction = async () => {
    try {
      const res = await getUserInfo()
      if (res.code === 200) {
        userInfo.value = res.data
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    ElMessage.success('退出登录成功')
  }

  return {
    token,
    userInfo,
    loginAction,
    registerAction,
    getUserInfoAction,
    logout
  }
})

