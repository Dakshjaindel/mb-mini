# MB Mini Platform Dashboard

Unified React dashboard connecting all three backend services:

| Service  | Port | Proxy prefix      |
|----------|------|-------------------|
| Catalog  | 8080 | `/api/catalog`    |
| Customer | 8081 | `/api/customer`   |
| Cart     | 8082 | `/api/cart`       |

## Prerequisites

- Node.js 18+
- MySQL and Redis running locally
- All three Spring Boot services started

## Start backends

In separate terminals:

```bash
# Catalog (port 8080)
cd catalog && ./mvnw spring-boot:run

# Customer (port 8081)
cd customer && ./mvnw spring-boot:run

# Cart (port 8082)
cd cart && ./mvnw spring-boot:run
```

## Start dashboard

```bash
cd customer/dashboard
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173).

## Features

- **Auth** — phone lookup, login, register, logout (customer service)
- **Catalog** — browse active products (catalog service)
- **Basket** — create basket, add/update items, wallet top-up (cart service)
- **Checkout** — finalize order (deducts wallet, updates stock)

## Flow

1. Enter phone → login or register
2. Browse catalog and add products to basket
3. Top up wallet credit if needed
4. Finalize order to complete checkout
