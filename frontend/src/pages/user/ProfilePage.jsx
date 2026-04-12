import { useMemo, useState } from 'react'
import { API_BASE } from '../../config/api'
import { createApiClient } from '../../services/apiClient'
import * as userService from '../../services/userService'
import { useAuth } from '../../hooks/useAuth'
import { ChangePassword } from '../../components/ChangePassword'

export function ProfilePage() {
  const { auth } = useAuth()
  const [message, setMessage] = useState('')
  const [result, setResult] = useState(null)
  const [profileForm, setProfileForm] = useState({ fullName: '', Email: '', Phone: '' })
  const [pwForm, setPwForm] = useState({ oldPassword: '', newPassword: '' })

  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token])
  const username = auth?.username || ''

  const withFeedback = async (action, okMsg) => {
    try {
      const data = await action()
      setMessage(okMsg)
      setResult(data)
    } catch (error) {
      setMessage(`Lỗi: ${error.message}`)
      setResult(null)
    }
  }

  return (
    <section className="mx-auto max-w-6xl px-4 py-14">
      <h1 className="text-4xl font-extrabold text-slate-800">Tài khoản</h1>
      <p className="mt-2 text-slate-600">Xin chào, {username}</p>

      {message ? <p className="mt-4 rounded-md bg-slate-100 p-2 text-sm">{message}</p> : null}

      <div className="mt-8 grid gap-6 md:grid-cols-2">
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-bold text-brand-red">Hồ sơ</h2>
          <div className="mt-4 space-y-3">
            <button
              type="button"
              className="rounded-md bg-brand-red px-4 py-2 text-sm font-semibold text-white hover:bg-brand-redDark"
              onClick={() =>
                withFeedback(() => userService.getUserProfile(client, username), 'Lấy profile thành công')
              }
            >
              Lấy profile
            </button>

            <div className="grid gap-3">
              <input
                className="w-full rounded-md border border-slate-300 px-3 py-2"
                placeholder="Full name"
                value={profileForm.fullName}
                onChange={(e) => setProfileForm((p) => ({ ...p, fullName: e.target.value }))}
              />
              <input
                className="w-full rounded-md border border-slate-300 px-3 py-2"
                placeholder="Email (field: Email)"
                value={profileForm.Email}
                onChange={(e) => setProfileForm((p) => ({ ...p, Email: e.target.value }))}
              />
              <input
                className="w-full rounded-md border border-slate-300 px-3 py-2"
                placeholder="Phone (field: Phone)"
                value={profileForm.Phone}
                onChange={(e) => setProfileForm((p) => ({ ...p, Phone: e.target.value }))}
              />
              <button
                type="button"
                className="rounded-md border border-slate-300 px-4 py-2 text-sm font-semibold hover:bg-slate-50"
                onClick={() =>
                  withFeedback(
                    () => userService.updateProfile(client, username, profileForm),
                    'Cập nhật profile thành công',
                  )
                }
              >
                Cập nhật profile
              </button>
            </div>
          </div>
        </div>

       <ChangePassword />
      </div>

      <div className="mt-8 rounded-2xl bg-slate-900 p-4 text-slate-100">
        <pre className="overflow-auto text-xs">{result ? JSON.stringify(result, null, 2) : 'Chưa có dữ liệu'}</pre>
      </div>
    </section>
  )
}

