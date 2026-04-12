import { useMemo, useState } from 'react'
import { API_BASE } from '../../config/api'
import { createApiClient } from '../../services/apiClient'
import * as userService from '../../services/userService'
import { useAuth } from '../../hooks/useAuth'

export function AdminDashboard() {
  const { auth } = useAuth()
  const [message, setMessage] = useState('')
  const [users, setUsers] = useState(null)

  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token])

  return (
    <section className="mx-auto max-w-6xl px-4 py-14">
      <h1 className="text-4xl font-extrabold text-slate-800">Admin</h1>
      <p className="mt-2 text-slate-600">Danh sách người dùng (cần quyền ADMIN).</p>

      <div className="mt-6">
        <button
          type="button"
          className="rounded-md bg-brand-red px-4 py-2 text-sm font-semibold text-white hover:bg-brand-redDark"
          onClick={async () => {
            setMessage('')
            try {
              const data = await userService.getAllUsers(client)
              setUsers(data)
              setMessage('Lấy danh sách user thành công')
            } catch (error) {
              setUsers(null)
              setMessage(`Lỗi: ${error.message}`)
            }
          }}
        >
          GET /api/users
        </button>
      </div>

      {message ? <p className="mt-4 rounded-md bg-slate-100 p-2 text-sm">{message}</p> : null}

      <div className="mt-6 rounded-2xl bg-slate-900 p-4 text-slate-100">
        <pre className="overflow-auto text-xs">{users ? JSON.stringify(users, null, 2) : 'Chưa có dữ liệu'}</pre>
      </div>
    </section>
  )
}

