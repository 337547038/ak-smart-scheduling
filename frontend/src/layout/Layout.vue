<template>
  <div class="layout-container">
    <div class="layout-header">
      <div class="logo">
        <el-icon :size="28"><School /></el-icon>
        <span>智能排课系统</span>
      </div>
      <div class="user-info">
        <el-tag :type="userStore.isAdmin ? 'danger' : 'success'" size="small">
          {{ userStore.isAdmin ? '管理员' : '教师' }}
        </el-tag>
        <span>{{ userStore.userInfo.realName }}</span>
        <el-dropdown @command="handleCommand">
          <el-avatar :size="32" style="cursor:pointer; background:#fff; color:#409EFF;">
            {{ userStore.userInfo.realName?.charAt(0) }}
          </el-avatar>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div class="layout-body">
      <div class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          background-color="#001529"
          text-color="#b7bdc3"
          active-text-color="#409EFF"
          router
        >
          <template v-for="item in menus" :key="item.path">
            <el-menu-item :index="item.path" v-if="hasRole(item.roles)">
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </div>
      <div class="layout-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const menus = [
  { path: '/dashboard', title: '首页', icon: 'HomeFilled', roles: ['ADMIN', 'TEACHER'] },
  { path: '/class', title: '班级管理', icon: 'Collection', roles: ['ADMIN'] },
  { path: '/teacher', title: '老师管理', icon: 'User', roles: ['ADMIN'] },
  { path: '/timeslot', title: '课时管理', icon: 'Clock', roles: ['ADMIN'] },
  { path: '/schedule', title: '课表查看', icon: 'Calendar', roles: ['ADMIN', 'TEACHER'] },
  { path: '/auto-schedule', title: '一键排课', icon: 'MagicStick', roles: ['ADMIN'] },
  { path: '/schedule-change', title: '排课变更', icon: 'EditPen', roles: ['ADMIN'] },
  { path: '/my-schedule', title: '我的课表', icon: 'Notebook', roles: ['TEACHER'] }
]

function hasRole(roles) {
  return roles && roles.includes(userStore.userInfo.role)
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.el-menu { border-right: none; }
</style>
