<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">课时管理（时间段配置）</div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增时间段
      </el-button>
    </div>

    <el-table :data="list" border stripe style="width:100%;" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="dayOfWeek" label="星期" width="100">
        <template #default="{ row }">{{ weekDays[row.dayOfWeek] }}</template>
      </el-table-column>
      <el-table-column prop="slotName" label="节次" width="100" />
      <el-table-column label="时间" width="200">
        <template #default="{ row }">{{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑时间段' : '新增时间段'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="星期" prop="dayOfWeek">
          <el-select v-model="form.dayOfWeek" placeholder="请选择星期" style="width:100%;">
            <el-option v-for="(name, idx) in weekDays" :key="idx" :label="name" :value="idx" />
          </el-select>
        </el-form-item>
        <el-form-item label="节次名称" prop="slotName">
          <el-select v-model="form.slotName" placeholder="如：第1节" style="width:100%;" allow-create filterable>
            <el-option v-for="i in 12" :key="i" :label="`第${i}节`" :value="`第${i}节`" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm:ss" placeholder="选择开始时间" style="width:100%;" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm:ss" placeholder="选择结束时间" style="width:100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTimeSlotList, addTimeSlot, updateTimeSlot, deleteTimeSlot } from '@/api'

const weekDays = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, dayOfWeek: 1, slotName: '', startTime: '08:00:00', endTime: '08:45:00' })
const rules = {
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  slotName: [{ required: true, message: '请输入节次名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

function formatTime(t) {
  if (!t) return ''
  return String(t).substring(0, 5)
}

async function loadData() {
  loading.value = true
  try {
    const res = await getTimeSlotList()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { id: null, dayOfWeek: 1, slotName: '', startTime: '08:00:00', endTime: '08:45:00' })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, {
    id: row.id,
    dayOfWeek: row.dayOfWeek,
    slotName: row.slotName,
    startTime: row.startTime,
    endTime: row.endTime
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  formRef.value.validate(async valid => {
    if (!valid) return
    try {
      if (form.id) {
        await updateTimeSlot(form)
        ElMessage.success('更新成功')
      } else {
        await addTimeSlot(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {}
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除【${weekDays[row.dayOfWeek]} ${row.slotName}】吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTimeSlot(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(loadData)
</script>
