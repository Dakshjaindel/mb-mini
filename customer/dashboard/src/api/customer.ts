import {
  authHeaders,
  handleResponse,
  parseAuthTokens,
  parseCustomerId,
} from './http'
import type { CustomerProfile, RegisterPayload } from '../types/auth'

const BASE = '/api/customer'

export async function checkPhone(phone: string): Promise<string> {
  const res = await fetch(`${BASE}/customers/generate_login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ PhoneNo: phone }),
  })
  return handleResponse<string>(res)
}

export async function login(phone: string, password: string) {
  const res = await fetch(`${BASE}/customers/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ PhoneNo: phone, Password: password }),
  })
  const text = await handleResponse<string>(res)
  return parseAuthTokens(text)
}

export async function register(payload: RegisterPayload) {
  const res = await fetch(`${BASE}/customers/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
  const text = await handleResponse<string>(res)
  return {
    customerId: parseCustomerId(text),
    ...parseAuthTokens(text),
  }
}

export async function logout(authKey: string): Promise<string> {
  const res = await fetch(`${BASE}/customers/logout`, {
    method: 'POST',
    headers: authHeaders(authKey),
  })
  return handleResponse<string>(res)
}

export async function fetchProfile(authKey: string): Promise<CustomerProfile> {
  const res = await fetch(`${BASE}/customers/me`, {
    headers: authHeaders(authKey),
  })
  return handleResponse<CustomerProfile>(res)
}

export async function refreshSession(refreshToken: string): Promise<ReturnType<typeof parseAuthTokens>> {
  const res = await fetch(`${BASE}/customer/refresh?refreshToken=${encodeURIComponent(refreshToken)}`, {
    method: 'POST',
  })
  const text = await handleResponse<string>(res)
  const tokens = parseAuthTokens(text.replace(/authKey\s+/i, 'AuthKey: ').replace(/refreshToken\s+/i, 'RefreshToken: '))
  if (!tokens.authKey) {
    const alt = text.match(/authKey\s+(\S+)/i)
    const altRefresh = text.match(/refreshToken\s+(\S+)/i)
    return {
      authKey: alt?.[1],
      refreshToken: altRefresh?.[1],
    }
  }
  return tokens
}
