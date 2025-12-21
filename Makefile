.PHONY: help dev test docker-up docker-down docker-build clean backend-dev frontend-dev backend-test frontend-test

# Default target
help:
	@echo "Chess Engine - Development Commands"
	@echo ""
	@echo "Available targets:"
	@echo "  make dev         - Run backend and frontend locally (in parallel)"
	@echo "  make test        - Run all tests (backend + frontend)"
	@echo "  make docker-up   - Start the application using Docker Compose"
	@echo "  make docker-down - Stop Docker Compose services"
	@echo "  make docker-build - Build Docker images"
	@echo "  make clean       - Clean build artifacts"
	@echo ""
	@echo "Individual targets:"
	@echo "  make backend-dev  - Run backend only"
	@echo "  make frontend-dev - Run frontend only"
	@echo "  make backend-test - Run backend tests only"
	@echo "  make frontend-test - Run frontend tests only"

# Run both backend and frontend locally
dev:
	@echo "Starting backend and frontend..."
	@echo "Backend will run on http://localhost:8080"
	@echo "Frontend will run on http://localhost:3000"
	@echo ""
	@echo "Press Ctrl+C to stop both services"
	@cd backend && ./gradlew bootRun & \
	cd frontend && npm run dev & \
	wait

# Run all tests
test: backend-test frontend-test

# Backend tests
backend-test:
	@echo "Running backend tests..."
	cd backend && ./gradlew test --no-daemon

# Frontend tests
frontend-test:
	@echo "Running frontend tests..."
	cd frontend && npm test -- --passWithNoTests

# Run backend only
backend-dev:
	@echo "Starting backend on http://localhost:8080..."
	cd backend && ./gradlew bootRun

# Run frontend only
frontend-dev:
	@echo "Starting frontend on http://localhost:3000..."
	cd frontend && npm run dev

# Docker commands
docker-build:
	@echo "Building Docker images..."
	docker-compose build

docker-up:
	@echo "Starting Docker Compose services..."
	@echo "Backend will be available at http://localhost:8080"
	@echo "Frontend will be available at http://localhost:3000"
	docker-compose up

docker-down:
	@echo "Stopping Docker Compose services..."
	docker-compose down

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	cd backend && ./gradlew clean
	cd frontend && rm -rf .next node_modules
	@echo "Clean complete"
