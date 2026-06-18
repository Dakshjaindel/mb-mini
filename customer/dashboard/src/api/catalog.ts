import { handleResponse } from './http'
import type { Catalog } from '../types/catalog'

const BASE = '/api/catalog'

export async function fetchCatalogs(): Promise<Catalog[]> {
  const res = await fetch(BASE)
  const catalogs = await handleResponse<Catalog[]>(res)
  return catalogs.map((catalog) => ({
    ...catalog,
    id: catalog.id ?? catalog.Id ?? 0,
  }))
}
