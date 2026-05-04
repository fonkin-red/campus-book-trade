import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// TODO: 组员补充请求拦截器（携带 Token）

// TODO: 组员补充响应拦截器（统一错误处理）

export default request
