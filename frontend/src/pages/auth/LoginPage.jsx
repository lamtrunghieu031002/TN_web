

import { useMemo, useState } from 'react' // Nhớ phải có useMemo
import { useNavigate, Link } from 'react-router-dom'
import { API_BASE } from '../../config/api' // Đường dẫn có thể khác tùy thư mục của bạn
import { createApiClient } from '../../services/apiClient'
import * as authService from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'




export function LoginPage(){
    const navigate=useNavigate()

    // lay them thong tin saveAuth de luu thong tin token khi dang nhap

    const { auth, saveAuth } = useAuth()
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('')
    const [form, setForm] = useState({
    username: '',
    password: '',
  })



const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token])
return (
    <section className="mx-auto grid max-w-6xl gap-8 px-4 py-14 md:grid-cols-2 md:items-start">
      <div>
        <h1 className="text-4xl font-extrabold text-slate-800">Đăng nhập</h1>
        <p className="mt-3 text-slate-600">
          Nền tảng thi trắc nghiệm: luyện đề, thi thử có hẹn giờ và theo dõi tiến độ học tập.
        </p>
      </div>

      <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <form
          className="space-y-4"
          onSubmit={async (event) => {
            event.preventDefault()
            setLoading(true)
            setMessage('')
            try {

                // const response = await client.post('/api/auth/login', data);
              // Gọi API đăng nhập
              const response = await authService.login(client, form)
              
              // Lưu data (token, user info) vào LocalStorage & Context
              // Tùy thuộc vào backend trả về gì, bạn có thể chỉnh lại (ví dụ response.data)
              saveAuth(response) 
              
              // Chuyển hướng về trang chủ
              navigate('/', { replace: true })
            } catch (error) {
              setMessage(`Lỗi đăng nhập: ${error.message || 'Sai tài khoản hoặc mật khẩu'}`)
            } finally {
              setLoading(false)
            }
          }}
        >
          <div>
            <input
              className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-brand-red focus:outline-none focus:ring-1 focus:ring-brand-red"
              placeholder="Tên đăng nhập"
              value={form.username}
              onChange={(event) => setForm((prev) => ({ ...prev, username: event.target.value }))}
              required
            />
          </div>

          <div>
            <input
              type="password"
              className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-brand-red focus:outline-none focus:ring-1 focus:ring-brand-red"
              placeholder="Mật khẩu"
              value={form.password}
              onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
              required
            />
          </div>

          <div className="flex justify-end">
            <Link 
              to="/forgot-password" 
              className="text-sm text-brand-red hover:text-brand-redDark hover:underline"
            >
              Quên mật khẩu?
            </Link>
          </div>

          <button
            type="submit"
            disabled={loading || !form.username || !form.password}
            className="w-full rounded-md bg-brand-red px-4 py-2 font-semibold text-white transition-colors hover:bg-brand-redDark disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? 'Đang kiểm tra...' : 'Đăng nhập'}
          </button>
        </form>

        {message ? (
          <p className="mt-4 rounded-md bg-red-50 p-3 text-sm text-red-600 border border-red-100">
            {message}
          </p>
        ) : null}

        <div className="mt-6 text-center text-sm text-slate-600">
          Chưa có tài khoản?{' '}
          <Link 
            to="/register" 
            className="font-semibold text-brand-red hover:text-brand-redDark hover:underline"
          >
            Đăng ký ngay
          </Link>
        </div>
      </div>
    </section>
  )
}