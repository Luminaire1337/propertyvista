FROM node:24-alpine
WORKDIR /app

# Set up corepack with pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy package files as root first
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./

# Change ownership and switch to node user
RUN chown -R node:node /app
USER node

# Install dependencies with a cache mount
RUN --mount=type=cache,target=/home/node/.local/share/pnpm/store,uid=1000,gid=1000 \
    pnpm install --frozen-lockfile

# Expose port
EXPOSE 5173

# Start the development server
CMD ["pnpm", "run", "dev", "--host", "0.0.0.0"]