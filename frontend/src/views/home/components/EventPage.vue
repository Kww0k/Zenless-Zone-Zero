<script setup>
import {onMounted, ref} from 'vue'
import request from "@/utils/request.js";

const list = ref([])
const allList = ref([])
const time = ref('morning')
const listSelect = ref()

onMounted(() => {
  getList()
})

const changeTime = (newTime) => {
  time.value = newTime
  changeList()
}

const getList = () => {
  request.get('/event/listEventForPlayer').then(res => {
    if (res.code === 200) {
      allList.value = res.data
      changeList()
    }
  })

}

const changeSelect = (index) => {
  listSelect.value = index
}

const changeList = () => {
  list.value = allList.value.filter(item => item.timePeriod.includes(time.value) || item.timePeriod === '全天')
  while (list.value.length < 5) {
    list.value.push(null)  // 你可以根据需要插入不同的占位符，如 null 或 "" 等
  }

  listSelect.value = 0
}
</script>

<template>
<div style="width: 100%;box-sizing: border-box;border-radius: 36px;border: 6px solid #313131;height: 100%">
  <div style="width: 100%;box-sizing: border-box;border-radius: 36px;border: 6px solid black;height: 100%;overflow: hidden">
    <div style="font-weight: bold;color: #434343;width: 100%;height: 52px;background-color: #080808;box-sizing: border-box;padding-left: 20px;display: flex;align-items: center">
      活动详情
    </div>
    <div style="width: 100%;height: calc(100% - 52px);background-color: #313131;box-sizing: border-box;padding:20px;display: flex;gap: 20px">
      <div style="background-color: #090909;border-radius: 16px;width: calc(50% - 10px);overflow: hidden">
        <div style="box-sizing: border-box;border: 4px solid black;height: 30%;border-radius: 16px;overflow: hidden;position: relative">
          <img v-if="list[listSelect]" alt="preview" :src="list[listSelect].preview" style="object-fit: cover;width: 100%;height: 100%" />
          <div style="font-weight: bold; position: absolute; left: 10px; bottom: 10px;
            color: white;
              letter-spacing: 3px;
         text-shadow:
              -1px -1px 0 #000,
              1px -1px 0 #000,
              -1px 1px 0 #000,
              1px 1px 0 #000;">
            {{ list[listSelect] ? list[listSelect].title : null }}
          </div>
        </div>
        <div style="box-sizing: border-box;padding: 10px;height: 60px;">
          <div style="display: flex;align-items:center;box-sizing: border-box;padding-left:20px;border-radius: 20px;background-color: #1c1c1c;color: white;height: 40px;font-size: 20px;font-weight: bold">
            一起参与，共创精彩，您的每一步都至关重要！
          </div>
        </div>
        <el-scrollbar style="box-sizing: border-box;padding: 10px;height: calc(70% - 70px);color: #8d8d8d;font-weight: 550">
          <div v-if="list[listSelect]" v-html="list[listSelect].content" class="responsive-content">
          </div>
        </el-scrollbar>
      </div>
      <div style="width: calc(50% - 10px)">
        <div style="position: relative;width: 100%;height: 100%;">
          <div style="position: absolute;display: grid;gap: 10px;grid-template-columns: repeat(4, 1fr);top: -40px;width: 100%">
            <div @click="changeTime('morning')" class="time-item" :class="{'select' : time === 'morning'}">
              <div v-if="time === 'morning'" style="position: absolute;right: -10px;top: -10px;color: #c4c4c4">
                <i style="width: 24px;height: 24px;font-size: 24px" class="fa-solid fa-location-dot"></i>
              </div>
              <div class="item-border">
                <div class="item-icon">
                  <i style="color: #fade3e" class="fa-solid fa-cloud-sun"></i>
                </div>
                <div class="item-text">
                  上午
                </div>
              </div>
            </div>
            <div @click="changeTime('afternoon')" class="time-item" :class="{'select' : time === 'afternoon'}">
              <div v-if="time === 'afternoon'" style="position: absolute;right: -10px;top: -10px;color: #c4c4c4">
                <i style="width: 24px;height: 24px;font-size: 24px" class="fa-solid fa-location-dot"></i>
              </div>
              <div class="item-border">
                <div class="item-icon">
                  <i style="color: #e8a51a" class="fa-regular fa-sun"></i>
                </div>
                <div class="item-text">
                  下午
                </div>
              </div>
            </div>
            <div @click="changeTime('evening')" class="time-item" :class="{'select' : time === 'evening'}">
              <div v-if="time === 'evening'" style="position: absolute;right: -10px;top: -10px;color: #c4c4c4">
                <i style="width: 24px;height: 24px;font-size: 24px" class="fa-solid fa-location-dot"></i>
              </div>
              <div class="item-border">
                <div class="item-icon">
                  <i style="color: #489ef8;" class="fa-solid fa-moon"></i>
                </div>
                <div class="item-text">
                  夜晚
                </div>
              </div>
            </div>
            <div @click="changeTime('night')" class="time-item" :class="{'select' : time === 'night'}">
              <div v-if="time === 'night'" style="position: absolute;right: -10px;top: -10px;color: #c4c4c4">
                <i style="width: 24px;height: 24px;font-size: 24px" class="fa-solid fa-location-dot"></i>
              </div>
              <div class="item-border">
                <div class="item-icon">
                  <i style="color: #4970ed" class="fa-solid fa-cloud-moon"></i>
                </div>
                <div class="item-text">
                  凌晨
                </div>
              </div>
            </div>
          </div>
          <div style="width: 100%;height: 64px">
          </div>
          <el-scrollbar style="margin-top: 20px;height: calc(100% - 84px)">
            <div  @click="item !== null ? changeSelect(index) : null" v-for="(item, index) in list" class="event-item" :class="{'event-select' : listSelect === index}">
              <div class="event-item-border">
                <div style="font-size: 32px;display: flex;gap: 10px;align-items: center">
                  <i v-if="item !== null" class="left-icon fa-solid fa-circle-exclamation"></i>
                  <i v-else class="left-icon fa-solid fa-circle-xmark"></i>
                  <div v-if="item === null && index >= 1">
                    EMPTY
                  </div>
                  <div v-else>
                    {{ item.title }}
                  </div>
                </div>
                <div style="font-size: 30px">
                  <i style="height: 32px" class="fa-solid fa-location-dot"></i>
                </div>
              </div>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<style scoped>
