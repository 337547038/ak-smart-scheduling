<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">班级管理</div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增班级
      </el-button>
    </div>

    <el-table :data="list" border stripe style="width:100%;" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="className" label="班级名称" />
      <el-table-column prop="grade" label="年级" width="120" />
      <el-table-column prop="studentCount" label="学生人数" width="120" />
      <el-table-column prop="headTeacherName" label="班主任" width="150">
        <template #default="{ row }">{{ row.headTeacherName || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑班级' : '新增班级'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择年级" style="width:100%;">
            <el-option label="高一" value="高一" />
            <el-option label="高二" value="高二" />
            <el-option label="高三" value="高三" />
            <el-option label="初一" value="初一" />
            <el-option label="初二" value="初二" />
            <el-option label="初三" value="初三" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生人数" prop="studentCount">
          <el-input-number v-model="form.studentCount" :min="0" :max="100" style="width:100%;" />
        </el-form-item>
        <el-form-item label="班主任">
          <el-select v-model="form.headTeacherId" placeholder="请选择班主任（可选）" clearable style="width:100%;">
            <el-option v-for="t in teachers" :key="t.id" :label="t.realName" :value="t.id" />
          </el-select>
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
import { getClassList, addClass, updateClass, deleteClass, getTeacherList } from '@/api'

const list = ref([])
const teachers = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, className: '', grade: '', studentCount: 0, headTeacherId: null })
const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getClassList()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

async function loadTeachers() {
  const res = await getTeacherList()
  teachers.value = res.data
}

function handleAdd() {
  Object.assign(form, { id: null, className: '', grade: '', studentCount: 0, headTeacherId: null })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleSubmit() {
  formRef.value.validate(async valid => {
    if (!valid) return
    try {
      if (form.id) {
        await updateClass(form)
        ElMessage.success('更新成功')
      } else {
        await addClass(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {}
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除班级【${row.className}】吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteClass(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadTeachers()
})
</script>
