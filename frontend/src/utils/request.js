import axios from "axios";
import {ElMessage} from "element-plus";


const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

request.interceptors.request.use(config => {
    let token = sessionStorage.getItem("token")
    if (token !== null) {
        config.headers["Authorization"] = "Bearer " + token
    }
    return config
})

request.interceptors.response.use(res => {
        if (res.status === 200)
            return res.data
        else
            ElMessage.error("请求错误")
    }, error => {
        ElMessage.error("请求错误")
        return error
    }
)

export default request;