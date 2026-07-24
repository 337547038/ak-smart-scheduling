<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">我的课表</div>
    </div>

    <el-alert
      :title="`您好，${userStore.userInfo.realName}老师，这是您本周的课表`"
      type="success"
      :closable="false"
      style="margin-bottom:20px;"
    />

    <div v-loading="loading">
      <div v-if="!hasData" style="text-align:center; padding:60px 0; color:#999;">
        <el-icon :size="60"><Notebook /></el-icon>
        <p style="margin-top:16px;">暂无课程安排</p>
      </div>
      <table v-else class="schedule-table">
        <thead>
          <tr>
            <th style="width:80px;">节次</th>
            <th v-for="d in days" :key="d">{{ weekDays[d] }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="slotName in slotNames" :key="slotName">
            <th>{{ slotName }}</th>
            <td v-for="day in days" :key="day"
                :class="['schedule-cell', getCell(day, slotName) ? '' : 'empty']">
              <template v-if="getCell(day, slotName)">
                <div class="course-name">{{ getCell(day, slotName).courseName }}</div>
                <div class="teacher-name">{{ getCell(day, slotName).className }}</div>
                <div class="classroom">{{ getCell(day, slotName).classroom }}</div>
              </template>
              <template v-else>-</template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <el-divider>本周课程统计</el-divider>
    <el-row :gutter="16">
      <el-col :span="6" v-for="(cnt, course) in courseCount" :key="course">
        <div style="text-align:center; padding:15px; background:#f5f7fa; border-radius:6px;">
          <div style="font-size:20px; font-weight:bold; color:#409EFF;">{{ cnt }}</div>
          <div style="color:#666; font-size:13px; margin-top:4px;">{{ course }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getScheduleList, getTimeSlotList } from '@/api'

const userStore = useUserStore()
const weekDays = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五' }
const days = [1, 2, 3, 4, 5]

const schedules = ref([])
const allTimeSlots = ref([])
const loading = ref(false)

const slotNames = computed(() => {
  const names = new Set()
  allTimeSlots.value.filter(t => days.includes(t.dayOfWeek)).forEach(t => names.add(t.slotName))
  return Array.from(names).sort((a, b) => parseInt(a) - parseInt(b))
})

const hasData = computed(() => schedules.value && schedules.value.length > 0)

const scheduleMap = computed(() => {
  const map = {}
  schedules.value.forEach(s => { map[`${s.dayOfWeek}_${s.slotName}`] = s })
  return map
})

const courseCount = computed(() => {
  const map = {}
  schedules.value.forEach(s => {
    map[s.courseName] = (map[s.courseName] || 0) + 1
  })
  return map
})

function getCell(day, slotName) {
  return scheduleMap.value[`${day}_${slotName}`]
}

async function loadData() {
  loading.value = true
  try {
    const [s, ts] = await Promise.all([
      getScheduleList({ teacherId: userStore.userInfo.userId }),
      getTimeSlotList()
    ])
    schedules.value = s.data
    allTimeSlots.value = ts.data
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
