# Contributing to ChessEngine

Thank you for your interest in contributing to ChessEngine! This document provides guidelines for contributing to the project.

## Getting Started

### Prerequisites

- **Java 17 or higher** - For backend development
- **Node.js 18+** - For frontend development
- **Git** - For version control
- **Docker** (optional) - For containerized development

### Development Setup

1. **Fork and clone the repository**
   ```bash
   git clone https://github.com/<your-username>/ChessEngine.git
   cd ChessEngine
   ```

2. **Backend setup**
   ```bash
   cd backend
   ./gradlew build
   ./gradlew bootRun
   ```
   Backend will run on `http://localhost:8080`

3. **Frontend setup**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Frontend will run on `http://localhost:3000`

### Using Make Commands

The project includes a Makefile for common tasks:

```bash
make dev         # Run backend + frontend locally
make test        # Run all tests
make docker-up   # Start with Docker Compose
make docker-down # Stop Docker services
```

## Development Workflow

### 1. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
```

Use descriptive branch names:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `test/` - Test additions or improvements
- `refactor/` - Code refactoring

### 2. Make Your Changes

- Follow existing code style and conventions
- Write clear, descriptive commit messages
- Keep commits focused and atomic
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

**Backend:**
```bash
cd backend
./gradlew test
```

**Frontend:**
```bash
cd frontend
npm run lint
npm test
npm run build
```

**All tests:**
```bash
make test
```

### 4. Commit Your Changes

Follow conventional commit format:
```
<type>(<scope>): <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Examples:
```
feat(backend): add stalemate detection
fix(frontend): correct piece highlighting bug
docs(readme): update Docker instructions
test(chess): add castling validation tests
```

### 5. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub with:
- Clear title describing the change
- Description of what changed and why
- Steps to test the changes
- Screenshots (for UI changes)
- Link to related issues

## Code Style Guidelines

### Backend (Java)

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Document complex logic with comments
- Keep methods focused and under 50 lines when possible
- Write unit tests for new functionality

### Frontend (TypeScript/React)

- Use TypeScript for type safety
- Follow Next.js and React best practices
- Use functional components and hooks
- Keep components focused and reusable
- Use meaningful prop and state names
- Follow ESLint rules: `npm run lint`

## Testing Guidelines

### Backend Tests

- Place tests in `backend/src/test/java/`
- Use JUnit 5 for test framework
- Name tests descriptively: `testFeatureUnderSpecificCondition`
- Test edge cases and error conditions
- Aim for meaningful test coverage

### Frontend Tests

- Place tests next to components: `*.test.tsx`
- Use Jest and React Testing Library
- Test user interactions and component behavior
- Mock external dependencies (API calls)
- Focus on behavior, not implementation

## What We're Looking For

### Good Contributions

✅ **Bug fixes** - Especially with test cases
✅ **Test additions** - Improve code coverage
✅ **Documentation improvements** - Clearer explanations
✅ **Performance improvements** - With benchmarks
✅ **Chess rule correctness** - Edge case handling
✅ **UI/UX enhancements** - Better user experience

### Contributions We Won't Accept

❌ **Database additions** - Keep it in-memory by design
❌ **Authentication systems** - Out of scope
❌ **AI opponents** - Future feature, not ready yet
❌ **Online multiplayer** - Out of scope
❌ **Major architectural changes** - Discuss first

## Known Limitations (As Designed)

These are **intentional design choices**, not bugs:

- **In-memory game state** - No persistence between restarts
- **Single game instance** - One game per server
- **No draw detection** - Stalemate not implemented
- **No move history export** - No PGN/FEN support
- **Local only** - No production deployment

If you want to address these, please open an issue first to discuss.

## Pull Request Process

1. **Ensure CI passes** - All tests must pass
2. **Update documentation** - If behavior changes
3. **Add tests** - For new features
4. **Keep PRs focused** - One feature/fix per PR
5. **Respond to feedback** - Be open to suggestions
6. **Be patient** - Reviews may take a few days

## Getting Help

- **Issues** - Check existing issues or open a new one
- **Discussions** - For questions and ideas
- **Documentation** - Read README.md thoroughly

## Code of Conduct

- Be respectful and professional
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Keep discussions on-topic

## Recognition

Contributors will be:
- Acknowledged in release notes
- Listed in GitHub contributors
- Thanked in the community

Thank you for contributing! 🎉
