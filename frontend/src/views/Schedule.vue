<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">课表查看</div>
      <div>
        <el-radio-group v-model="viewMode" size="default" style="margin-right:16px;">
          <el-radio-button label="class">按班级查看</el-radio-button>
          <el-radio-button label="teacher" v-if="userStore.isAdmin">按教师查看</el-radio-button>
        </el-radio-group>
        <el-select v-if="viewMode==='class'" v-model="selectedClass" placeholder="选择班级" style="width:200px;" @change="loadSchedule">
          <el-option v-for="c in classes" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>
        <el-select v-else v-model="selectedTeacher" placeholder="选择教师" style="width:200px;" @change="loadSchedule">
          <el-option v-for="t in teachers" :key="t.id" :label="t.realName" :value="t.id" />
        </el-select>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="!hasData" style="text-align:center; padding:60px 0; color:#999;">
        <el-icon :size="60"><Calendar /></el-icon>
        <p style="margin-top:16px;">暂无排课数据，请先前往"一键排课"页面进行排课</p>
      </div>
      <table v-else class="schedule-table">
        <thead>
          <tr>
            <th style="width:80px;">节次 / 时间</th>
            <th v-for="d in days" :key="d">{{ weekDays[d] }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="slotName in slotNames" :key="slotName">
            <th>{{ slotName }}</th>
            <td v-for="day in days" :key="day"
                :class="['schedule-cell', getCell(day, slotName) ? 'status-' + (getCell(day, slotName).status || 'NORMAL') : 'empty']"
                @click="getCell(day, slotName) && handleCellClick(getCell(day, slotName))">
              <template v-if="getCell(day, slotName)">
                <div class="course-name">{{ getCell(day, slotName).courseName }}</div>
                <div class="teacher-name">
                  <template v-if="viewMode==='class'">{{ getCell(day, slotName).teacherName }}</template>
                  <template v-else>{{ getCell(day, slotName).className }}</template>
                </div>
                <div class="classroom">{{ getCell(day, slotName).classroom }}</div>
              </template>
              <template v-else>-</template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 调课对话框 -->
    <el-dialog v-model="adjustVisible" title="课程详情 / 调课" width="550px">
      <div v-if="currentCell" style="margin-bottom:20px; padding:15px; background:#f5f7fa; border-radius:6px;">
        <p><strong>课程：</strong>{{ currentCell.courseName }}</p>
        <p><strong>班级：</strong>{{ currentCell.className }}</p>
        <p><strong>教师：</strong>{{ currentCell.teacherName }}</p>
        <p><strong>时间：</strong>{{ weekDays[currentCell.dayOfWeek] }} {{ currentCell.slotName }} {{ currentCell.timeRange }}</p>
        <p><strong>教室：</strong>{{ currentCell.classroom || '-' }}</p>
        <p><strong>状态：</strong>
          <el-tag size="small" :type="currentCell.status==='NORMAL' ? 'success' : 'warning'">
            {{ currentCell.status==='NORMAL' ? '正常' : '已调课' }}
          </el-tag>
        </p>
        <p v-if="currentCell.remark"><strong>备注：</strong>{{ currentCell.remark }}</p>
      </div>
      <template v-if="userStore.isAdmin">
        <el-divider>调整课表</el-divider>
        <el-form :model="adjustForm" label-width="100px">
          <el-form-item label="调整类型">
            <el-radio-group v-model="adjustForm.changeType">
              <el-radio label="ADJUST_TIME">调整时间</el-radio>
              <el-radio label="REPLACE_TEACHER">更换老师</el-radio>
              <el-radio label="CANCEL">取消课程</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="新时间" v-if="adjustForm.changeType==='ADJUST_TIME'">
            <el-select v-model="adjustForm.newTimeSlotId" placeholder="选择新的时间段" style="width:100%;">
              <el-option v-for="ts in availableTimeSlots" :key="ts.id"
                         :label="`${weekDays[ts.dayOfWeek]} ${ts.slotName} ${ts.startTime?.substring(0,5)}-${ts.endTime?.substring(0,5)}`"
                         :value="ts.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="新教师" v-if="adjustForm.changeType==='REPLACE_TEACHER'">
            <el-select v-model="adjustForm.newTeacherId" placeholder="选择新教师" style="width:100%;">
              <el-option v-for="t in teachers" :key="t.id" :label="t.realName" :value="t.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="调整原因">
            <el-input v-model="adjustForm.changeReason" type="textarea" :rows="2" placeholder="请输入调整原因" />
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button @click="adjustVisible = false">关闭</el-button>
        <el-button type="primary" @click="submitAdjust" v-if="userStore.isAdmin">提交调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  getScheduleList, getClassList, getTeacherList,
  getTimeSlotList, adjustSchedule
} from '@/api'

