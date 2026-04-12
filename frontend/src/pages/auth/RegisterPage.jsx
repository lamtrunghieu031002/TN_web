import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { API_BASE } from '../../config/api'
import { createApiClient } from '../../services/apiClient'
import * as authService from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'

export function RegisterPage() {
  const navigate = useNavigate()
  const { auth } = useAuth()
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    role: 'student',
  })

  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token])

  return (
    <section className="mx-auto grid max-w-6xl gap-8 px-4 py-14 md:grid-cols-2 md:items-start">
      <div>
        <h1 className="text-4xl font-extrabold text-slate-800">Đăng ký</h1>
        <p className="mt-3 text-slate-600">Tạo tài khoản để tham gia thi thử và lưu kết quả.</p>
      </div>

      <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <form
          className="space-y-3"
          onSubmit={async (event) => {
            event.preventDefault()
            setLoading(true)
            setMessage('')
            try {
              await authService.register(client, form)
              navigate('/login', { replace: true })
            } catch (error) {
              setMessage(`Lỗi đăng ký: ${error.message}`)
            } finally {
              setLoading(false)
            }
          }}
        >
          <input
            className="w-full rounded-md border border-slate-300 px-3 py-2"
            placeholder="Username"
            value={form.username}
            onChange={(event) => setForm((prev) => ({ ...prev, username: event.target.value }))}
          />
          <input
            className="w-full rounded-md border border-slate-300 px-3 py-2"
            placeholder="Email"
            value={form.email}
            onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
          />
          <input
            type="password"
            className="w-full rounded-md border border-slate-300 px-3 py-2"
            placeholder="Password"
            value={form.password}
            onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
          />
          <select
            className="w-full rounded-md border border-slate-300 px-3 py-2"
            value={form.role}
            onChange={(event) => setForm((prev) => ({ ...prev, role: event.target.value }))}
          >
            <option value="student">student</option>
            <option value="teacher">teacher</option>
            <option value="admin">admin</option>
          </select>
          <button
            disabled={loading}
            className="w-full rounded-md bg-brand-red px-4 py-2 font-semibold text-white hover:bg-brand-redDark disabled:opacity-60"
          >
            {loading ? 'Đang xử lý...' : 'Đăng ký'}
          </button>
        </form>

        {message ? <p className="mt-3 rounded-md bg-slate-100 p-2 text-sm">{message}</p> : null}
      </div>
    </section>
  )
}

