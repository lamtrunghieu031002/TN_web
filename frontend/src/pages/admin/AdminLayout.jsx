import { NavLink, Outlet } from 'react-router-dom'

const NAV = [
  { to: '/admin',             label: 'Tổng quan', icon: '📊', end: true },
  { to: '/admin/users',       label: 'Users',      icon: '👥' },
  { to: '/admin/problems',    label: 'Bài tập',    icon: '📚' },
  { to: '/admin/submissions', label: 'Submissions',icon: '📝' },
]

export function AdminLayout() {
  return (
    <div className="flex min-h-[calc(100vh-64px)]">
      <aside className="w-56 border-r border-slate-200 bg-white">
        <div className="p-4 text-xs font-bold uppercase text-slate-400">Admin Panel</div>
        <nav className="space-y-1 px-2">
          {NAV.map(n => (
            <NavLink
              key={n.to} to={n.to} end={n.end}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition ${
                  isActive
                    ? 'bg-red-50 text-red-700'
                    : 'text-slate-600 hover:bg-slate-50'
                }`}
            >
              <span>{n.icon}</span>{n.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <main className="flex-1 bg-slate-50 p-8 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}