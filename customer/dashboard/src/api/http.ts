export interface ApiError {
  error?: string
  errors?: string[]
}

export async function handleResponse<T>(res: Response): Promise<T> {
  const text = await res.text()

  if (!res.ok) {
    let message = text
    try {
      const parsed = JSON.parse(text) as ApiError
      message = parsed.error ?? parsed.errors?.join(', ') ?? text
    } catch {
      // plain text error
    }
    throw new Error(message || `Request failed (${res.status})`)
  }

  if (!text) return undefined as T

  try {
    return JSON.parse(text) as T
  } catch {
    return text as T
  }
}

export function authHeaders(authKey: string): HeadersInit {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${authKey}`,
  }
}

export function parseAuthTokens(text: string): { authKey?: string; refreshToken?: string } {
  const authMatch = text.match(/AuthKey:?\s*(\S+)/i)
  const refreshMatch = text.match(/RefreshToken:?\s*(\S+)/i)
  return {
    authKey: authMatch?.[1],
    refreshToken: refreshMatch?.[1],
  }
}

export function parseCustomerId(text: string): number | null {
  const match = text.match(/Id\s*(\d+)/i)
  return match ? Number(match[1]) : null
}

export function parseBasketId(text: string): number | null {
  const match = text.match(/basket\s*ID\s*(\d+)/i)
  return match ? Number(match[1]) : null
}
