export interface Catalog {
  id: number
  Id?: number
  productName: string
  quantity: number
  price: number
  isActive: boolean
  created_on?: string | null
  created_by?: string | null
  modified_on?: string | null
  modified_by?: string | null
}

export interface CatalogCreate {
  productName: string
  quantity: number
  price: number
  isActive: boolean
}

export interface CatalogUpdate {
  id: number
  productName?: string
  quantity?: number
  price?: number
  isActive?: boolean
}

export interface ApiError {
  error?: string
  errors?: string[]
}
