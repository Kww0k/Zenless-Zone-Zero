<script setup>
import {ref, onMounted, onBeforeUnmount, shallowRef, reactive} from 'vue'
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
const dialog = ref(false)
const tableData = ref([])
const total = ref(0)
const form = reactive({
  id: '',
  title: '',
  content: '',
  preview: '',
  startTime: '',
  endTime: '',
  location: '',
  examine: '',
  formType: ''
})
const searchQuery = ref('')
const pageNum = ref(1)

const handleCurrentChange = (index) => {
  pageNum.value = index
  getList()
}

onMounted(() => {
  getList()
})

const getList = () => {
  request.get('/event/list?pageSize=5&pageNum=' + pageNum.value + '&title=' + searchQuery.value).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    } else
      ElMessage.error(res.message)
  })
}

const close = () => {
  dialog.value = false
  form.id = ''
  form.title= ''
  form.content= ''
  form.preview= ''
  form.startTime= ''
  form.endTime= ''
  form.examine= ''
  form.location= ''
  form.formType= ''
}

const handleAvatarSuccess = (response) => {
  form.preview = response
  ElMessage.success('上传成功')
}

const save = () => {
  request.post('/event/save', form).then(res => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
      close()
      pageNum.value = 1
      getList()
    } else
      ElMessage.error(res.message)
  })
}

const parsedTitle = (html) => {
  // 将 HTML 转换为文本，防止 XSS 和解析不当
  // 这里需要你自己实现转义或者使用一个库来处理
  const tempDiv = document.createElement('div');
  tempDiv.innerHTML = html.replace(/<img[^>]*>/g, '');
  return tempDiv.innerText || tempDiv.textContent;
}

const updateDialog = (row) => {
  form.id = row.id
  form.preview = row.preview
  form.title = row.title
  form.location = row.location
  form.startTime = row.startTime
  form.endTime = row.endTime
  form.content = row.content
  form.examine = row.examine
  form.formType = 'update'
  dialog.value = true
}

const removeItem = (id) => {
  request.delete('/event/delete?id=' + id).then(res => {
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getList()
    } else
      ElMessage.error(res.message)
  })
}

const resize = () => {
  pageNum.value = 1;
  searchQuery.value = ''
  getList()
}

const changeExamine = (row) => {
  request.post('/event/save', row).then(res => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
    } else
      ElMessage.error(res.message)
  })
}
</script>

<template>
  <div style="font-size: 24px; font-weight: bold; margin-bottom: 20px;">
    活动管理
  </div>

  <div style="margin-bottom: 20px; font-size: 16px;">
    <p style="color: #555;">这里是活动管理模块，您可以在此查看和管理所有活动信息。</p>
  </div>

  <div style="margin-bottom: 20px;">
    <el-input placeholder="搜索活动" v-model="searchQuery" style="width: 240px; margin-right: 10px;"></el-input>
    <el-button type="primary" @click="getList" plain>搜索</el-button>
    <el-button type="warning" @click="resize" plain>重置</el-button>
    <el-button type="success" @click="dialog = true" plain>新增</el-button>
  </div>

  <el-table border stripe :data="tableData" style="width: 100%;margin-bottom: 20px">
    <el-table-column prop="id" label="id" width="50" />
    <el-table-column prop="title" label="标题" />
    <el-table-column label="内容">
      <template v-slot:default="scope">
        <el-row class="mb-2">
          <el-text :line-clamp="1" :title="parsedTitle(scope.row.content)"
                   v-html="scope.row.content.replace(/<img[^>]*>/g, '')" truncated></el-text>
        </el-row>
      </template>
    </el-table-column>
    <el-table-column prop="location" label="举办地" />
    <el-table-column prop="startTime" label="开始时间" />
    <el-table-column prop="endTime" label="结束时间" />

    <el-table-column prop="examine" label="是否过审">
      <template v-slot:default="scope">
        <el-switch
            v-model="scope.row.examine"
            style="--el-switch-on-color: #13ce66;"
            active-value="yes"
            inactive-value="no"
            @change="changeExamine(scope.row)"
        ></el-switch>
      </template>
    </el-table-column>

    <el-table-column label="操作" width="200">
      <template v-slot:default="scope">
        <el-button @click="updateDialog(scope.row)" type="success"><el-icon style="margin-right: 6px"><InfoFilled /></el-icon>修改</el-button>
        <el-popconfirm @confirm="removeItem(scope.row.id)" title="你确定要删除这条信息吗?">
          <template #reference>
            <el-button type="danger"><el-icon style="margin-right: 6px"><CircleCloseFilled /></el-icon>删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-table-column>
  </el-table>

  <div style="width: 100%;display: flex;justify-content: center">
    <el-pagination v-model="pageNum" :page-size="5" @current-change="handleCurrentChange" layout="prev, pager, next" :total="total" />
  </div>
  <el-dialog
      v-model="dialog"
      :title="form.formType !== 'update' ? '新增活动' : '更新活动'"
      width="1000"
      :before-close="close"
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
</template>

<style scoped>
.avatar-uploader .avatar {
  width: 309px;
  object-fit: cover;
  height: 402px;
  display: block;
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