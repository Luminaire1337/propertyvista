# Build stage
FROM node:22-alpine AS build
WORKDIR /app

# Set up corepack with pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy package files and install dependencies
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

# Copy the rest of the application code and build the project
COPY . .
RUN pnpm run build

# Runtime stage
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Copy built files from the build stage
RUN rm -rf ./*
COPY --from=build /app/dist .

# Copy nginx config
COPY docker/default.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Add a health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/health || exit 1

# Start nginx server
CMD ["nginx", "-g", "daemon off;"]