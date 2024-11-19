<script setup>

import router from "@/router/index.js";
const currentRoute = router.currentRoute
const menu = [
  { name: "个人中心", value: '/level' },
  { name: "好友列表", value: '/level/friend' },
  { name: "活动管理", value: '/level/event' },
];
</script>

<template>
  <div style="width: 1200px; margin: auto;display: flex;gap: 40px;height: 100%">
    <div style="width: 260px;">
      <div class="side-item" v-for="item in menu" @click="router.push(item.value)">
        <div class="side-item-border" :class="[{ 'side-item-check' : currentRoute.path === item.value }]">
          {{ item.name }}
        </div>
      </div>
    </div>
    <div style="width: 900px;">
      <router-view/>
    </div>
  </div>
</template>

<style scoped>
.side-item {
  height: 50px;
  width: 100%;
  border-radius: 25px;
  background-color: black;
  box-sizing: border-box;
  border: 2px solid black;
  cursor: pointer;
}
.side-item:not(:last-child) {
  margin-bottom: 20px;
}
.side-item-border {
  width: 100%;
  height: 100%;
  color: white;
  font-weight: bold;
  border-radius: 23px;
  border: 4px solid #313131;
  box-sizing: border-box;
  align-items: center;
  justify-content: space-between;
  padding-left: 16px;
  padding-right: 20px;
  display: flex;
  transition: border-color 0.3s ease; /* 添加过渡效果 */
}

.side-item-border:hover {
  border-color: #fddc01; /* 鼠标悬停时边框颜色变化 */
}

.side-item-check {
  border: 4px solid #fddc01;
}



</style>