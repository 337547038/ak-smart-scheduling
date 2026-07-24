<template>
  <div class="page-card">
    <div class="page-header">
      <div class="page-title">排课变更记录</div>
      <el-button @click="loadData">
        <el-icon><Refresh /></el-icon> 刷新
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading" style="width:100%;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="createTime" label="变更时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" width="120" />
      <el-table-column prop="changeType" label="变更类型" width="120">
        <template #default="{ row }">
          <el-tag size="small" :type="changeTypeTag(row.changeType)">
            {{ changeTypeText(row.changeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="原时间" width="160">
        <template #default="{ row }">{{ row.oldTimeSlotInfo || '-' }}</template>
      </el-table-column>
      <el-table-column label="新时间/教师" width="160">
        <template #default="{ row }">
          <template v-if="row.changeType === 'REPLACE_TEACHER'">{{ row.newTeacherName || '-' }}</template>
          <template v-else-if="row.changeType === 'CANCEL'">-</template>
          <template v-else>{{ row.newTimeSlotInfo || '-' }}</template>
        </template>
      </el-table-column>
      <el-table-column prop="changeReason" label="变更原因" />
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无变更记录" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getScheduleChanges } from '@/api'

const list = ref([])
const loading = ref(false)

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 19)
}

function changeTypeText(type) {
  const map = { ADJUST_TIME: '调课', REPLACE_TEACHER: '换老师', CANCEL: '取消课程' }
  return map[type] || type
}
function changeTypeTag(type) {
  const map = { ADJUST_TIME: 'warning', REPLACE_TEACHER: 'primary', CANCEL: 'danger' }
  return map[type] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getScheduleChanges()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
