<script setup>
import {onMounted, ref, reactive, onBeforeUnmount, shallowRef, watch} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const editorRef = shallowRef()

const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...', MENU_CONF: {} }

const clearEditorContent = () => {
  const editor = editorRef.value;
  if (editor) {
    // 假设 editor 有一个名为 clear 的方法
    editor.clear(); // 具体方法名需要根据你使用的编辑器来确认
  }
};

editorConfig.MENU_CONF["uploadImage"] = {
  // 自定义上传 InsertFnType
  async customUpload(richPic, insertFn) {
    const formData = new FormData();
    formData.append('file', richPic);
    request.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
        .then(response => {
          ElMessage.success('上传成功')
          insertFn(response);
        })
        .catch(error => {
          ElMessage.error('上传失败:', error);
        });
  },
};

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

const uploadHeader = {'Authorization': 'Bearer ' + sessionStorage.getItem('token')}
const form = reactive({
  title: '',
  content: '',
  preview: '',
  startTime: '',
  endTime: '',
  location: '',
})
const addDialog = ref(false)
const haveData = ref(true)
const pageNum = ref(4)
const isLoading = ref(false)
const dialog = ref(false)
const isJoin = ref(0)
const eventList = ref([])
const commitList = ref([])
const commitForm = reactive({
  eventId: '',
  content: ''
})
const total = ref(0)
const dialogInfo = ref()
const font = reactive({
  color: '#1f1f1f',
  fontSize: 300,
  fontWeight: 700
})

const load = async () => {
  // 检查是否还有数据可加载
  if (eventList.value.length >= total.value) {
    haveData.value = false;
    return;
  }
  if (!isLoading.value) {
    isLoading.value = true;
    try {
      const res = await request.get(`event/list?pageSize=4&pageNum=${pageNum.value}`);
      if (res.code === 200) {
        total.value = res.data.total;
        eventList.value.push(...res.data.list);
        pageNum.value += 1;
      } else {
        console.error('Error fetching data:', res.message);
      }
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      isLoading.value = false;
    }
  }
};

const handleScroll = (event) => {
  const {scrollTop, clientHeight, scrollHeight} = event.target;
  if (scrollTop + clientHeight >= scrollHeight - 10) { // 距离底部30px时加载
    load();
  }
};

onMounted(() => {
  const scrollbarElement = document.querySelector('.el-scrollbar__wrap');
  scrollbarElement.addEventListener('scroll', handleScroll);
  request.get('/event/list?pageNum=1&pageSize=12').then(res => {
    if (res.code === 200) {
      eventList.value = res.data.list
      total.value = res.data.value
    } else
      ElMessage.error(res.message)
  })

});

const openDialog = (row) => {
  row.view += 1
  request.post('/event/addReview', row)
  dialogInfo.value = row
  commitForm.eventId = row.id
  request.get('/commit/getCommitsByEventId/' + row.id).then(res => {
    if (res.code === 200) {
      commitList.value = res.data
    }
  })

  const currentTime = new Date().getTime(); // 获取当前时间
  const endTime = new Date(row.endTime).getTime()
  if (currentTime > endTime) {
    isJoin.value = 0
  } else {
    request.get("/event/isJoinEvent?eventId=" + row.id).then(res => {
      if (res.code === 200) {
        isJoin.value = res.data ? 1 : 2
      }
    })
  }

  dialog.value = true
}

const close = () => {
  addDialog.value = false
  form.title= ''
  form.content= ''
  form.preview= ''
  form.startTime= ''
  form.endTime= ''
  form.location= ''
}

const handleAvatarSuccess = (response) => {
  form.preview = response
  ElMessage.success('上传成功')
}

const save = () => {
  request.post('/event/save', form).then(res => {
    if (res.code === 200) {
      ElMessage.success('申请已提交，等待管理员审核')
      close()
    } else
      ElMessage.error(res.message)
  })
}

const saveCommit = () => {
  if (commitForm.content) {
    request.post('/commit/save', commitForm).then(res => {
      if (res.code === 200) {
        commitList.value.push(res.data)
        ElMessage.success('发布成功')
        commitForm.content = ''
      } else
        ElMessage.error(res.message)
    })
  }
}

