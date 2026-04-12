import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

function TopNavLink({ to, children }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `hover:opacity-80 ${isActive ? 'opacity-100' : 'opacity-90'}`
      }
    >
      {children}
    </NavLink>
  )
}

export function AppLayout() {
  const navigate = useNavigate()
  const { auth, clearAuth } = useAuth()

  return (
    <div className="min-h-screen bg-white">
      <header className="bg-brand-red text-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
          <button type="button" onClick={() => navigate('/')} className="text-left">
            <div className="text-2xl font-extrabold leading-6">PTIT Exam</div>
            <div className="text-xs font-semibold uppercase tracking-wide opacity-90">
              Nền tảng thi trắc nghiệm
            </div>
          </button>

          <nav className="hidden items-center gap-8 text-sm font-semibold md:flex">
            <TopNavLink to="/">Trang chủ</TopNavLink>
            <TopNavLink to="/exams">Đề thi</TopNavLink>
            {auth ? <TopNavLink to="/profile">Tài khoản</TopNavLink> : null}
          </nav>

          <div className="flex items-center gap-3">
            {auth ? (
              <>
                <span className="hidden text-sm font-medium md:inline">
                  Xin chào, {auth.username}
                </span>
                <button
                  type="button"
                  onClick={() => {
                    clearAuth()
                    navigate('/')
                  }}
                  className="rounded-md border border-white/70 px-4 py-1.5 text-sm font-semibold hover:bg-white/10"
                >
                  Đăng xuất
                </button>
              </>
            ) : (
              <>
                <NavLink
                  to="/login"
                  className="rounded-md border border-white/70 px-4 py-1.5 text-sm font-semibold hover:bg-white/10"
                >
                  Đăng nhập
                </NavLink>
                <NavLink
                  to="/register"
                  className="rounded-md bg-white px-4 py-1.5 text-sm font-semibold text-brand-red hover:bg-red-50"
                >
                  Đăng ký
                </NavLink>
              </>
            )}
          </div>
        </div>
      </header>

      <Outlet />

      <footer className="border-t border-slate-200 bg-slate-50">
        <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-2 px-4 py-6 text-sm text-slate-600 md:flex-row">
          <span>© 2026 PTIT Exam Platform</span>
          <span>React + TailwindCSS</span>
        </div>
      </footer>
    </div>
  )
}

