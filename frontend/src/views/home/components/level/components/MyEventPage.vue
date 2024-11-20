<script setup>
import {onMounted, ref, reactive, onBeforeUnmount, shallowRef} from 'vue'
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
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
const list = ref([])
const updateDialog = ref(false)
const form = reactive({
  id: '',
  title: '',
  content: '',
  preview: '',
  startTime: '',
  endTime: '',
  location: '',
  examine: 'no'
})

const getList = () => {
  request.get('/event/myList').then(res => {
    if (res.code === 200)
      list.value = res.data
  })
}

onMounted(() => {
  getList()
})

const openDialog = (item) => {
  updateDialog.value = true
  form.id = item.id
  form.title= item.title
  form.content= item.content
  form.preview= item.preview
  form.startTime= item.startTime
  form.endTime= item.endTime
  form.location= item.location
}

const close = () => {
  updateDialog.value = false
  form.title= ''
  form.id = ''
  form.content= ''
  form.preview= ''
  form.startTime= ''
  form.endTime= ''
  form.location= ''
}

const save = () => {
  request.post('/event/save', form).then(res => {
    if (res.code === 200) {
      ElMessage.success('申请已提交，等待管理员审核')
      getList()
      close()
    } else
      ElMessage.error(res.message)
  })
}

const handleAvatarSuccess = (response) => {
  form.preview = response
  ElMessage.success('上传成功')
}

const open = (id) => {
  ElMessageBox.confirm(
      '你确定要删除这个活动吗？',
      '提醒',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'is-plain el-button--danger', // 添加 plain 样式
        cancelButtonClass: 'is-plain el-button--success' // 添加 plain 样式
      })
      .then(() => {
        request.delete('/event/delete?id=' + id).then(res => {
          if (res.code === 200) {
            ElMessage.success('删除成功')
            getList()
          } else
            ElMessage.error(res.message)
        })
      })
}
</script>

<template>
  <div style="height: 100%; width: 100%">
    <el-scrollbar style="width: 100%;height: 100%">
      <div style="width: 100%; height: 100%;display: grid;grid-template-columns: repeat(4, 1fr);gap: 20px">
        <div class="card" v-for="item in list">
          <div style="position: relative">
            <div style="position: absolute;left: 8px; top: 10px">
              <i class="fa-regular fa-eye"></i> {{ item.view }}
            </div>
            <img :src="item.preview"
                 style="width: 100%;object-fit: cover; height: 150px"/>
          </div>
          <div style="box-sizing: border-box;padding-left: 8px;padding-right:8px;display: flex;justify-content: space-between;align-items: center">
            <el-text style="font-size: 14px;width: 180px" truncated>{{ item.title }}</el-text>
          </div>
          <div style="margin-top: 4px;margin-bottom:4px;box-sizing: border-box;padding-left: 8px;padding-right:8px;display: flex;justify-content: space-between;align-items: center">
            <div style="background: #2a598a;font-size: 12px;width: fit-content;border-radius: 4px;box-sizing: border-box;padding: 2px" v-if="item.examine === 'yes'">已过审</div>
            <div style="background: #854040;font-size: 12px;width: fit-content;border-radius: 4px;box-sizing: border-box;padding: 2px" v-else>审核中</div>
            <el-popover popper-style="padding: 5px 5px 0px" :width="100">
              <template #reference>
                <div style="cursor:pointer;;height: 20px;width: 20px;border-radius: 50%;background: black;display: flex;align-items: center;justify-content: center">
                  <i class="fa-solid fa-ellipsis"></i>
                </div>
              </template>
              <div class="popover-item" @click="openDialog(item)">
                <i class="fa-solid fa-pen-to-square" style="margin-right: 4px"></i>修改活动信息
              </div>

              <div class="popover-item" @click="open(item.id)">
                <i class="fa-solid fa-trash-can" style="margin-right: 4px"></i>删除活动信息
              </div>
            </el-popover>
          </div>
        </div>
      </div>
    </el-scrollbar>

    <el-dialog
        v-model="updateDialog"
        :title="'更新活动'"
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
  </div>
</template>

<style scoped>
.popover-item {
  width: 100%;
  font-size: 12px;
  text-align: center;
  box-sizing: border-box;
  padding: 4px;
  border-radius: 4px;
  cursor: pointer;
}
.popover-item:not(:last-child) {
  margin-bottom: 5px;
}
.popover-item:hover {
  background-color: #409eff1A;
}
.card {
  width: 95%;
  overflow: hidden;
  background: #1d1e1f;
  box-sizing: border-box;
  border: 2px solid #414243;
  border-radius: 6px;
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

.avatar-uploader .avatar {
  width: 309px;
  object-fit: cover;
  height: 402px;
  display: block;
}

</style>

<style>

</style>