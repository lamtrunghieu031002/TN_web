import axios from 'axios'
import { AUTH_STORAGE_KEY } from '../constants/storage'
import { API_BASE } from '../config/api'

// 1. Khởi tạo một instance riêng để cấu hình chung
const api = axios.create({
  baseURL: API_BASE,
})

// 2. Tự động đính kèm Token vào MỌI request (Interceptor)
api.interceptors.request.use(
  (config) => {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    if (raw) {
      try {
        const auth = JSON.parse(raw)
        if (auth?.token) {
          config.headers.Authorization = `Bearer ${auth.token}`
        }
      } catch (e) {
        console.error("Lỗi parse token:", e)
      }
    }
    return config
  },
  (error) => Promise.reject(error)
)

// --- CÁC HÀM SERVICE BÂY GIỜ SẼ CỰC KỲ GỌN ---

// Lấy danh sách bài (Đã sửa logic lọc)
export const getProblems = (client, difficulty, topic) => {
  const params = {}

  // Chỉ gửi param lên nếu giá trị khác 'ALL'
  if (difficulty && difficulty !== 'ALL') {
    params.difficulty = difficulty
  }
  if (topic && topic !== 'ALL') {
    params.topic = topic
  }

  const query = new URLSearchParams(params).toString()
  const path = query ? `/api/problems/filter?${query}` : '/api/problems/filter'
  return client.request(path, { useAuth: true })
}

// Lấy danh sách topics
export const getTopics = (client) => client.request('/api/problems/topics', { useAuth: true })

// Lấy chi tiết 1 bài
export const getProblemById = (client, id) => client.request(`/api/problems/${id}`, { useAuth: true })

// Nộp bài
export const submitSolution = (client, problemId, userQuery) =>
  client.request('/api/submissions/submit', {
    method: 'POST',
    body: { problemId, userQuery },
    useAuth: true,
  })

// Lịch sử nộp bài
export const getHistory = (problemId) => 
  api.get(`/api/submissions/history/${problemId}`)

// Thống kê & Leaderboard
export const getMyStats = () => api.get('/api/submissions/stats')
export const getLeaderboard = () => api.get('/api/submissions/leaderboard')