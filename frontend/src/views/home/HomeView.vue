<script setup>
import {onMounted, reactive, ref} from 'vue'
import router from "@/router/index.js";
const currentRoute = router.currentRoute

const userinfo = ref(JSON.parse(sessionStorage.getItem('userInfo')))

const font = reactive({
  color: '#1d1d1d',
  fontSize: 90,
  fontWeight: 700
})

onMounted(() => {
  const htmlElement = document.documentElement; // 获取 HTML 根元素
  htmlElement.classList.add('dark'); // 移除 'dark' 类
})

const list = [
  { name: "推送", value: '/home' },
  { name: "日程", value: '/event' },
  { name: "个人中心", value: '/level' },
];
</script>

<template>
  <el-watermark :width="800" :gap="[0, -120]" style="background-color: #010101;height: 100vh" :font="font" :content="['Campus activity', 'Student']">
    <div style="width: 100%;height: 80px;background-color: black;z-index: 100;position: absolute">
      <div style="width: 1400px; height: 100%;margin: auto;display: flex;align-items: center;justify-content: space-between;position: relative">
        <div style="display: flex;align-items: center;gap: 20px">
          <div @click="router.push('/home')" style="cursor: pointer">
            <i style="font-size: 36px;height: 36px" class="fa-brands fa-rust"></i>
          </div>
          <div @click="router.push('/level')" style="padding: 2px 20px 2px 2px;border: 2px solid #343434;height: 40px;box-sizing: border-box;background-color: #202020;border-radius: 20px;display:flex;align-items: center;gap: 2px;cursor: pointer">
            <img :src="userinfo.avatarUrl" style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;" alt="avatar"/>
            <div >
              {{ userinfo.nickname }}
            </div>
          </div>
        </div>

        <div class="wrap">
          <div v-for="(item, index) in list"
               :key="item.value"
               @click="router.push(item.value)"
               :class="[
         currentRoute.path === item.value || (item.value === '/level' && currentRoute.path.includes('/level')) ? 'node-active' : 'node',
         {
           'first-node': index === 0,
           'last-node': index === list.length - 1,
           'middle-node': index > 0 && index < list.length - 1
         }
       ]">
            <span class="name">{{ item.name }}</span>
          </div>
        </div>
        <div style="right: 0;position: absolute;width: 500px;height: 40px; border: 4px solid rgba(153, 153, 153, 0.5); z-index: -100;box-sizing: border-box;border-radius: 30px">
        </div>
      </div>
    </div>

    <div style="position: absolute;top: 100px; height: calc(100vh - 180px);z-index: 100;width: 100%">
      <div style="width: 1400px;height: 100%;margin: auto">
        <router-view/>
      </div>
    </div>
  </el-watermark>
</template>

<style scoped>
.first-node {
  border-right: none !important; /* 第一个元素左边框为 none */
}

.middle-node {
  border-left: none !important; /* 中间元素左边框为 none */
  border-right: none !important; /* 中间元素右边框为 none */
}

.last-node {
  border-left: none !important; /* 最后一个元素右边框为 none */
}
.header-nav-item {
  box-sizing: border-box;
  padding-right: 40px;
  padding-left: 40px;
  display: flex;
  align-items: center;
  font-size: 18px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease, color 0.3s ease; /* 添加过渡效果 */
}

.select {
  background-color: #97be00;
  color: #010601;
}

.wrap {
  width: 500px;
  height: 40px;
  display: flex;
}

@keyframes growShrink {
  0% {
    transform: scale(1) skew(0);
  }
  50% {
    transform: scale(1.2) skew(-10deg);
  }
  100% {
    transform: scale(1) skew(0);
  }
}

.node,
.node-active {
  border: 4px solid rgba(153, 153, 153, 0.5);
  background-color: black;
  box-sizing: border-box;
  cursor: pointer;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-weight: bolder;
  font-style: italic;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.node-active {
  background-color: rgb(245, 204, 24);
  color: #000;
  z-index: 1;
  border: none;
  border-radius: 10px;
  animation: growShrink 1s ease-in-out infinite;
}

.node-active > span {
  display: inline-block;
  transform: skew(10deg);
}
.node > span {
  font-style: italic;
  color: #fff;
  font-size: 14px;
  font-weight: bolder;
}

.node:first-child,
.node-active:first-child {
  border-top-left-radius: 30px;
  border-bottom-left-radius: 30px;
}

.node:last-child,
.node-active:last-child {
  border-top-right-radius: 30px;
  border-bottom-right-radius: 30px;
}

</style>