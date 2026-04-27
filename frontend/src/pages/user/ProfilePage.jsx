import { useEffect, useMemo, useState } from 'react'
import { API_BASE } from '../../config/api'
import { createApiClient } from '../../services/apiClient'
import { useAuth } from '../../hooks/useAuth'
import { ChangePassword } from '../../components/ChangePassword'

export function ProfilePage() {
  const { auth } = useAuth()
  const [stats, setStats] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)

  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token])

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsData, historyData] = await Promise.all([
          client.request('/api/submissions/stats', { useAuth: true }),
          client.request('/api/submissions/my-history', { useAuth: true })
        ])
        setStats(statsData)
        setHistory(historyData || [])
      } catch (err) {
        console.error("Lỗi tải dữ liệu profile:", err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [client])

  if (loading) return <div className="text-center py-20 text-slate-400">Đang tải hồ sơ...</div>

  return (
    <section className="mx-auto max-w-6xl px-4 py-12">
      <div className="flex items-center gap-6 mb-10">
        <div className="h-20 w-20 rounded-full bg-red-100 flex items-center justify-center text-red-600 text-3xl font-bold shadow-sm">
          {auth?.username?.[0]?.toUpperCase()}
        </div>
        <div>
          <h1 className="text-3xl font-extrabold text-slate-800">{auth?.username}</h1>
          <p className="text-slate-500">Thành viên từ 2026</p>
        </div>
      </div>

      <div className="grid gap-8 lg:grid-cols-3">
        {/* Cột trái: Stats */}
        <div className="lg:col-span-1 space-y-6">
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-lg font-bold text-slate-800 mb-4">Thống kê bài giải</h2>
            <div className="space-y-4">
              <StatItem label="Tổng số bài giải" value={stats?.totalSolved || 0} color="text-slate-800" />
              <div className="h-2 w-full bg-slate-100 rounded-full overflow-hidden flex">
                 <div style={{ width: `${(stats?.totalEasy/stats?.totalSolved)*100 || 0}%` }} className="bg-green-500 h-full"></div>
                 <div style={{ width: `${(stats?.totalMedium/stats?.totalSolved)*100 || 0}%` }} className="bg-yellow-500 h-full"></div>
                 <div style={{ width: `${(stats?.totalHard/stats?.totalSolved)*100 || 0}%` }} className="bg-red-500 h-full"></div>
              </div>
              <div className="grid grid-cols-3 gap-2 pt-2">
                <MiniStat label="Easy" value={stats?.totalEasy || 0} color="text-green-600" />
                <MiniStat label="Medium" value={stats?.totalMedium || 0} color="text-yellow-600" />
                <MiniStat label="Hard" value={stats?.totalHard || 0} color="text-red-600" />
              </div>
            </div>
          </div>
          
          <ChangePassword />
        </div>

        {/* Cột phải: History */}
        <div className="lg:col-span-2">
          <div className="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
            <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
              <h2 className="font-bold text-slate-800">Lịch sử làm bài gần đây</h2>
              <span className="text-xs text-slate-400 uppercase font-bold">{history.length} lần nộp</span>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="text-xs font-bold text-slate-400 uppercase border-b border-slate-100">
                    <th className="px-6 py-3">Bài tập</th>
                    <th className="px-6 py-3 text-center">Kết quả</th>
                    <th className="px-6 py-3 text-center">Thời gian</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-50">
                  {history.length === 0 ? (
                    <tr>
                      <td colSpan="3" className="px-6 py-10 text-center text-slate-400 italic text-sm">Bạn chưa nộp bài nào.</td>
                    </tr>
                  ) : (
                    history.map((s) => (
                      <tr key={s.id} className="hover:bg-slate-50/80 transition">
                        <td className="px-6 py-4">
                          <p className="font-semibold text-slate-700 text-sm">{s.problem?.title}</p>
                          <p className="text-[10px] text-slate-400 font-mono mt-0.5">{s.submittedAt}</p>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <span className={`px-2 py-1 rounded text-[10px] font-bold ${
                            s.status === 'ACCEPTED' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'
                          }`}>
                            {s.status}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-center text-xs text-slate-500 font-medium">
                          {(s.executionTimeMs / 1000).toFixed(3)}s
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

function StatItem({ label, value, color }) {
  return (
    <div className="flex justify-between items-end">
      <span className="text-sm text-slate-500">{label}</span>
      <span className={`text-2xl font-black ${color}`}>{value}</span>
    </div>
  )
}

function MiniStat({ label, value, color }) {
  return (
    <div className="text-center p-2 rounded-xl bg-slate-50 border border-slate-100">
      <div className={`text-sm font-bold ${color}`}>{value}</div>
      <div className="text-[10px] text-slate-400 uppercase font-bold">{label}</div>
    </div>
  )
}


