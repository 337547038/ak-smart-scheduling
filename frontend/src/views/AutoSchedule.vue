<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">一键智能排课</div>
    </div>

    <el-alert
      title="智能排课说明"
      type="info"
      :closable="false"
      style="margin-bottom:20px;">
      <template #default>
        <p>系统将根据以下规则自动排课：</p>
        <ul style="margin:8px 0 0 20px; line-height:1.8;">
          <li>语文、数学、英语等主科优先安排在上午时段</li>
          <li>自动避免同一教师/同一班级的时间冲突</li>
          <li>根据课程每周课时数合理均衡分布</li>
          <li>执行排课前会清空现有排课数据</li>
        </ul>
      </template>
    </el-alert>

    <div style="margin-bottom:20px;">
      <el-space size="large" wrap>
        <el-statistic title="班级数量" :value="classes.length" />
        <el-statistic title="教师数量" :value="teachers.length" />
        <el-statistic title="课程数量" :value="courses.length" />
        <el-statistic title="可用课时" :value="timeSlots.length" />
      </el-space>
    </div>

    <el-space size="middle">
      <el-button type="primary" size="large" :loading="scheduling" @click="handleAutoSchedule">
        <el-icon><MagicStick /></el-icon> 开始一键排课
      </el-button>
      <el-button size="large" :loading="clearing" @click="handleClear" :disabled="scheduleCount === 0">
        <el-icon><Delete /></el-icon> 清空现有排课
      </el-button>
      <el-tag v-if="scheduleCount > 0" type="success" size="large">当前已有 {{ scheduleCount }} 条排课记录</el-tag>
    </el-space>

    <el-divider>排课结果</el-divider>

    <div v-if="result" style="padding:20px; background:#f5f7fa; border-radius:8px;">
      <el-result :icon="result.failCount === 0 ? 'success' : 'warning'"
                 :title="result.failCount === 0 ? '排课完成！' : '排课完成，存在部分课程未能安排'"
                 :sub-title="`成功安排 ${result.successCount} 节课`">
        <template #extra>
          <el-button type="primary" @click="$router.push('/schedule')">查看课表</el-button>
        </template>
      </el-result>
      <div v-if="result.messages && result.messages.length > 0" style="margin-top:10px;">
        <el-alert v-for="(msg, i) in result.messages" :key="i" :title="msg" type="warning" show-icon :closable="false" style="margin-bottom:8px;" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getClassList, getTeacherList, getCourseList, getTimeSlotList,
  getScheduleList, autoSchedule, clearSchedule
} from '@/api'

const classes = ref([])
const teachers = ref([])
const courses = ref([])
const timeSlots = ref([])
const scheduleCount = ref(0)
const scheduling = ref(false)
const clearing = ref(false)
const result = ref(null)

async function loadData() {
  const [c, t, co, ts, s] = await Promise.all([
    getClassList(), getTeacherList(), getCourseList(), getTimeSlotList(), getScheduleList()
  ])
  classes.value = c.data
  teachers.value = t.data
  courses.value = co.data
  timeSlots.value = ts.data
  scheduleCount.value = s.data.length
}

function handleAutoSchedule() {
  if (classes.value.length === 0) {
    ElMessage.warning('请先添加班级')
    return
  }
  if (teachers.value.length === 0) {
    ElMessage.warning('请先添加教师')
    return
  }
  ElMessageBox.confirm(
    scheduleCount.value > 0
      ? `当前已有 ${scheduleCount.value} 条排课记录，继续将清空重排，是否继续？`
      : '确定开始一键智能排课吗？',
    '确认排课', { type: 'warning' }
  ).then(async () => {
    scheduling.value = true
    result.value = null
    try {
      const res = await autoSchedule()
      result.value = res.data
      ElMessage.success('排课完成')
      loadData()
    } catch (e) {} finally {
      scheduling.value = false
    }
  }).catch(() => {})
}

function handleClear() {
  ElMessageBox.confirm('确定清空所有排课记录吗？此操作不可恢复！', '提示', { type: 'warning' })
    .then(async () => {
      clearing.value = true
      try {
        await clearSchedule()
        ElMessage.success('清空成功')
        result.value = null
        loadData()
      } finally {
        clearing.value = false
      }
    }).catch(() => {})
}

onMounted(loadData)
</script>