const joinEvent = () => {
  request.get('/event/joinEvent?eventId=' + commitForm.eventId).then(res => {
    if (res.code === 200) {
      isJoin.value = 1
      ElMessage.success('报名成功')
    } else
      ElMessage.error(res.message)
  })
}

const outEvent = () => {
  request.get('/event/outEvent?eventId=' + commitForm.eventId).then(res => {
    if (res.code === 200) {
      isJoin.value = 2
      ElMessage.success('退出成功')
    } else
      ElMessage.error(res.message)
  })
}

const scrollbarRef = ref()
const topIsShow = ref(false)

const scroll = ({ scrollTop }) => {
  topIsShow.value = scrollTop > 0;

}

const scrollToTop = () => {
  const scrollEl = scrollbarRef.value;
  if (scrollEl) {
    scrollEl.scrollTo({ top: 0, behavior: 'smooth' }); // 平滑滚动到顶部
  }
};
</script>

<template>
  <div style="width: 100%;height: 100%;position: relative">
    <el-scrollbar @scroll="scroll" ref="scrollbarRef" style="width: 1200px; margin: auto; height: 100%;position: relative">
      <div
          v-masonry
          transition-duration="0.3s"
          item-selector=".infinite-list-item"
          :gutter="20"
      >
        <div
            @click="openDialog(item)"
            v-masonry-tile
            class="infinite-list-item"
            v-for="item in eventList"
            :key="item.id"
        >
          <div style="position: relative">
            <div style="position: absolute;left: 20px; top: 10px">
              <i class="fa-regular fa-eye"></i> {{ item.view }}
            </div>
            <img :src="item.preview"
                 style="width: 100%;border-top-left-radius: 20px;border-top-right-radius: 20px;object-fit: cover"/>
          </div>
          <div style="width: 100%; box-sizing: border-box;padding: 2px 20px 10px;">
            <div style="display: flex;position: relative;gap: 2px">
              <img :src="item.creator.avatarUrl"
                   style="position: absolute;top: -36px;width: 60px;height: 60px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;"
                   alt="avatar"/>
              <div style="width: 60px;height: 24px"></div>
              <div
                  style="color: #606060;flex: 1 0 0;box-sizing: border-box;border-bottom: 1px solid #606060;font-size: 14px;font-weight: bold">
                {{ item.creator.nickname }}
              </div>
            </div>
            <div>
              <el-text line-clamp="2" style="margin-top: 6px;font-weight: bold;color: white;font-size: 16px">
                {{ item.title }}
              </el-text>
            </div>
            <div>
              <el-text v-html="item.content.replace(/<img[^>]*>/g, '')" line-clamp="1" style="margin-top: 6px;font-size: 14px;font-weight: 550">
              </el-text>
            </div>

          </div>
        </div>
      </div>
      <div v-if="!haveData" style="margin-top: 30px; text-align: center;font-size: 24px; color: gray">
        已经到底啦。。。
      </div>

      <el-dialog
          v-model="dialog"
          :show-close="false"
          width="1200"
      >

        <el-watermark :width="1200" :gap="[0, -400]" style="background-color: #010101;height: 600px;  border-top-left-radius: 20px;
  border-bottom-right-radius: 20px;
  border-bottom-left-radius: 20px;" :font="font" :content="['Campus activity', 'Student']">
          <div
              style="justify-content: space-between;display: flex;align-items:center;border-top-left-radius:20px;width: 100%;height: 60px;background-color: black;position: absolute;z-index: 100;box-sizing: border-box;padding-right: 40px;padding-left: 40px">
            <div style="display: flex;align-items: center;gap: 10px">
              <div
                  style="height: 40px;width: 40px;border-radius: 50%;  border: 3px solid #313131;box-sizing: border-box;display: flex;align-items: center;justify-content: center">
                <img :src="dialogInfo.creator.avatarUrl"
                     style="width: 35px;height: 35px;border-radius: 50%;box-sizing: border-box;border: 2px solid black;"
                     alt="avatar"/>
              </div>
              <div>
                <div style="color: #5e5e5e;height: 20px;font-weight: bold">
                  {{ dialogInfo.creator.nickname }}
                </div>
                <div
                    style="color: black;height: 16px;border-radius: 10px;background-color: #5e5e5e;font-size: 12px;margin-top: 4px;padding-right: 8px;padding-left: 8px;width: fit-content">
                  <i class="fa-regular fa-eye" style="margin-right: 4px"></i> {{ dialogInfo.view }}
                </div>
              </div>
              <div style="font-size: 12px;color: #5e5e5e">
                <div style="height: 20px;display: flex;align-items: center;gap: 4px">
                  <i class="fa-solid fa-clock"></i>
                  {{ dialogInfo.startTime }} ~ {{ dialogInfo.endTime }}
                </div>
                <div style="height: 16px;display: flex;align-items: center;gap: 6px;margin-top: 4px">
                  <i class="fa-solid fa-map-location-dot"></i>{{ dialogInfo.location }}
                  <i class="fas fa-users"></i>{{ dialogInfo.tag }}
                </div>
              </div>
            </div>
            <div @click="dialog = false" class="close-icon">
              <i class="fa-solid fa-rectangle-xmark"></i>
            </div>
          </div>
          <div
              style="display: flex;gap:20px;position: absolute;top: 80px;height: 500px;width: 100%;z-index: 100;box-sizing: border-box;padding-right: 40px;padding-left: 40px">
            <div style="width: calc(30% - 10px)">
              <img alt="preview" :src="dialogInfo.preview"
                   style="border-radius: 20px;width: 100%;height: 100%;object-fit: cover;box-sizing: border-box;border: 2px solid #313131;"/>
            </div>
            <div style="padding: 20px 20px 10px;border-radius: 20px;background-color: rgba(0, 0, 0, .7);width: calc(70% - 10px);box-sizing: border-box;">
              <el-scrollbar style="height: calc(100% - 52px)">
                <div style="font-weight: bold">{{ dialogInfo.title }}</div>
                <div style="color: gray;margin-top: 10px" v-html="dialogInfo.content" class="responsive-content"></div>
                <div style="margin-top: 20px">
                  <div v-for="(item, index) in commitList" class="commit-item">
                    <div style="box-sizing: border-box;border: 3px solid #313131;border-radius: 50%; height: 42px;width: 42px">
                      <img style="box-sizing: border-box;border: 2px solid #111111;border-radius: 50%; height: 37px;width: 37px" :src="item.creator.avatarUrl"/>
                    </div>
                    <div style="flex: 1 0 0">
                      <div style="display: flex;align-items: center;justify-content: space-between;line-height: 24px">
                        <div style="color: #cfd3dc">
                          {{ item.creator.nickname }}
                        </div>
                        <div style="background-color: #616161;
                        box-sizing: border-box;
                        padding: 3px 12px;
                        font-weight: 550;
                        font-size: 14px;
                        line-height: 14px;
                        color: black;
                        height: 20px;
                        border-bottom-right-radius: 10px;
                        border-bottom-left-radius: 10px;
                        border-top-right-radius: 10px">
                          {{ index + 1 }}F
                        </div>
                      </div>
                      <div style="margin-top: 6px;line-height: 24px">
                        {{ item.content }}
                      </div>
                    </div>
                  </div>
                </div>
              </el-scrollbar>

              <div style="display: flex;margin-top: 20px">
                <el-input v-model="commitForm.content" class="custom-input" placeholder="善于结善缘，恶语伤人心"/>
                <el-button @click="saveCommit" type="success" plain style="width: 120px;margin-left: 20px">发表评论</el-button>
                <el-button v-if="isJoin === 0" disabled type="danger">不在时间</el-button>
                <el-button v-if="isJoin === 2" @click="joinEvent" type="warning" plain style="width: 120px">加入活动</el-button>
                <el-button v-if="isJoin === 1" @click="outEvent" type="danger" plain style="width: 120px">退出活动</el-button>
              </div>
            </div>

          </div>
        </el-watermark>
      </el-dialog>

      <el-dialog
          v-model="addDialog"
          :title="'申请活动'"
          width="1000"
          :before-close="close"
          style="box-sizing: border-box;padding: 20px"
      >
        <el-row :gutter="20"  style="margin-bottom: 20px">
          <el-col :span="6">
            <el-input placeholder="请输入活动标题" v-model="form.title"/>
          </el-col>
          <el-col :span="6">
            <el-input placeholder="请输入活动举办地点" v-model="form.location"/>
          </el-col>
          <el-col :span="6">
            <el-date-picker
                style="width: 100%"
                v-model="form.startTime"
                type="datetime"
                placeholder="请选择活动开始时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                align="right"
                clearable
            ></el-date-picker>
          </el-col>
          <el-col :span="6">
            <el-date-picker
                style="width: 100%"
                v-model="form.endTime"
                type="datetime"
                placeholder="请选择活动结束时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                align="right"
                clearable
            ></el-date-picker>
          </el-col>
        </el-row>
        <el-row style="margin-bottom: 20px" :gutter="20">
          <el-col :span="8">
            <div style="height: 40px;font-size: 18px;">请上传封面图</div>
            <el-upload
                class="avatar-uploader"
                action="http://localhost:8080/file/upload"
                :show-file-list="false"
                :headers="uploadHeader"
                :on-success="handleAvatarSuccess"
            >
              <img v-if="form.preview" :src="form.preview" class="avatar" alt="preview"/>
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-col>
          <el-col :span="16">
            <div style="border: 1px solid #ccc">
              <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  :mode="'default'"
              />
              <Editor
                  style="height: 320px; overflow-y: hidden;"
                  v-model="form.content"
                  :defaultConfig="editorConfig"
                  :mode="'default'"
                  @onCreated="handleCreated"
              />
            </div>
          </el-col>
        </el-row>
        <div style="display: flex;align-items: center;justify-content: center">
          <el-button type="danger" plain @click="close" style="width: 200px">取消</el-button>
          <el-button type="primary" plain @click="save" style="width: 200px">确定</el-button>
        </div>

      </el-dialog>
    </el-scrollbar>

    <div v-if="topIsShow" class="edit" @click="scrollToTop" style="bottom: 90px">
      <i style="font-size: 20px;" class="fa-solid fa-arrows-up-to-line"></i>
    </div>

    <div class="edit" @click="addDialog = true">
      <i style="font-size: 20px;" class="fa-solid fa-pen-to-square"></i>
    </div>
  </div>
