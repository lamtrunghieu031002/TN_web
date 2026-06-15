import { useEffect, useState } from 'react'
import api from '../../services/adminService' // dung axios instance san co

function StatCard({ label, value, color }) {
  return (
    <div className="rounded-2xl bg-white border border-slate-200 p-5 shadow-sm">
      <p className="text-xs font-bold uppercase text-slate-400">{label}</p>
      <p className={`mt-2 text-3xl font-extrabold ${color}`}>{value ?? '...'}</p>
    </div>
  )
}

export function AdminOverview() {
  const [data, setData] = useState(null)

  useEffect(() => {
    api.get('/api/admin/overview').then(r => setData(r.data))
  }, [])

  const acRate = data
    ? Math.round((data.totalAccepted / Math.max(1, data.totalSubmissions)) * 1000) / 10
    : 0

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-800">Tổng quan</h1>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Users"       value={data?.totalUsers}        color="text-slate-800" />
        <StatCard label="Bài tập"     value={data?.totalProblems}     color="text-blue-600" />
        <StatCard label="Submissions" value={data?.totalSubmissions}  color="text-purple-600" />
        <StatCard label="Tỉ lệ AC"    value={`${acRate}%`}            color="text-green-600" />
      </div>

      <div className="rounded-2xl bg-white border border-slate-200 p-6 shadow-sm">
        <h2 className="font-bold text-slate-700 mb-4">Submissions 7 ngày gần nhất</h2>
        <div className="h-48 flex items-end gap-2">
          {(data?.submissionsByDay || []).map(([day, count]) => (
            <div key={day} className="flex-1 flex flex-col items-center gap-1">
              <div className="w-full bg-red-500 rounded-t"
                   style={{ height: `${Math.min(100, count * 5)}%` }} />
              <span className="text-[10px] text-slate-400">{day.slice(5)}</span>
              <span className="text-xs font-bold">{count}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}