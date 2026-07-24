<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">老师管理</div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增老师
      </el-button>
    </div>

    <el-table :data="list" border stripe style="width:100%;" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="phone" label="联系电话" width="150" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑老师' : '新增老师'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username" v-if="!form.id">
          <el-input v-model="form.username" placeholder="请输入用户名（登录账号）" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-alert v-if="!form.id" title="新增老师初始密码为：teacher123" type="info" show-icon :closable="false" style="margin-top:10px;" />
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
import { getTeacherList, addTeacher, updateTeacher, deleteTeacher } from '@/api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, username: '', realName: '', phone: '', email: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getTeacherList()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { id: null, username: '', realName: '', phone: '', email: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  formRef.value.validate(async valid => {
    if (!valid) return
    try {
      if (form.id) {
        await updateTeacher(form)
        ElMessage.success('更新成功')
      } else {
        await addTeacher(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {}
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除老师【${row.realName}】吗？相关排课也会受影响。`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteTeacher(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(loadData)
</script>
