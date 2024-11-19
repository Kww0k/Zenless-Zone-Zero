<template>
  <div style="width: 100%;display: flex;gap: 60px;height: 100%">
    <el-scrollbar style="width: 280px;background-color: rgba(0, 0, 0, 0.7);">
      <template v-for="item in roomList">
        <el-badge v-if="item.room.id !== room.room.id && item.countMessage > 0" style="width: 100%;margin-bottom: 16px"
                  :value="item.countMessage" class="item">
          <div @click="changeRoom(item)" class="chat-box"
               :class="{'chat-box-simple': item.room.id !== room.room.id, 'chat-box-check': item.room.id === room.room.id}">
            <img :src="item.toUser.avatarUrl"
                 style="width: 45px;height: 45px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;"
                 alt="avatar"/>
            <div>
              <el-text style="width: 120px" truncated>{{ item.toUser.nickname }}</el-text>
              <br>
              <el-text style="width: 120px" truncated>{{ item.message.content }}</el-text>
            </div>
            <div style="font-size: 12px;display: flex;align-items: flex-start;height: 45px">
              {{ formatDate(item.message.createTime) }}
            </div>
          </div>
        </el-badge>

        <div v-else @click="changeRoom(item)" class="chat-box"
             :class="{'chat-box-simple': item.room.id !== room, 'chat-box-check': item.room.id === room.room.id}">
          <img :src="item.toUser.avatarUrl"
               style="width: 45px;height: 45px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;"
               alt="avatar"/>
          <div>
            <el-text style="width: 120px" truncated>{{ item.toUser.nickname }}</el-text>
            <br>
            <el-text style="width: 120px" truncated>{{ item.message.content }}</el-text>
          </div>
          <div style="font-size: 12px;display: flex;align-items: flex-start;height: 45px">
            {{ formatDate(item.message.createTime) }}
          </div>
        </div>
      </template>
    </el-scrollbar>

    <div v-if="room" style="background-color: rgba(0, 0, 0, 0.7);flex: 1 0 0;box-sizing: border-box;padding: 20px">
      <div>
        <div style="height: 40px;display: flex;align-items: center;font-size: 18px;border-bottom: 1px solid #4C4D4F">{{ room.toUser.nickname }}</div>
        <el-scrollbar id="scrollbar" ref="scrollbar"
                      style="height: calc(100vh - 180px - 40px - 40px - 120px);box-sizing: border-box;padding-left: 20px;padding-right: 20px">
          <div v-for="(msg, index) in messages" :key="index">
            <div
                class="message-box"
                style="margin-left: 20%;justify-content: right"
                v-if="msg.createBy === userinfo.id">
              <el-tooltip
                  class="box-item"
                  :content="formatDateMessage(msg.createTime)"
                  placement="left"
              >
                <div
                    style="background-color: #323232f2;box-sizing: border-box;padding: .625rem .9375rem;font-size: .9rem;border-radius: .75rem;">
                  {{ msg.content }}
                </div>
              </el-tooltip>
              <img :src="userinfo.avatarUrl"
                   style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;"
                   alt="avatar"/>
            </div>
            <div
                class="message-box"
                v-else>
              <img :src="room.toUser.avatarUrl"
                   style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;"
                   alt="avatar"/>
              <el-tooltip
                  class="box-item"
                  :content="formatDateMessage(msg.createTime)"
                  placement="right"
              >
                <div
                    style="background-color: #323232f2;box-sizing: border-box;padding: .625rem .9375rem;font-size: .9rem;border-radius: .75rem;">
                  {{ msg.content }}
                </div>
              </el-tooltip>
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
      console.log(roomList.value)
      if (roomList.value.length > 0) {
        room.value = res.data[0]
        console.log(111)
        request.get("/message/listMessage/" + room.value.room.id).then(res => {
          if (res.code === 200) {
            messages.value = res.data

            socket.value = new WebSocket('ws://localhost:8848/ws');

            socket.value.onopen = () => {
              status.value = '连接成功';
              if (socket.value && socket.value.readyState === WebSocket.OPEN) {
                socket.value.send(JSON.stringify({
                  "type": "open",
                  "room": room.value.room,
                  "token": "Bearer " + sessionStorage.getItem('token')
                }));
              }
            };

            socket.value.onmessage = (event) => {
              let result = JSON.parse(event.data)
              if (result.code === 200) {
              } else if (result.code === 201) {
                const createTimeUTC = new Date(result.data.createTime);
                const createTimeChina = new Date(createTimeUTC.getTime() + 8 * 60 * 60 * 1000);
                result.data.createTime = createTimeChina.toISOString();

                if (result.data.roomId === room.value.id) {
                  const room = roomList.value.find(info => info.room.id === result.data.roomId);
                  if (room) {
                    room.message = result.data
                  } else {
                    request.get("/room/getRoomInfoById/" + result.data.roomId).then(res => {
                      if (res.code === 200)
                        roomList.value.push(res.data)
                      else
                        ElMessage.error(res.message)
                    })
                  }
                  messages.value.push(result.data);
                } else {
                  const room = roomList.value.find(info => info.room.id === result.data.roomId);
                  if (room) {
                    room.message = result.data
                    room.countMessage += 1
                  } else {
                    request.get("/room/getRoomInfoById/" + result.data.roomId).then(res => {
                      if (res.code === 200)
                        roomList.value.push(res.data)
                      else
                        ElMessage.error(res.message)
                    })
                  }
                }
                roomList.value.sort((a, b) => {
                  return new Date(b.message.createTime) - new Date(a.message.createTime);
                });
              } else if (result.code === 202) {
                const room = roomList.value.find(info => info.room.id === result.data.roomId);
                if (room) {
                  // 假设 createTime 是一个 ISO 8601 字符串或类似格式的日期字符串
                  const createTimeUTC = new Date(result.data.createTime);

                  // 将时间增加8小时
                  const createTimeChina = new Date(createTimeUTC.getTime() + 8 * 60 * 60 * 1000);

                  // 更新 createTime 为中国时间
                  result.data.createTime = createTimeChina.toISOString(); // 或者你可以选择其他格式
                  room.message = result.data
                } else {
                  console.error("Room not found");
                }

                messages.value.push(result.data);
                message.value = '';

                roomList.value.sort((a, b) => {
                  return new Date(b.message.createTime) - new Date(a.message.createTime);
                });
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

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const today = new Date();

  const year = date.getUTCFullYear();
  const month = (date.getUTCMonth() + 1).toString().padStart(2, '0'); // 月份从0开始
  const day = date.getUTCDate().toString().padStart(2, '0');

  const toDayYear = today.getUTCFullYear();
  const toDayMonth = (today.getUTCMonth() + 1).toString().padStart(2, '0'); // 月份从0开始
  const toDayDay = today.getUTCDate().toString().padStart(2, '0');

  if (year === toDayYear && month === toDayMonth && day === toDayDay) {
    // 如果是今天，获取时分秒
    const hours = date.getUTCHours().toString().padStart(2, '0');
    const minutes = date.getUTCMinutes().toString().padStart(2, '0');
    const seconds = date.getUTCSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  } else {
    return `${year}/${month}/${day}`;
  }
}

const formatDateMessage = (dateString) => {
  const date = new Date(dateString);
  const today = new Date();

  const year = date.getUTCFullYear();
  const month = (date.getUTCMonth() + 1).toString().padStart(2, '0'); // 月份从0开始
  const day = date.getUTCDate().toString().padStart(2, '0');
  const hours = date.getUTCHours().toString().padStart(2, '0');
  const minutes = date.getUTCMinutes().toString().padStart(2, '0');
  const seconds = date.getUTCSeconds().toString().padStart(2, '0');

  const toDayYear = today.getUTCFullYear();
  const toDayMonth = (today.getUTCMonth() + 1).toString().padStart(2, '0'); // 月份从0开始
  const toDayDay = today.getUTCDate().toString().padStart(2, '0');

  if (year === toDayYear && month === toDayMonth && day === toDayDay) {
    return `${hours}:${minutes}:${seconds}`;
  } else {
    return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
  }
}
</script>

<style scoped>
.chat-box {
  cursor: pointer;
  box-sizing: border-box;
  padding: 10px 20px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.chat-box:not(:last-child) {
  margin-bottom: 16px;
}

.chat-box-simple {
  background-color: #4C4D4F;
  border: 1px solid #eeeeee
}

.chat-box-check {
  color: #1c1c1c;
  background-color: #eeeeee;
  border: 1px solid #4C4D4F
}
.message-box {
  max-width: 80%;
  display: flex;
  gap: 8px;
}
.message-box:first-child {
  margin-top: 8px;
}
.message-box:not(:last-child) {
  margin-bottom: 8px;
}
</style>