.responsive-content:deep(img) {
  max-width: 100%;
  height: auto;
}
.event-select .left-icon {
  color: #ea311b;
}
.event-select .event-item-border {
  color: black !important;
  background-color: #ffcf00 !important;
  font-weight: bold;
}

.left-icon {
  height: 32px
}
.event-item {
  height: calc((100vh - 180px - 168px) / 5 - 24px);
  border-radius: calc(((100vh - 180px - 168px) / 5 - 24px) / 2);
  width: 100%;
  background-color: black;
  box-sizing: border-box;
  border: 2px solid black;
  cursor: pointer;
}
.event-item:not(:last-child) {
  margin-bottom: 20px;
}
.event-item-border {
  width: 100%;
  height: 100%;
  color: #313131;
  border-radius: calc(((100vh - 180px - 168px) / 5 - 24px) / 2);
  border: 4px solid #313131;
  box-sizing: border-box;
  align-items: center;
  justify-content: space-between;
  padding-left: 16px;
  padding-right: 20px;
  display: flex;
}

.select .item-border {
  background-color: #fddc01 !important;
  color: black;
}
.select .item-icon {
  background-color: black;
}
.time-item {
  position: relative;
  cursor: pointer;
  width: 100%;
  box-sizing: border-box;
  border: 2px solid black;
  border-radius: 16px;
  background-color: black;

}
.item-border {
  border: 4px solid #313131;
  box-sizing: border-box;
  border-radius: 16px;
  padding: 4px;
  background-color: black;
  transition: background-color 0.3s ease, color 0.3s ease; /* 添加过渡效果 */
}
.item-icon {
  border-radius: 16px;
  box-sizing: border-box;
  background-color: #313131;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 32px;
}
.item-text {
  margin-top: 10px;
  font-size: 14px;
  font-weight: 550;
  text-align: center;
}
</style>