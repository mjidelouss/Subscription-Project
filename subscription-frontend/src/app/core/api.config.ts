/**
 * Base path of the Spring Boot API.
 *
 * It is intentionally relative ("/api"):
 *  - In production (Docker), nginx serves the SPA and proxies "/api" to the backend.
 *  - In development, the Angular dev-server proxy (proxy.conf.json) forwards
 *    "/api" to http://localhost:8080.
 * This keeps the same-origin contract and avoids any CORS configuration.
 */
export const API_BASE_URL = '/api';