</template>

<style scoped>
.responsive-content:deep(img) {
  max-width: 100%;
  height: auto;
}
.commit-item {
  display: flex;
  gap: 10px;
  box-sizing: border-box;
  padding-top: 10px;
  padding-bottom: 10px;
  color: gray;
  border-bottom: 3px solid #313131;
}
.avatar-uploader .avatar {
  width: 309px;
  object-fit: cover;
  height: 402px;
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
.edit {
  display: flex;
  cursor: pointer;
  position: absolute;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  box-sizing: border-box;
  border: 4px solid #313131;
  background-color: black;
  align-items: center;
  justify-content: center;
}
.edit:active {
  border: 4px solid #ffcf00;
}

:deep(.el-dialog) {
  padding: 0;
  border: 4px solid #313131;
  border-top-left-radius: 20px;
  border-bottom-right-radius: 20px;
  border-bottom-left-radius: 20px;
}

:deep(.el-dialog .el-dialog__header) {
  display: none;
}

.infinite-list-item {
  width: calc(25% - 20px);
  margin-bottom: 20px;
  height: fit-content;
  /* 可选：增加圆角 */
  border-radius: 20px 20px 0 20px;
  box-sizing: border-box;
  border: 3px solid black;
  background-color: #222222;
  cursor: pointer;
}

.infinite-list-item:active {
  border: 3px solid #ffcf00;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 309px;
  height: 402px;
  text-align: center;
}
</style>