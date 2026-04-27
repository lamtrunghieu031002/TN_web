export const AUTH_STORAGE_KEY = 'ptit_auth'

// Khi chạy Docker, frontend và backend cùng domain nên dùng /api
// Khi dev local thì dùng localhost:8080
export const API_BASE = import.meta.env.VITE_API_BASE || ''