import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/seckill',
        name: 'Seckill',
        component: () => import('@/views/Seckill.vue'),
        meta: { title: '秒杀活动' }
      },
      {
        path: '/product',
        name: 'Product',
        component: () => import('@/views/Product.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: '/order',
        name: 'Order',
        component: () => import('@/views/Order.vue'),
        meta: { title: '我的订单', requireAuth: true }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requireAuth && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router

