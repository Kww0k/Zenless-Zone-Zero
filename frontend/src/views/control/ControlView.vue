<script setup>
import router from "@/router/index.js";
import {onMounted} from 'vue'

const currentRoute = router.currentRoute
const menuItems= [
  {
    title: "首页",
    children: [
      { name: "首页", path: '/control' }
    ]
  },
  {
    title: "活动管理",
    children: [
      { name: "活动管理", path: '/control/event' },
      { name: "评论管理", path: '/control/review' },
    ]
  },
  {
    title: "系统管理",
    children: [
      { name: "用户管理", path: '/control/user' },
      { name: "消息管理", path: '/control/message' },
    ]
  }
]

onMounted(() => {
  const htmlElement = document.documentElement; // 获取 HTML 根元素
  htmlElement.classList.add('dark'); // 移除 'dark' 类
})
</script>

<template>
  <div style="width: 100%;height: 55px;box-sizing: border-box;border-bottom: 1px solid #4C4D4F">
    <div style="width: 1500px;margin: auto;display: flex;align-items: center;justify-content: space-between">
    </div>
  </div>
<div style="width: 1500px;display: flex;margin: 20px auto auto;">
  <el-scrollbar style="width: 300px;box-sizing: border-box;padding: 20px 20px 60px;height: calc(100vh - 75px)">
    <div v-for="(item, index) in menuItems" :key="index" class="menu-item">
      <div class="menu-title">{{ item.title }}</div>
      <div v-for="(son, i) in item.children" :key="i" @click="router.push(son.path)"
           :class="['menu-son', { checked: currentRoute.path === son.path }]">
        {{ son.name }}
      </div>
    </div>
  </el-scrollbar>
  <el-scrollbar style="width: 1200px;box-sizing: border-box;padding: 20px 20px 60px;;height: calc(100vh - 75px);">
    <router-view/>
  </el-scrollbar>
</div>
</template>

<style scoped>
.menu-item:not(:last-child) {
  margin-bottom: 20px;
}
.menu-title {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 8px;
  line-height: 24px;
}
.menu-son {
  color: #cfd3dc;
  cursor: pointer;
  padding: 8px 16px;
  line-height: 1.5;
  font-size: .9rem;
  border-radius: 8px;
  transition: background-color 0.3s, transform 0.2s;
}
.menu-son:hover {
  color: #66b1ff
}
.menu-son:active {
  transform: scale(0.95);
}
.checked {
  background-color: #409eff1A;
  font-weight: bold;
  color: #409eff
}
</style>