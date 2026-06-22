import type { ApiError, Catalog, CatalogCreate, CatalogUpdate } from '../types/catalog'

const BASE = '/data'

async function handleResponse<T>(res: Response): Promise<T> {
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

export async function fetchCatalogs(): Promise<Catalog[]> {
  const res = await fetch(BASE)
  return handleResponse<Catalog[]>(res)
}

export async function fetchCatalogById(id: number): Promise<Catalog> {
  const res = await fetch(`${BASE}/${id}`)
  return handleResponse<Catalog>(res)
}

export async function createCatalog(data: CatalogCreate): Promise<string> {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  return handleResponse<string>(res)
}

export async function updateCatalog(data: CatalogUpdate): Promise<string> {
  const res = await fetch(BASE, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  return handleResponse<string>(res)
}

export async function refreshCache(): Promise<string> {
  const res = await fetch(`${BASE}/cache/refresh`, { method: 'POST' })
  return handleResponse<string>(res)
}
