<script setup>
import {onMounted, ref} from 'vue'
import request from "@/utils/request.js";
import { UserFilled } from '@element-plus/icons-vue'
const userinfo = ref(JSON.parse(sessionStorage.getItem('userInfo')))
const nickname = ref(userinfo.value.nickname)
const list = ref([])
const updateNicknameDialog = ref(false)
import {ElMessage} from "element-plus";
const uploadHeader = {'Authorization': 'Bearer ' + sessionStorage.getItem('token')}

const getList = () => {
  request.get('/event/myJoinList').then(res => {
    if (res.code === 200)
      list.value = res.data
    else
      ElMessage.error(res.message)
  })
}

onMounted(() => {
  getList()
})

const outEvent = (id) => {
  request.get('/event/outEvent?eventId=' + id).then(res => {
    if (res.code === 200) {
      ElMessage.success('退出成功')
      getList()
    } else
      ElMessage.error(res.message)
  })
}

const handleAvatarSuccess = (response) => {
  let form = {
    id : userinfo.value.id,
    avatarUrl: response
  }
  request.post('/account/save', form).then(res => {
    if (res.code === 200) {
      console.log(res.data)
      userinfo.value = res.data
      sessionStorage.setItem("userInfo", JSON.stringify(res.data))
      ElMessage.success('上传成功')
      setTimeout(() => {
        location.reload(); // 刷新页面
      }, 1000); // 1000 毫秒 = 1 秒
    } else {
      ElMessage.error(res.message)
    }
  })
}

const saveNickname = () => {
  let form = {
    id : userinfo.value.id,
    nickname: nickname.value
  }
  request.post('/account/save', form).then(res => {
    if (res.code === 200) {
      console.log(res.data)
      userinfo.value = res.data
      sessionStorage.setItem("userInfo", JSON.stringify(res.data))
      ElMessage.success('修改成功')
      close()
      setTimeout(() => {
        location.reload(); // 刷新页面
      }, 1000); // 1000 毫秒 = 1 秒
    } else {
      ElMessage.error(res.message)
    }

  })
}

const close = () => {
  updateNicknameDialog.value = false
  nickname.value = userinfo.value.nickname
}
</script>

<template>
  <div style="width: 100%;box-sizing: border-box;border-radius: 36px;border: 6px solid black;height: 100%;background: black">
    <div style="width: 100%;box-sizing: border-box;border-radius: 36px;border: 6px solid #313131;height: 100%;overflow: hidden;padding: 10px">
      <div style="box-sizing: border-box;height: 35%;width: 100%;position: relative;border-top-left-radius: 36px;border-top-right-radius: 36px;overflow: hidden;border: 4px solid #313131">
        <img src="@/assets/img/level-bg.png" style="width: 100%; height: 100%;object-fit: cover">
        <div style="position: absolute; left: 36px;z-index: 1000;top: 30%;display: flex;align-items: center;gap: 10px">
          <el-upload
              class="avatar-uploader-level"
              action="http://localhost:8080/file/upload"
              :show-file-list="false"
              :headers="uploadHeader"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="userinfo.avatarUrl" :src="userinfo.avatarUrl" class="avatar" alt="preview"/>
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>

          <div style="display: flex;gap: 4px;align-items: center;font-size: 14px;font-weight: bold">
            <div>
              {{ userinfo.nickname }}
            </div>
            <div style="cursor:pointer;" @click="updateNicknameDialog = true">
              <i class="fa-solid fa-pen-to-square"></i>
            </div>
          </div>
        </div>
      </div>
      <div style="height: 10%;display: flex;align-items: center;font-weight: bold;font-size: 18px;">
        您参与的活动
      </div>
      <div style="height: 55%;width: 100%">
        <div style="padding: 40px 20px;box-sizing: border-box;width: 100%;border-bottom-left-radius: 36px;border-bottom-right-radius: 36px;;border:4px solid #313131;height: 100%">
          <el-scrollbar>
            <div style="display: flex">
              <div v-for="item in list" class="scrollbar-demo-item">
                <img :src="item.preview" style="width: 176px;height: 60%;object-fit: cover;border-top-left-radius: 16px;border-top-right-radius: 16px">
                <div style="box-sizing: border-box;padding: 10px">
                  <el-text style="width: 156px" truncated>{{ item.title }}</el-text>
                  <el-button :disabled="new Date(item.endTime) < new Date()" @click="outEvent(item.id)" type="danger" plain style="width: 156px;margin-top: 10px" size="small">
                    {{ new Date(item.endTime) < new Date()? '活动结束' : '退出活动' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </div>

    <el-dialog
        v-model="updateNicknameDialog"
        width="400"
        :before-close="close"
    >
      <div style="background: black;box-sizing: border-box;padding: 10px 20px;display: flex;align-items: center;justify-content: space-between">
        <div style="font-weight: bold;font-size: 16px">
          修改您的用户名
        </div>
        <div @click="updateNicknameDialog = false" class="close-icon">
          <i class="fa-solid fa-rectangle-xmark"></i>
        </div>
      </div>
      <div style="box-sizing: border-box;padding: 20px">
        <el-input :prefix-icon="UserFilled" show-word-limit maxlength="20" style="width: 100%" v-model="nickname"/>
        <div style="text-align: right;margin-top: 20px">
          <el-button style="width: 120px" type="danger" @click="saveNickname" plain>确定</el-button>
        </div>
      </div>

    </el-dialog>
  </div>
</template>

<style scoped>

.scrollbar-demo-item {
  max-width: 184px;
  box-sizing: border-box;
  border: 4px solid #313131;
  border-radius: 20px;
  max-height: calc((100vh - 180px - 62px) * 0.55 - 100px);
  padding: 0;
  margin: 0;
}

.scrollbar-demo-item:not(:last-child) {
  margin-right: 20px;
}

.event-item {
  width: 197px;
  height: 100%;
  border: 4px solid #313131;
  background: black;
}

.avatar-uploader-level .avatar {
  width: 50px;
  object-fit: cover;
  border-radius: 50%;
  height: 50px;
  display: block;
}

.close-icon {
  font-size: 44px;
  cursor: pointer;
  color: #b82309
}
.close-icon:active {
  color: #ffcf00;
}
:deep(.el-dialog) {
  padding: 0;
  border: 4px solid #313131;
  overflow: hidden;
  border-top-left-radius: 20px;
  border-bottom-right-radius: 20px;
  border-bottom-left-radius: 20px;
}

:deep(.el-dialog .el-dialog__header) {
  display: none;
}
</style>

<style>

.avatar-uploader-level .el-upload {
  border: 4px solid #313131 !important;
  border-radius: 50% !important;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader-level .el-upload:hover {
  border-color: #fddc01;
}

.avatar-uploader-level .el-upload:focus {
  border-color: #313131;
}
</style>
