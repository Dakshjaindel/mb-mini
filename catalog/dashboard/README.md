# Catalog Dashboard

React dashboard for the mb-mini catalog API.

## Prerequisites

- Java 17+ with the Spring Boot API running on `http://localhost:8080`
- MySQL and Redis configured per the main project

## Run

```bash
# From project root — start the API
./mvnw spring-boot:run

# In another terminal — start the dashboard
cd dashboard
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173).

The Vite dev server proxies `/data` requests to the API.

## Features

- View all catalog products with stats
- Create and edit products
- Refresh Redis cache from MySQL
- Real-time error handling from API validation
