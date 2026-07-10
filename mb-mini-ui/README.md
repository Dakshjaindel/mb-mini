# mb-mini-ui

Two React + Vite web apps for the `mb-mini` services:

| App | Folder | Dev port | Who | Uses |
| --- | --- | --- | --- | --- |
| **Consumer** | [`consumer-app/`](consumer-app) | `5173` | Customers | Register / login, browse catalog, manage basket & credits, profile |
| **Admin** | [`admin-app/`](admin-app) | `5174` | Operators | Catalog CRUD, delivery-route optimisation, geofencing |

## Services the apps talk to

| Service | Default URL | Used by |
| --- | --- | --- |
| catalog | `http://localhost:8080` | both |
| customer | `http://localhost:8081` | consumer (auth/profile), admin (geofence) |
| cart | `http://localhost:8082` | consumer (basket), admin (routing) |

Each app has a **Settings** tab to override these URLs at runtime (stored in `localStorage`).

## Prerequisites

- **Node ≥ 18** (built and tested on Node 26 / npm 11). If `node -v` reports something older,
  this machine also has a newer Node under Homebrew — e.g. run with
  `PATH="$(brew --prefix node)/bin:$PATH" npm run dev`.
- The three backend services running (catalog `8080`, customer `8081`, cart `8082`), plus their
  MySQL / Redis / Mongo / Kafka dependencies.

## Run

```bash
# Consumer app
cd consumer-app && npm install && npm run dev      # http://localhost:5173

# Admin app (separate terminal)
cd admin-app && npm install && npm run dev         # http://localhost:5174
```

Production build: `npm run build` (output in `dist/`).

## Backend changes made for these apps

These UIs run in the browser from a different origin, which surfaced two backend issues that were
fixed with small, non-breaking changes:

1. **CORS** — added a `CorsConfigurationSource` + `http.cors(...)` to the shared
   `framework` `SecurityConfig`. Because catalog, customer and cart all component-scan
   `com.example.mbminiframework.Configs`, this enables CORS across all three services.

2. **GET-with-body endpoints** — browsers cannot attach a request body to a `GET`. Three
   endpoints (`/catalog/all`, `/optimalPath`, `/optimalPath2`) were `@GetMapping` **with**
   `@RequestBody`. They were changed to `@RequestMapping(method = {GET, POST})` so the original
   `GET` contract is preserved **and** the apps can call them via `POST`.

## Endpoint map

**Consumer app**
- Auth (customer): `POST /customers/generate_login`, `POST /customers/login`,
  `POST /customers/register`, `POST /customer/refresh`, `POST /consumer/customers/logout`
- Profile (customer): `GET /customers/me`, `PUT /customers`, `PUT /customers/password`, `PUT /customer/address`
- Catalog: `GET/POST /catalog/all`, `GET /catalog/{id}`
- Basket (cart): `POST /consumer/baskets`, `POST /consumer/baskets/itemAdd`,
  `POST /consumer/baskets/finalize`, `POST /consumer/credits`

**Admin app**
- Catalog: `POST /catalog`, `PUT /catalog`, `GET/POST /catalog/all`, `GET /catalog/{id}`,
  `PUT /catalog/quantity`, `POST "/consumer /catalog/cache/refresh"`
- Routing (cart): `GET/POST /optimalPath`, `GET/POST /optimalPath2` → list of `[lat, lng]` waypoints (plotted on a map)
- Geofence (customer): `POST /setFence` → polygon of `[lat, lng]` points

## Notes / quirks handled in the UI

- **Auth tokens are returned as plain sentences** (e.g. `... AuthKey: abc | RefreshToken: def`).
  The consumer app parses the `AuthKey`/`RefreshToken` out of the response text and stores them;
  `/consumer/**` calls send the `AuthKey` header, and `/customers/me` + logout also send
  `Authorization: Bearer <AuthKey>`.
- **Admin endpoints are not authenticated** — only `/consumer/**` is behind the auth interceptor,
  so the admin app has no login.
- The catalog **cache-refresh path literally contains a space** in the backend mapping
  (`"/consumer /catalog/cache/refresh"`); the admin app calls it URL-encoded.
- The routing endpoints return coordinate pairs whose lat/lng order isn't guaranteed; the routing
  screen has a **"swap lat/lng"** toggle in case the map looks mirrored.
