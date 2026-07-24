<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <el-icon :size="32" color="#409EFF"><School /></el-icon>
        <div>智能排课系统</div>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tip">
        <p>管理员账号：admin / admin123</p>
        <p>教师账号：teacher1 / teacher123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { login } from '@/api'
  import { useUserStore } from '@/stores/user'

  const router = useRouter()
  const userStore = useUserStore()
  const formRef = ref(null)
  const loading = ref(false)

  const form = reactive({
    username: 'admin',
    password: 'admin123'
  })

  const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
  }

  async function handleLogin() {
    formRef.value.validate(async valid => {
      if (!valid) return
      loading.value = true
      try {
        const res = await login(form)
        userStore.setLoginData(res.data.token, res.data)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    })
  }
</script>
