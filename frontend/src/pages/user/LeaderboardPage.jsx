import { useEffect, useState } from 'react'
import { getLeaderboard } from "../../services/problemService"




export function LeaderboardPage() {
    const [leaderboard, setLeaderboard] = useState([])
    const [loading, setLoading] = useState(true)
    

    useEffect(()=>{
        getLeaderboard()
        .then(res=>{
            const leaderboardData = res?.data || res;
            setLeaderboard(leaderboardData);
        })
        .finally(()=>setLoading(false))
    },[])
    const rankIcon = (rank) => {
    if (rank === 1) return '🥇'
    if (rank === 2) return '🥈'
    if (rank === 3) return '🥉'
    return rank
  }
        
  return (
    <main className="mx-auto max-w-5xl px-4 py-8">
      {/* Header */}
      <div className="mb-8 text-center">
        <h1 className="text-3xl font-bold text-slate-800">🏆 Bảng xếp hạng</h1>
        <p className="text-slate-500 mt-2">Top người dùng giải nhiều bài nhất</p>
      </div>

      {loading ? (
        <div className="text-center text-slate-400 py-16">Đang tải...</div>
      ) : (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          {/* Table header */}
          <div className="grid grid-cols-12 text-xs font-bold uppercase text-slate-400 px-6 py-3 bg-slate-50 border-b border-slate-200">
            <span className="col-span-1">#</span>
            <span className="col-span-3">Username</span>
            <span className="col-span-2 text-center">Đã giải</span>
            <span className="col-span-2 text-center text-green-600">Easy</span>
            <span className="col-span-2 text-center text-yellow-600">Medium</span>
            <span className="col-span-1 text-center text-red-600">Hard</span>
            <span className="col-span-1 text-center">AC%</span>
          </div>

          {/* Table rows */}
          {leaderboard.length === 0 ? (
            <div className="text-center text-slate-400 py-16">Chưa có dữ liệu</div>
          ) : (
            <div className="divide-y divide-slate-100">
              {leaderboard.map((user) => (
                <div
                  key={user.userId ?? user.username}
                  className={`grid grid-cols-12 items-center px-6 py-4 hover:bg-slate-50 transition ${
                    user.rank <= 3 ? 'bg-yellow-50/30' : ''
                  }`}
                >
                  {/* Rank */}
                  <span className="col-span-1 text-lg font-bold text-slate-600">
                    {rankIcon(user.rank)}
                  </span>

                  {/* Username */}
                  <span className="col-span-3 font-semibold text-slate-800">
                    {user.username}
                  </span>

                  {/* Total solved */}
                  <span className="col-span-2 text-center font-bold text-slate-700">
                    {user.totalSolved}
                  </span>

                  {/* Easy */}
                  <span className="col-span-2 text-center text-green-600 font-medium">
                    {user.totalEasy}
                  </span>

                  {/* Medium */}
                  <span className="col-span-2 text-center text-yellow-600 font-medium">
                    {user.totalMedium}
                  </span>

                  {/* Hard */}
                  <span className="col-span-1 text-center text-red-600 font-medium">
                    {user.totalHard}
                  </span>

                  {/* Acceptance rate */}
                  <span className="col-span-1 text-center text-slate-500 text-sm">
                    {user.acceptanceRate}%
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </main>
  )
}