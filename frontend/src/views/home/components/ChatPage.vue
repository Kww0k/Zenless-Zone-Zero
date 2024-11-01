<template>
  <div style="width: 100%;display: flex;gap: 80px;height: 100%">
    <div style="width: 280px;background-color: rgba(0, 0, 0, 0.7);">
      <template v-for="item in roomList">
        <el-badge v-if="item !== room && item.countMessage > 0" style="width: 100%;margin-bottom: 16px" :value="item.countMessage" class="item">
          <div  @click="changeRoom(item)" style="
      cursor:pointer;background-color: #4C4D4F; box-sizing: border-box;
      padding:20px;display: flex;gap: 10px;align-items: center;border-bottom: 1px solid #eeeeee">
            <img :src="item.toUser.avatarUrl" style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;" alt="avatar"/>
          </div>
        </el-badge>

        <div v-else @click="changeRoom(item)" style="
      cursor:pointer;background-color: #4C4D4F; box-sizing: border-box;margin-bottom: 16px;
      padding:20px;display: flex;gap: 10px;align-items: center;border-bottom: 1px solid #eeeeee">
          <img :src="item.toUser.avatarUrl" style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;" alt="avatar"/>
        </div>
      </template>

    </div>

    <div v-if="room" style="background-color: rgba(0, 0, 0, 0.7);flex: 1 0 0;box-sizing: border-box;padding: 20px;">
      <div>
        <div style="height: 40px;display: flex;align-items: center;font-size: 18px">{{ room.toUser.nickname }}</div>
        <el-scrollbar id="scrollbar" ref="scrollbar"
                      style="height: calc(100vh - 180px - 40px - 40px - 120px);box-sizing: border-box;padding-left: 20px;padding-right: 20px">
          <div v-for="(msg, index) in messages" :key="index">
            <div
                :style="{
                   maxWidth: '80%',
                   marginLeft: '20%',
                   display: 'flex',
                   gap: '8px',
                   justifyContent: 'right',
                   marginBottom: index < messages.length - 1 ? '8px' : '0'
                }"
                v-if="msg.createBy === userinfo.id">
              <div style="background-color: #323232f2;box-sizing: border-box;padding: .625rem .9375rem;font-size: .9rem;border-radius: .75rem;">
                {{ msg.content }}
              </div>

              <img :src="userinfo.avatarUrl" style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;" alt="avatar"/>
            </div>
            <div
                :style="{
                   maxWidth: '80%',
                   display: 'flex',
                   gap: '8px',
                   marginBottom: index < messages.length - 1 ? '8px' : '0'
                }"
                v-else>
              <img :src="room.toUser.avatarUrl" style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;" alt="avatar"/>
              <div style="background-color: #323232f2;box-sizing: border-box;padding: .625rem .9375rem;font-size: .9rem;border-radius: .75rem;">
                {{ msg.content }}
              </div>
            </div>
          </div>
        </el-scrollbar>
      </div>
      <div style="display: flex;gap: 20px;height: 120px;position: relative;box-sizing: border-box;padding-top: 10px">
        <el-input type="textarea" :resize="'none'" :rows="3" v-model="message" @keyup.enter="sendMessage"
                  placeholder="输入消息..."/>
        <el-button style="position: absolute; bottom: 2px; right: 0;" type="primary" plain @click="sendMessage">
          发送消息
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, watch, nextTick} from 'vue';
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const roomList = ref([])
const room = ref()
const userinfo = ref(JSON.parse(sessionStorage.getItem('userInfo')))
const socket = ref(null);
const status = ref('');
const message = ref('');
const messages = ref([]);
const scrollbar = ref(null);

onMounted(() => {
  request.get("/room/getRoomList").then(res => {
    if (res.code === 200) {
      roomList.value = res.data
      if (roomList.value.length > 0) {
        room.value = res.data[0]
        request.get("/message/listMessage/" + room.value.room.id).then(res => {
          if (res.code === 200) {
            messages.value = res.data

            socket.value = new WebSocket('ws://localhost:8848/ws');

            socket.value.onopen = () => {
              status.value = '连接成功';
              if (socket.value && socket.value.readyState === WebSocket.OPEN) {
                socket.value.send(JSON.stringify({
                  "type": "open",
                  "token": "Bearer " + sessionStorage.getItem('token')
                }));
              }
            };

            socket.value.onmessage = (event) => {
              let result = JSON.parse(event.data)
              if (result.code === 200) {
              } else if (result.code === 201) {
                messages.value.push(result.data);
              } else if (result.code === 202) {
                messages.value.push(result.data);
                message.value = ''; // 清空输入框
              } else if (result.code === 401) {
                status.value = '连接失败'
              } else {
                ElMessage.error(result.message)
              }
            };

            socket.value.onclose = () => {
              status.value = '连接已关闭';
            };

            socket.value.onerror = (error) => {
              console.error('WebSocket 错误:', error);
              status.value = '连接失败';
            };
          } else
            ElMessage.error(res.message)
        })
        updateScroll(); // 组件挂载时滚动到底部
      }
    } else
      ElMessage.error(res.message)
  })
})

const changeRoom = (item) => {
  item.countMessage = 0
  request.get("/message/listMessage/" + item.room.id).then(res => {
    if (res.code === 200) {
      messages.value = res.data
      room.value = item
    }
  })
}

const updateScroll = async () => {
  await nextTick(); // 确保 DOM 更新完成
  const scrollHeight = document.getElementById('scrollbar')?.scrollHeight;
  scrollbar.value.setScrollTop(scrollHeight);
};

watch([messages], updateScroll, {
  deep: true,
});

const sendMessage = () => {
  if (message.value && socket.value && socket.value.readyState === WebSocket.OPEN) {
    socket.value.send(JSON.stringify({
      "type": "message",
      "room": room.value.room,
      "toUserId": room.value.room.userOne === userinfo.value.id ? room.value.room.userTwo : room.value.room.userOne,
      "token": "Bearer " + sessionStorage.getItem('token'),
      "message": message.value,
      "timestamp": Date.now()
    }));
  }
};

onBeforeUnmount(() => {
  if (socket.value) {
    socket.value.close();
  }
});
</script>


