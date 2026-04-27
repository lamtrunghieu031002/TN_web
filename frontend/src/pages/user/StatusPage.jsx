import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { AUTH_STORAGE_KEY } from '../../constants/storage'
import { API_BASE } from '../../config/api'

export function StatusPage() {
  const [submissions, setSubmissions] = useState([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const getHeaders = () => {
    try {
      const raw = localStorage.getItem(AUTH_STORAGE_KEY)
      const auth = raw ? JSON.parse(raw) : null
      return { headers: { Authorization: auth?.token ? `Bearer ${auth.token}` : '' } }
    } catch { return { headers: {} } }
  }

  useEffect(() => {
    axios.get(`${API_BASE}/api/submissions/status`, getHeaders())
      .then(res => setSubmissions(Array.isArray(res.data) ? res.data : []))
      .catch(() => setSubmissions([]))
      .finally(() => setLoading(false))
  }, [])

  return (
    <main className="mx-auto max-w-6xl px-4 py-8">
      <h1 className="text-2xl font-bold text-slate-800 mb-6">📊 Trạng thái nộp bài</h1>

      {loading ? (
        <div className="text-center text-slate-400 py-16">Đang tải...</div>
      ) : (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          {/* Header */}
          <div className="grid grid-cols-12 text-xs font-bold uppercase text-slate-400 px-6 py-3 bg-red-50 border-b border-slate-200">
            <span className="col-span-1">STT</span>
            <span className="col-span-2">Thời gian</span>
            <span className="col-span-2">Tài khoản</span>
            <span className="col-span-4">Bài tập</span>
            <span className="col-span-1 text-center">Kết quả</span>
            <span className="col-span-2 text-center">Thực thi</span>
          </div>

          {/* Rows */}
          <div className="divide-y divide-slate-100">
            {submissions.length === 0 ? (
              <div className="text-center text-slate-400 py-16">Chưa có lượt nộp bài nào</div>
            ) : submissions.map((s, i) => (
              <div
                key={s.id ?? i}
                className="grid grid-cols-12 items-center px-6 py-3 hover:bg-slate-50 transition"
              >
                <span className="col-span-1 text-slate-400 text-sm">{s.id}</span>

                <span className="col-span-2 text-slate-500 text-xs">{s.submittedAt}</span>

                <span className="col-span-2 text-slate-700 text-sm font-medium">
                  {s.username}
                </span>

                <span
                  onClick={() => navigate(`/problems/${s.problemId}`)}
                  className="col-span-4 text-blue-600 hover:underline text-sm cursor-pointer truncate"
                >
                  {s.problemTitle}
                </span>

                <span className={`col-span-1 text-center text-sm font-bold ${
                  s.status === 'AC' ? 'text-green-600' : 'text-red-500'
                }`}>
                  {s.status}
                </span>

                <span className="col-span-2 text-center text-slate-500 text-sm">
                  {(s.executionTimeMs / 1000).toFixed(3)}s
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </main>
  )
}