export interface LocalBasketItem {
  productId: number
  productName: string
  price: number
  quantity: number
}

export interface BasketState {
  basketId: number | null
  items: LocalBasketItem[]
  finalized: boolean
}
