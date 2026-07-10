// ---------------------------------------------------------------------------
// API client for the mb-mini services (admin app).
//
//   catalog  -> http://localhost:8080  (catalog CRUD, quantity, cache refresh)
//   customer -> http://localhost:8081  (geofence)
//   cart     -> http://localhost:8082  (optimal delivery routing)
//
// Admin endpoints are NOT behind the /consumer/** auth interceptor, so no token
// is required. Base URLs are overridable via the Settings screen (localStorage).
// ---------------------------------------------------------------------------

const STORAGE = {
  catalog: 'mbmini.admin.url.catalog',
  customer: 'mbmini.admin.url.customer',
  cart: 'mbmini.admin.url.cart',
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

async function request(service, path, { method = 'GET', body } = {}) {
  const base = getBaseUrls()[service];
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';

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
    try { data = JSON.parse(raw); } catch { /* keep raw */ }
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

function cleanCatalogFilters(filters = {}) {
  const pageSize = Math.max(1, Number(filters.pageSize) || 100);
  const pageNo = Math.max(1, Number(filters.pageNo) || 1);
  return {
    pageSize,
    pageNo,
    similar: filters.similar || null,
    productNameFilter: filters.productNameFilter || null,
    quantityFilter: filters.quantityFilter || null,
  };
}

async function listAllCatalog(filters = {}) {
  const firstPage = Math.max(1, Number(filters.pageNo) || 1);
  const pageSize = Math.max(1, Number(filters.pageSize) || 100);
  const all = [];

  for (let pageNo = firstPage; pageNo < firstPage + 1000; pageNo += 1) {
    const page = await request('catalog', '/catalog/all', {
      method: 'POST',
      body: cleanCatalogFilters({ ...filters, pageSize, pageNo }),
    });
    if (!Array.isArray(page) || page.length === 0) break;
    all.push(...page);
    if (page.length < pageSize) break;
  }

  return all;
}

export const catalogApi = {
  create: (payload) => request('catalog', '/catalog', { method: 'POST', body: payload }),
  update: (payload) => request('catalog', '/catalog', { method: 'PUT', body: payload }),
  list: (filters) => request('catalog', '/catalog/all', { method: 'POST', body: cleanCatalogFilters(filters) }),
  listAll: listAllCatalog,
  get: (id) => request('catalog', '/catalog/' + encodeURIComponent(id)),
  updateQuantity: (productId, quantity) =>
    request('catalog', '/catalog/quantity', { method: 'PUT', body: { productId, quantity } }),
  // Note: the backend mapping literally contains a space -> "/consumer /catalog/cache/refresh".
  cacheRefresh: () => request('catalog', '/consumer%20/catalog/cache/refresh', { method: 'POST' }),
};

export const routingApi = {
  // GET /optimalPath takes a { date } body; sent via the POST alias for browsers.
  optimalPath: (date) => request('cart', '/optimalPath', { method: 'POST', body: { date } }),
  optimalPath2: (date) => request('cart', '/optimalPath2', { method: 'POST', body: { date } }),
};

export const geofenceApi = {
  // points: array of [lat, lng] pairs.
  setFence: (points) => request('customer', '/setFence', { method: 'POST', body: { points } }),
};
