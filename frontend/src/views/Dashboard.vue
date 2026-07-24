<template>
  <div>
    <div class="dashboard-welcome">
      <h2>欢迎回来，{{ userStore.userInfo.realName }}！</h2>
      <p>今天是 {{ today }}，祝您工作顺利！</p>
    </div>

    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon blue"><el-icon><Collection /></el-icon></div>
        <div class="stat-info">
          <div class="num">{{ stats.classCount }}</div>
          <div class="label">班级总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><el-icon><User /></el-icon></div>
        <div class="stat-info">
          <div class="num">{{ stats.teacherCount }}</div>
          <div class="label">教师总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><el-icon><Calendar /></el-icon></div>
        <div class="stat-info">
          <div class="num">{{ stats.courseCount }}</div>
          <div class="label">课程总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple"><el-icon><Clock /></el-icon></div>
        <div class="stat-info">
          <div class="num">{{ stats.scheduleCount }}</div>
          <div class="label">已排课程</div>
        </div>
      </div>
    </div>

    <div class="page-card">
      <div class="page-header">
        <div class="page-title">快捷操作</div>
      </div>
      <el-space wrap size="large">
        <el-button type="primary" size="large" @click="$router.push('/auto-schedule')" v-if="userStore.isAdmin">
          <el-icon><MagicStick /></el-icon> 一键排课
        </el-button>
        <el-button size="large" @click="$router.push('/schedule')">
          <el-icon><Calendar /></el-icon> 查看课表
        </el-button>
        <el-button type="success" size="large" @click="$router.push('/class')" v-if="userStore.isAdmin">
          <el-icon><Collection /></el-icon> 班级管理
        </el-button>
        <el-button type="warning" size="large" @click="$router.push('/teacher')" v-if="userStore.isAdmin">
          <el-icon><User /></el-icon> 教师管理
        </el-button>
      </el-space>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getClassList, getTeacherList, getCourseList, getScheduleList } from '@/api'

const userStore = useUserStore()
const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })

const stats = ref({ classCount: 0, teacherCount: 0, courseCount: 0, scheduleCount: 0 })

onMounted(async () => {
  try {
    const [c, t, co, s] = await Promise.all([
      getClassList(), getTeacherList(), getCourseList(), getScheduleList()
    ])
    stats.value.classCount = c.data.length
    stats.value.teacherCount = t.data.length
    stats.value.courseCount = co.data.length
    stats.value.scheduleCount = s.data.length
  } catch (e) {}
})
</script>
