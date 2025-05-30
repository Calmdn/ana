<template>
  <div v-if="visible" class="error-alert">
    <el-alert
      :title="title"
      :description="description"
      type="error"
      :closable="closable"
      :show-icon="showIcon"
      @close="handleClose"
    >
      <template v-if="$slots.default" #default>
        <slot />
      </template>
      
      <div v-if="showRetry" class="error-actions">
        <el-button size="small" type="primary" @click="handleRetry">
          重试
        </el-button>
        <el-button v-if="showDetails" size="small" @click="toggleDetails">
          {{ showDetailContent ? '隐藏详情' : '显示详情' }}
        </el-button>
      </div>
      
      <div v-if="showDetailContent && errorDetails" class="error-details">
        <pre>{{ errorDetails }}</pre>
      </div>
    </el-alert>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '操作失败'
  },
  description: {
    type: String,
    default: ''
  },
  error: {
    type: [String, Error, Object],
    default: null
  },
  closable: {
    type: Boolean,
    default: true
  },
  showIcon: {
    type: Boolean,
    default: true
  },
  showRetry: {
    type: Boolean,
    default: false
  },
  showDetails: {
    type: Boolean,
    default: false
  },
  visible: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['close', 'retry'])

const showDetailContent = ref(false)

const errorDetails = computed(() => {
  if (!props.error) return null
  
  if (typeof props.error === 'string') {
    return props.error
  } else if (props.error instanceof Error) {
    return props.error.stack || props.error.message
  } else if (typeof props.error === 'object') {
    return JSON.stringify(props.error, null, 2)
  }
  
  return String(props.error)
})

const handleClose = () => {
  emit('close')
}

const handleRetry = () => {
  emit('retry')
}

const toggleDetails = () => {
  showDetailContent.value = !showDetailContent.value
}
</script>

<style scoped>
.error-alert {
  margin-bottom: 16px;
}

.error-actions {
  margin-top: 12px;
}

.error-actions .el-button {
  margin-right: 8px;
}

.error-details {
  margin-top: 12px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  max-height: 200px;
  overflow-y: auto;
}

.error-details pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>