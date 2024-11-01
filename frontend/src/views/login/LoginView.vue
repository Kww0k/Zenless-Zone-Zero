<script setup>
import router from "@/router/index.js";
import {onMounted, reactive} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const form = reactive({
  username: '',
  password: '',
  type: 'login'
})

onMounted(() => {
  const htmlElement = document.documentElement; // 获取 HTML 根元素
  htmlElement.classList.remove('dark'); // 移除 'dark' 类
  window.addEventListener('keydown', keyDown)
})

const keyDown = (e) => {
  if (e.keyCode === 13) {
    login()
  }
}


const login = () => {
  request.post("/login", form,
      {headers: {"Content-type": 'application/x-www-form-urlencoded'}})
      .then(res => {
        if (res.code === 200) {
          sessionStorage.setItem("token", res.data.token)
          sessionStorage.setItem("userInfo", JSON.stringify(res.data.accountAuthVO))
          ElMessage.success('登录成功')
          router.push('/home')
        } else
          ElMessage.error(res.message)
      })
}

const changeType = () => {
  form.password = ''
  form.username = ''
  if (form.type === 'login')
    form.type = 'register'
  else
    form.type = 'login'
}
</script>

<template>
  <div class="login-bg">
    <div
        style="background-color: #eeeeee;box-sizing: border-box;padding: 20px 30px;border-top-right-radius: 16px;text-align: center">
      <div style="margin-bottom: 20px;color: black;font-size: 30px;font-weight: bold">
        {{ form.type === 'login' ? '登录' : '注册'}}
      </div>
      <div style="width: 320px;margin-bottom: 16px;height: 36px">
        <el-input v-model="form.username" placeholder="请输入用户名"/>
      </div>
      <div style="width: 320px;margin-bottom: 20px;height: 36px">
        <el-input type="password" v-model="form.password" placeholder="请输入密码"/>
      </div>
      <el-button @click="login" type="success" style="width: 100%;height: 36px">登录</el-button>
      <div v-if="form.type === 'login'" style="margin-top: 20px; text-align: right;width: 100%">
        <el-link @click="changeType">前往注册</el-link>
      </div>
      <div v-if="form.type === 'register'" style="margin-top: 20px; text-align: left;width: 100%">
        <el-link @click="changeType">返回登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-bg {
  width: 100%;
  height: 100vh;
  position: relative;
  background: url("@/assets/img/login-bg.jpg") top / cover no-repeat scroll;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>