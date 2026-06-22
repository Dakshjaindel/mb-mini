import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import * as customerApi from '../api/customer'
import type { AuthSession, RegisterPayload } from '../types/auth'

const STORAGE_KEY = 'mb-mini-session'

interface AuthContextValue {
  session: AuthSession | null
  loading: boolean
  checkPhone: (phone: string) => Promise<'login' | 'register'>
  login: (phone: string, password: string) => Promise<void>
  register: (payload: RegisterPayload) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

function loadStoredSession(): AuthSession | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as AuthSession) : null
  } catch {
    return null
  }
}

function saveSession(session: AuthSession | null) {
  if (session) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
  } else {
    localStorage.removeItem(STORAGE_KEY)
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => loadStoredSession())
  const [loading, setLoading] = useState(true)

  const hydrateProfile = useCallback(async (authKey: string, phone: string) => {
    const profile = await customerApi.fetchProfile(authKey)
    const next: AuthSession = {
      authKey,
      refreshToken: session?.refreshToken ?? '',
      customerId: profile.id,
      phone: profile.phoneNo || phone,
      name: profile.name,
      email: profile.email,
    }
    setSession(next)
    saveSession(next)
    return next
  }, [session?.refreshToken])

  useEffect(() => {
    async function restore() {
      const stored = loadStoredSession()
      if (!stored?.authKey) {
        setLoading(false)
        return
      }
      try {
        await hydrateProfile(stored.authKey, stored.phone)
      } catch {
        saveSession(null)
        setSession(null)
      } finally {
        setLoading(false)
      }
    }
    restore()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const checkPhone = useCallback(async (phone: string) => {
    try {
      await customerApi.checkPhone(phone)
      return 'login' as const
    } catch (err) {
      const message = err instanceof Error ? err.message.toLowerCase() : ''
      if (message.includes('not found')) {
        return 'register' as const
      }
      throw err
    }
  }, [])

  const login = useCallback(async (phone: string, password: string) => {
    const tokens = await customerApi.login(phone, password)
    if (!tokens.authKey) throw new Error('Login succeeded but no auth token was returned')

    const profile = await customerApi.fetchProfile(tokens.authKey)
    const next: AuthSession = {
      authKey: tokens.authKey,
      refreshToken: tokens.refreshToken ?? '',
      customerId: profile.id,
      phone: profile.phoneNo || phone,
      name: profile.name,
      email: profile.email,
    }
    setSession(next)
    saveSession(next)
  }, [])

  const register = useCallback(async (payload: RegisterPayload) => {
    const result = await customerApi.register(payload)
    if (result.authKey) {
      const profile = await customerApi.fetchProfile(result.authKey)
      const next: AuthSession = {
        authKey: result.authKey,
        refreshToken: result.refreshToken ?? '',
        customerId: profile.id,
        phone: profile.phoneNo || payload.PhoneNo,
        name: profile.name,
        email: profile.email,
      }
      setSession(next)
      saveSession(next)
      return
    }

    await login(payload.PhoneNo, payload.Password)
  }, [login])

  const logout = useCallback(async () => {
    if (session?.authKey) {
      try {
        await customerApi.logout(session.authKey)
      } catch {
        // clear local session even if server logout fails
      }
    }
    setSession(null)
    saveSession(null)
  }, [session?.authKey])

  const value = useMemo(
    () => ({ session, loading, checkPhone, login, register, logout }),
    [session, loading, checkPhone, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
