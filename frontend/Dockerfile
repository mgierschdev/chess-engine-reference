# Build stage
FROM node:18-alpine AS build
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies (dev + prod) for build
RUN npm ci

# Copy source code
COPY . .

# Build the Next.js application
RUN npm run build

# Ensure font-manifest.json exists to avoid runtime MODULE_NOT_FOUND
RUN mkdir -p .next/server \
  && if [ ! -f .next/server/font-manifest.json ]; then echo '{}' > .next/server/font-manifest.json; fi

# Remove dev dependencies to keep node_modules production-only
RUN npm prune --production

# Runtime stage
FROM node:18-alpine
WORKDIR /app

# Copy package files
COPY package*.json ./

# Copy built application and production node_modules from build stage
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/node_modules ./node_modules

# Expose port
EXPOSE 3000

# Set environment to production
# ENV NODE_ENV=production

# Start the application
CMD ["npm", "start"]
