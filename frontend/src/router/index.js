import { createRouter, createWebHistory,createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login1',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '测试' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'class',
        name: 'ClassManage',
        component: () => import('@/views/ClassManage.vue'),
        meta: { title: '班级管理', roles: ['ADMIN'] }
      },
      {
        path: 'teacher',
        name: 'TeacherManage',
        component: () => import('@/views/TeacherManage.vue'),
        meta: { title: '老师管理', roles: ['ADMIN'] }
      },
      {
        path: 'timeslot',
        name: 'TimeSlotManage',
        component: () => import('@/views/TimeSlotManage.vue'),
        meta: { title: '课时管理', roles: ['ADMIN'] }
      },
      {
        path: 'schedule',
        name: 'ScheduleView',
        component: () => import('@/views/Schedule.vue'),
        meta: { title: '课表查看', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'auto-schedule',
        name: 'AutoSchedule',
        component: () => import('@/views/AutoSchedule.vue'),
        meta: { title: '一键排课', roles: ['ADMIN'] }
      },
      {
        path: 'schedule-change',
        name: 'ScheduleChange',
        component: () => import('@/views/ScheduleChange.vue'),
        meta: { title: '排课变更', roles: ['ADMIN'] }
      },
      {
        path: 'my-schedule',
        name: 'MySchedule',
        component: () => import('@/views/MySchedule.vue'),
        meta: { title: '我的课表', roles: ['TEACHER'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  document.title = to.meta.title ? `${to.meta.title} - 智能排课系统` : '智能排课系统'

  /*if (to.path === '/login') {
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
  }

  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  if (to.meta.roles && !to.meta.roles.includes(userStore.userInfo.role)) {
    next('/dashboard')
    return
  }*/

  next()
})

export default router
