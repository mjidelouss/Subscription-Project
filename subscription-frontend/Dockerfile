# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies (cached unless package files change).
COPY package*.json ./
RUN npm ci

# Build the production bundle.
COPY . .
RUN npm run build

# ---------- Runtime stage ----------
FROM nginx:1.27-alpine AS runtime

# SPA + reverse-proxy configuration.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Angular's application builder outputs to dist/<project>/browser.
COPY --from=build /app/dist/subscription-frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
