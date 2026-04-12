import { useMemo, useState } from 'react'
import { AUTH_STORAGE_KEY } from '../constants/storage'
import { AuthContext } from './AuthContext'

function readStoredAuth() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => readStoredAuth())

  const value = useMemo(() => {
    const saveAuth = (data) => {
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(data))
      setAuth(data)
    }

    const clearAuth = () => {
      localStorage.removeItem(AUTH_STORAGE_KEY)
      setAuth(null)
    }

    return { auth, saveAuth, clearAuth }
  }, [auth])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
