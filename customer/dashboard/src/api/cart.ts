import { handleResponse, parseBasketId } from './http'

const BASE = '/api/cart'

export async function createBasket(userId: number): Promise<number> {
  const res = await fetch(`${BASE}/baskets`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      UserId: userId,
      Date: new Date().toISOString(),
      flag: 1,
    }),
  })
  const text = await handleResponse<string>(res)
  const basketId = parseBasketId(text)
  if (!basketId) throw new Error('Could not parse basket ID from response')
  return basketId
}

export async function addWalletCredit(
  customerId: number,
  amount: number,
  type = 'topup',
): Promise<string> {
  const res = await fetch(`${BASE}/credits`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      customerId,
      creditAmount: amount,
      type,
      flag: 1,
    }),
  })
  return handleResponse<string>(res)
}

export async function addItemToBasket(
  basketId: number,
  productId: number,
  quantity: number,
): Promise<string> {
  const res = await fetch(`${BASE}/baskets/itemAdd`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ basketId, productId, quantity }),
  })
  return handleResponse<string>(res)
}

export async function finalizeOrder(basketId: number): Promise<string> {
  const res = await fetch(`${BASE}/baskets/finalize`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ basketId }),
  })
  return handleResponse<string>(res)
}
