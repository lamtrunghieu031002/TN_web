import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'

export function AdminRoute() {
  const { auth } = useAuth()
  if (!auth) return <Navigate to="/login" replace />
  if (!auth.roles?.includes('ROLE_ADMIN')) return <Navigate to="/" replace />
  return <Outlet />
}
