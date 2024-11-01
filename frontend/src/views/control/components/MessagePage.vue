<script setup>
import {ref, onMounted} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

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
  request.get('/message/list?pageSize=5&pageNum=' + pageNum.value + '&content=' + searchQuery.value).then(res => {
    if (res.code === 200) {
      console.log(res.data)
      tableData.value = res.data.list
      total.value = res.data.total
    } else
      ElMessage.error(res.message)
  })
}


const removeItem = (id) => {
  request.delete('/message/delete?id=' + id).then(res => {
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


</script>

<template>
  <div style="font-size: 24px; font-weight: bold; margin-bottom: 20px;">
    消息管理
  </div>

  <div style="margin-bottom: 20px; font-size: 16px;">
    <p style="color: #555;">这里是消息管理模块，您可以在此查看和管理所有消息信息。</p>
  </div>

  <div style="margin-bottom: 20px;">
    <el-input placeholder="搜索评论" v-model="searchQuery" style="width: 240px; margin-right: 10px;"></el-input>
    <el-button type="primary" @click="getList" plain>搜索</el-button>
    <el-button type="warning" @click="resize" plain>重置</el-button>
  </div>

  <el-table border stripe :data="tableData" style="width: 100%;margin-bottom: 20px">
    <el-table-column prop="id" label="id"  />
    <el-table-column prop="content" label="内容"  />
    <el-table-column prop="fromUserName" label="发起人"  />
    <el-table-column prop="toUserName" label="接收人"  />
    <el-table-column label="操作">
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

