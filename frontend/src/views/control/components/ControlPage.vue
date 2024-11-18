<script setup>

import {onMounted, ref} from "vue";
import request from "@/utils/request.js";
const userinfo = ref(JSON.parse(sessionStorage.getItem('userInfo')))
const eventTotal = ref()
const userTotal = ref()
const commitTotal = ref()

onMounted(() => {
  request.get('/account/list?pageSize=5&pageNum=1&name=').then(res => {
    if (res.code === 200) {
      userTotal.value = res.data.total
    }
  })

  request.get('/event/list?pageSize=5&pageNum=1&name=').then(res => {
    if (res.code === 200) {
      console.log(res.data.list)
      eventTotal.value = res.data.total
    }
  })

  request.get('/commit/list?pageSize=5&pageNum=1&name=').then(res => {
    if (res.code === 200) {
      commitTotal.value = res.data.total
    }
  })
})
</script>

<template>
  <div style="width: 100%">
    <el-card style="width: calc(100% - 6px);margin-bottom: 40px">
      <div style="display: flex;gap: 20px;">
        <img :src="userinfo.avatarUrl" style="width: 50px;height: 50px;border-radius: 50%"/>
        <div>
          <div style="height: 25px; display: flex;align-items: center;font-weight: bold">
            欢迎您，{{ userinfo.nickname }}
          </div>
          <div style="height: 25px; display: flex;align-items: center;font-size: 12px;color: gray">
            每一次的代码提交，都是迈向成功的一步，让我们一起用技术创造更美好的未来！
          </div>
        </div>
      </div>
    </el-card>
    <div style="width: calc(100% - 6px);display: grid;grid-gap: 20px;grid-template-columns: repeat(3, 1fr);margin-bottom: 40px">
      <el-card style="width: 100%">
        <template #header>
          <div class="card-header">
            <span>活动数量</span>
          </div>
        </template>
        <div style="font-size: 24px;font-weight: bold">
          {{ eventTotal }}
        </div>
      </el-card>
      <el-card style="width: 100%">
        <template #header>
          <div class="card-header">
            <span>用户数量</span>
          </div>
        </template>
        <div style="font-size: 24px;font-weight: bold">
          {{ userTotal }}
        </div>
      </el-card>
      <el-card style="width: 100%">
        <template #header>
          <div class="card-header">
            <span>评论数量</span>
          </div>
        </template>
        <div style="font-size: 24px;font-weight: bold">
          {{ commitTotal }}
        </div>
      </el-card>
    </div>

    <el-card style="width: calc(100% - 6px);">
      <template #header>
        <div class="card-header">
          <span>图表数据</span>
        </div>
      </template>
      <div style="font-size: 24px;font-weight: bold">
        echarts
      </div>
    </el-card>
  </div>
</template>

<style scoped>

</style>