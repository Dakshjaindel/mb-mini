// ---------------------------------------------------------------------------
// API client for the mb-mini services (consumer app).
//
// Services & default ports:
//   catalog  -> http://localhost:8080
//   customer -> http://localhost:8081
//   cart     -> http://localhost:8082
//
// Base URLs are overridable at runtime via the Settings screen (localStorage).
// ---------------------------------------------------------------------------

const STORAGE = {
  catalog: 'mbmini.url.catalog',
  customer: 'mbmini.url.customer',
  cart: 'mbmini.url.cart',
};

export const DEFAULT_URLS = {
  catalog: 'http://localhost:8080',
  customer: 'http://localhost:8081',
  cart: 'http://localhost:8082',
};

export function getBaseUrls() {
  return {
    catalog: localStorage.getItem(STORAGE.catalog) || DEFAULT_URLS.catalog,
    customer: localStorage.getItem(STORAGE.customer) || DEFAULT_URLS.customer,
    cart: localStorage.getItem(STORAGE.cart) || DEFAULT_URLS.cart,
  };
}

export function setBaseUrl(service, url) {
  localStorage.setItem(STORAGE[service], String(url).trim().replace(/\/+$/, ''));
}

export function resetBaseUrls() {
  Object.values(STORAGE).forEach((k) => localStorage.removeItem(k));
}

export class ApiError extends Error {
  constructor(status, message, body) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

async function request(service, path, { method = 'GET', body, auth } = {}) {
  const base = getBaseUrls()[service];
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (auth?.authKey) headers['AuthKey'] = auth.authKey;
  if (auth?.bearer) headers['Authorization'] = 'Bearer ' + auth.bearer;

  let res;
  try {
    res = await fetch(base + path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch (e) {
    throw new ApiError(
      0,
      `Network error calling the ${service} service at ${base}. Make sure it is running and CORS is enabled.`,
      null
    );
  }

  const raw = await res.text();
  let data = raw;
  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) {
    try { data = JSON.parse(raw); } catch { /* keep raw text */ }
  }

  if (!res.ok) {
    const msg =
      typeof data === 'string'
        ? data
        : data && (data.message || data.error)
        ? data.message || data.error
        : raw;
    throw new ApiError(res.status, msg || `HTTP ${res.status}`, data);
  }
  return data;
}

// The login/register/refresh endpoints return a plain sentence containing the
// tokens, e.g. "... AuthKey: abc | RefreshToken: def" or
// "... authKey abc refreshToken def". Pull the tokens out of whatever we get.
export function parseSession(text) {
  const s = String(text ?? '');
  const authKey = (s.match(/auth\s*key[:\s]+([^\s|,]+)/i) || [])[1] || null;
  const refreshToken = (s.match(/refresh\s*token[:\s]+([^\s|,]+)/i) || [])[1] || null;
  return { authKey, refreshToken };
}

export const customerApi = {
  generateLogin: (phoneNo) =>
    request('customer', '/customers/generate_login', { method: 'POST', body: { phoneNo } }),
  login: (phoneNo, password) =>
    request('customer', '/customers/login', { method: 'POST', body: { phoneNo, password } }),
  register: (payload) =>
    request('customer', '/customers/register', { method: 'POST', body: payload }),
  me: (authKey) =>
    request('customer', '/customers/me', { auth: { bearer: authKey } }),
  logout: (authKey) =>
    request('customer', '/consumer/customers/logout', {
      method: 'POST',
      auth: { authKey, bearer: authKey },
    }),
  refresh: (refreshToken) =>
    request('customer', '/customer/refresh?refreshToken=' + encodeURIComponent(refreshToken), {
      method: 'POST',
    }),
  updateProfile: (payload) =>
    request('customer', '/customers', { method: 'PUT', body: payload }),
  updatePassword: (id, newPass) =>
    request('customer', '/customers/password', { method: 'PUT', body: { id, newPass } }),
  updateAddress: (payload) =>
    request('customer', '/customer/address', { method: 'PUT', body: payload }),
};

export const catalogApi = {
  // GET /catalog/all takes a filter body; sent via the POST alias so browsers can attach it.
  list: (filters) => request('catalog', '/catalog/all', { method: 'POST', body: filters }),
  get: (id) => request('catalog', '/catalog/' + encodeURIComponent(id)),
};

export const cartApi = {
  newBasket: (userId, dateMillis, flag, authKey) =>
    request('cart', '/consumer/baskets', {
      method: 'POST',
      body: { userId, date: dateMillis, flag },
      auth: { authKey },
    }),
  addItem: (productId, quantity, authKey) =>
    request('cart', '/consumer/baskets/itemAdd', {
      method: 'POST',
      body: { productId, quantity },
      auth: { authKey },
    }),
  finalize: (basketId, authKey) =>
    request('cart', '/consumer/baskets/finalize', {
      method: 'POST',
      body: { basketId },
      auth: { authKey },
    }),
  addCredit: (creditAmount, type, flag, authKey) =>
    request('cart', '/consumer/credits', {
      method: 'POST',
      body: { creditAmount, type, flag },
      auth: { authKey },
    }),
};
