<script setup>
import {ref, onMounted} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const dialog = ref(false)
const tableData = ref([])
const total = ref(0)
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
  request.get('/commit/list?pageSize=5&pageNum=' + pageNum.value + '&title=' + searchQuery.value).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
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


const removeItem = (id) => {
  request.delete('/commit/delete?id=' + id).then(res => {
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

const formatDate = (row) => {
  return dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss'); // 可以根据需求调整格式
}
</script>

<template>
  <div style="font-size: 24px; font-weight: bold; margin-bottom: 20px;">
    评论管理
  </div>

  <div style="margin-bottom: 20px; font-size: 16px;">
    <p style="color: #555;">这里是评论管理模块，您可以在此查看和管理所有评论信息。</p>
  </div>

  <div style="margin-bottom: 20px;">
    <el-input placeholder="搜索评论" v-model="searchQuery" style="width: 240px; margin-right: 10px;"></el-input>
    <el-button type="primary" @click="getList" plain>搜索</el-button>
    <el-button type="warning" @click="resize" plain>重置</el-button>
  </div>

  <el-table border stripe :data="tableData" style="width: 100%;margin-bottom: 20px">
    <el-table-column prop="id" label="id" width="50" />
    <el-table-column label="内容">
      <template v-slot:default="scope">
        <el-row class="mb-2">
          <el-text :line-clamp="1" :title="parsedTitle(scope.row.content)"
                   v-html="scope.row.content.replace(/<img[^>]*>/g, '')" truncated></el-text>
        </el-row>
      </template>
    </el-table-column>

    <el-table-column label="活动标题">
      <template v-slot:default="scope">
        <el-row class="mb-2">
          <el-text :line-clamp="1" :title="parsedTitle(scope.row.event.title)"
                   v-html="scope.row.event.title.replace(/<img[^>]*>/g, '')" truncated></el-text>
        </el-row>
      </template>
    </el-table-column>

    <el-table-column label="评论人">
      <template v-slot:default="scope">
        <el-row class="mb-2">
          <el-text :line-clamp="1" :title="parsedTitle(scope.row.creator.nickname)"
                   v-html="scope.row.creator.nickname.replace(/<img[^>]*>/g, '')" truncated></el-text>
        </el-row>
      </template>
    </el-table-column>

    <el-table-column prop="createTime" label="评论时间"/>

    <el-table-column label="操作" width="200">
      <template v-slot:default="scope">
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
</template>

