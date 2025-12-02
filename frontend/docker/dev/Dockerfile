FROM node:24-alpine
WORKDIR /app

# Set up corepack with pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# Set default permission
ARG DEFAULT_PERMISSION=755

# Create a non-root user to run the application
ARG USER_NAME=docker
ARG USER_ID=1001
ARG GROUP_NAME=docker
ARG GROUP_ID=1001
RUN addgroup -g ${GROUP_ID} ${GROUP_NAME} && \
    adduser -D -u ${USER_ID} -G ${GROUP_NAME} -s /bin/sh ${USER_NAME} && \
    chown -R ${USER_NAME}:${GROUP_NAME} /app && \
    chmod -R ${DEFAULT_PERMISSION} /app

# Copy package files
COPY --chown=${USER_NAME}:${GROUP_NAME} --chmod=${DEFAULT_PERMISSION} package.json pnpm-lock.yaml pnpm-workspace.yaml ./

# Change to the non-root user
USER ${USER_NAME}

# Install dependencies as non-root user
RUN pnpm install --frozen-lockfile

# Expose port
EXPOSE 5173

# Start the development server
CMD ["pnpm", "run", "dev", "--host", "0.0.0.0"]