const userStore = useUserStore()
const weekDays = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
const days = [1, 2, 3, 4, 5]

const viewMode = ref('class')
const classes = ref([])
const teachers = ref([])
const selectedClass = ref(null)
const selectedTeacher = ref(null)
const schedules = ref([])
const allTimeSlots = ref([])
const loading = ref(false)
const adjustVisible = ref(false)
const currentCell = ref(null)
const adjustForm = ref({ changeType: 'ADJUST_TIME', newTimeSlotId: null, newTeacherId: null, changeReason: '' })

const slotNames = computed(() => {
  const names = new Set()
  allTimeSlots.value.filter(t => days.includes(t.dayOfWeek)).forEach(t => names.add(t.slotName))
  return Array.from(names).sort((a, b) => {
    const na = parseInt(a.replace(/\D/g, ''))
    const nb = parseInt(b.replace(/\D/g, ''))
    return na - nb
  })
})

const availableTimeSlots = computed(() => {
  return allTimeSlots.value.filter(t => days.includes(t.dayOfWeek))
})

const hasData = computed(() => schedules.value && schedules.value.length > 0)

const scheduleMap = computed(() => {
  const map = {}
  schedules.value.forEach(s => {
    const key = `${s.dayOfWeek}_${s.slotName}`
    map[key] = s
  })
  return map
})

function getCell(day, slotName) {
  return scheduleMap.value[`${day}_${slotName}`]
}

async function loadBaseData() {
  const [c, t, ts] = await Promise.all([getClassList(), getTeacherList(), getTimeSlotList()])
  classes.value = c.data
  teachers.value = t.data
  allTimeSlots.value = ts.data
  if (classes.value.length > 0) selectedClass.value = classes.value[0].id
}

async function loadSchedule() {
  loading.value = true
  try {
    const params = {}
    if (viewMode.value === 'class' && selectedClass.value) params.classId = selectedClass.value
    if (viewMode.value === 'teacher' && selectedTeacher.value) params.teacherId = selectedTeacher.value
    const res = await getScheduleList(params)
    schedules.value = res.data
  } finally {
    loading.value = false
  }
}

function handleCellClick(cell) {
  currentCell.value = cell
  adjustForm.value = { changeType: 'ADJUST_TIME', newTimeSlotId: null, newTeacherId: null, changeReason: '' }
  adjustVisible.value = true
}

async function submitAdjust() {
  if (!adjustForm.value.changeReason) {
    ElMessage.warning('请输入调整原因')
    return
  }
  try {
    await adjustSchedule({
      scheduleId: currentCell.value.id,
      newTimeSlotId: adjustForm.value.changeType === 'ADJUST_TIME' ? adjustForm.value.newTimeSlotId : null,
      newTeacherId: adjustForm.value.changeType === 'REPLACE_TEACHER' ? adjustForm.value.newTeacherId : null,
      changeType: adjustForm.value.changeType,
      changeReason: adjustForm.value.changeReason
    })
    ElMessage.success('调整成功')
    adjustVisible.value = false
    loadSchedule()
  } catch (e) {}
}

onMounted(async () => {
  await loadBaseData()
  await loadSchedule()
})
</script>
