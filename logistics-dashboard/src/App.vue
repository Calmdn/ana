<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <AppHeader />
      </el-header>
      
      <el-container>
        <el-aside class="app-sidebar" width="200px">
          <AppSidebar />
        </el-aside>
        
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import AppHeader from '@/components/layout/Header.vue'
import AppSidebar from '@/components/layout/Sidebar.vue'

export default {
  name: 'App',
  components: {
    AppHeader,
    AppSidebar
  }
}
</script>

<style>
.app-container {
  height: 100vh;
}

.app-header {
  background: #001529;
  color: white;
  padding: 0;
}

.app-sidebar {
  background: #f5f5f5;
}

.app-main {
  background: #fff;
  padding: 20px;
}
</style>