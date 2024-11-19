<script setup>
import {ref, onMounted, reactive} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const dialog = ref(false)
const tableData = ref([])
const total = ref(0)
const form = reactive({
  id: '',
  name: '',
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
  request.get('/tag/list?pageSize=5&pageNum=' + pageNum.value + '&name=' + searchQuery.value).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    } else
      ElMessage.error(res.message)
  })
}

const updateDialog = (row) => {
  form.id = row.id
  form.name = row.name
  form.formType = 'update'
  dialog.value = true
}


const removeItem = (id) => {
  request.delete('/tag/delete?id=' + id).then(res => {
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

const close = () => {
  dialog.value = false
  form.id = ''
  form.name = ''
  form.formType = ''
}

const save = () => {
  request.post('/tag/save', form).then(res => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
      close()
      pageNum.value = 1
      getList()
    } else
      ElMessage.error(res.message)
  })
}
</script>

<template>
  <div style="font-size: 24px; font-weight: bold; margin-bottom: 20px;">
    组织管理
  </div>

  <div style="margin-bottom: 20px; font-size: 16px;">
    <p style="color: #555;">这里是组织管理模块，您可以在此查看和管理所有组织信息。</p>
  </div>

  <div style="margin-bottom: 20px;">
    <el-input placeholder="搜索组织" v-model="searchQuery" style="width: 240px; margin-right: 10px;"></el-input>
    <el-button type="primary" @click="getList" plain>搜索</el-button>
    <el-button type="warning" @click="resize" plain>重置</el-button>
    <el-button type="success" @click="dialog = true" plain>新增</el-button>
  </div>

  <el-table border stripe :data="tableData" style="width: 100%;margin-bottom: 20px">
    <el-table-column prop="id" label="id"  />
    <el-table-column prop="name" label="name"  />
    <el-table-column label="操作">
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
      :title="form.formType !== 'update' ? '新增组织' : '更新组织'"
      width="450"
      top="40vh"
      :before-close="close"
  >
    <el-form :model="form" label-width="auto">
      <el-form-item label="组织名">
        <el-input v-model="form.name"/>
      </el-form-item>
    </el-form>
    <div style="display: flex;align-items: center;justify-content: center">
      <el-button type="danger" plain @click="close" style="width: 120px">取消</el-button>
      <el-button type="primary" plain @click="save" style="width: 120px">确定</el-button>
    </div>

  </el-dialog>
</template>

