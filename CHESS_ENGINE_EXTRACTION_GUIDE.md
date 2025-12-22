# Chess Engine Core - Extraction Guide

## Overview

The chess engine core consists of standalone, reusable components that implement complete chess game logic. These components can be extracted into a separate library for use in other projects.

## Core Components

### Domain Classes

Located in `backend/src/main/java/com/backend/domain/`:

- **`Chessboard.java`** - Core chess board logic
  - Move validation for all piece types
  - Special moves (castling, en passant, promotion)
  - Check and checkmate detection
  - Stalemate and draw detection
  - No dependencies on Spring or web frameworks

- **`ChessGame.java`** - High-level game state management
  - Turn management
  - Captured pieces tracking
  - Move history
  - Game state (check, checkmate, draw)
  - Undo/redo functionality
  - FEN import/export
  - No dependencies on Spring or web frameworks

### Model Classes

Located in `backend/src/main/java/com/backend/models/`:

- `ChessPiece.java` - Immutable chess piece representation
- `ChessPieceType.java` - Enum of piece types
- `Color.java` - Enum for piece colors
- `Position.java` - Board position (row, col)
- `Move.java` - Move representation with metadata
- `GameState.java` - Enum for game states
- `GameStateSnapshot.java` - Immutable game state snapshot for undo/redo

### Utility Classes

Located in `backend/src/main/java/com/backend/util/`:

- **`FENParser.java`** - FEN notation parser and generator
  - Parse FEN strings to board state
  - Generate FEN from board state
  - No external dependencies

- **`PGNExporter.java`** - PGN format exporter
  - Export games to standard PGN format
  - Move notation generation

### AI Components

Located in `backend/src/main/java/com/backend/ai/`:

- **`BoardEvaluator.java`** - Static board position evaluation
  - Material evaluation
  - Positional bonuses
  - No external dependencies

- **`ChessAI.java`** - Minimax AI with alpha-beta pruning
  - Configurable search depth
  - Move generation and selection
  - Depends only on domain and model classes

## How to Extract to a Separate Library

### Option 1: Gradle Multi-Module Project

1. Create a new module `chess-engine-core`:
   ```
   chess-engine-core/
   ├── build.gradle.kts
   └── src/main/java/com/chessengine/
       ├── domain/
       │   ├── Chessboard.java
       │   └── ChessGame.java
       ├── models/
       │   ├── ChessPiece.java
       │   ├── Position.java
       │   ├── Move.java
       │   └── ...
       ├── util/
       │   ├── FENParser.java
       │   └── PGNExporter.java
       └── ai/
           ├── BoardEvaluator.java
           └── ChessAI.java
   ```

2. Update root `settings.gradle.kts`:
   ```kotlin
   rootProject.name = "chess-engine-reference"
   include("chess-engine-core")
   include("backend")
   ```

3. Add dependency in `backend/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(project(":chess-engine-core"))
       // ... other dependencies
   }
   ```

4. Move core classes to `chess-engine-core` module

5. Update package names and imports in backend

### Option 2: Separate Maven/Gradle Artifact

1. Create a new repository `chess-engine-core`

2. Copy core classes (domain, models, util, ai) to new project

3. Create build configuration:
   ```kotlin
   // build.gradle.kts
   plugins {
       java
       `maven-publish`
   }
   
   group = "com.chessengine"
   version = "1.0.0"
   
   publishing {
       publications {
           create<MavenPublication>("maven") {
               from(components["java"])
           }
       }
   }
   ```

4. Publish to Maven Central or private repository

5. Add dependency in consuming projects:
   ```kotlin
   dependencies {
       implementation("com.chessengine:chess-engine-core:1.0.0")
   }
   ```

### Option 3: JAR Library

1. Create standalone project with core classes

2. Build JAR:
   ```bash
   ./gradlew jar
   ```

3. Include JAR in other projects' classpath

## Dependencies

The chess engine core has ZERO external dependencies beyond Java 17 standard library.

### What's Included
- Complete chess rules implementation
- Move validation and generation
- Check/checkmate/stalemate detection
- FEN import/export
- PGN export
- Undo/redo support
- Basic AI with minimax

### What's NOT Included
- REST API endpoints (in `backend/controllers`)
- Spring Boot configuration
- Web UI
- Database persistence
- Network play

## Usage Example

```java
import com.backend.domain.ChessGame;
import com.backend.models.Position;

public class Example {
    public static void main(String[] args) {
        // Create a new game
        ChessGame game = new ChessGame();
        
        // Make a move (e2 to e4)
        Position from = new Position(2, 5);
        Position to = new Position(4, 5);
        game.MoveController(from, to);
        
        // Get valid moves for a piece
        Position[] validMoves = game.getValidMovesController(new Position(1, 7));
        
        // Export to FEN
        String fen = game.exportToFEN();
        
        // Undo last move
        game.undo();
        
        // Get AI suggested move
        import com.backend.ai.ChessAI;
        ChessAI.AIMove aiMove = ChessAI.findBestMove(game);
        if (aiMove != null) {
            game.MoveController(aiMove.from, aiMove.to);
        }
    }
}
```

## Testing

All core components have comprehensive unit tests in `backend/src/test/java/`:

- `ChessBoardTest.java` - Board and move validation tests
- `GameStateTest.java` - Check and checkmate tests
- `CastlingTest.java` - Castling rules tests
- `DrawDetectionTest.java` - Draw condition tests
- `UndoRedoTest.java` - Undo/redo functionality tests
- `FENParserTest.java` - FEN parsing tests
- `PGNExporterTest.java` - PGN export tests

These tests can be moved along with the core classes to ensure the library works correctly.

## Benefits of Extraction

1. **Reusability** - Use chess engine in multiple projects
2. **Separation of Concerns** - Pure game logic separate from API/UI
3. **Testability** - Core logic tested independently
4. **Portability** - Can be used in different frameworks (Spring, Jakarta, etc.)
5. **Version Control** - Independent versioning for chess engine
6. **Distribution** - Can be published as Maven/Gradle artifact

## Current Status

The chess engine is currently embedded within the Spring Boot backend but is architected with clean separation:

- ✅ No Spring dependencies in core classes
- ✅ No web/HTTP dependencies in core classes
- ✅ All core logic is pure Java
- ✅ Comprehensive test coverage
- ✅ Well-documented code
- ⏳ Not yet extracted to separate module (can be done following this guide)

## Recommendations

For extracting the chess engine:

1. **Short term**: Keep current structure, but be aware of the clean boundaries
2. **Medium term**: Create multi-module Gradle project (Option 1)
3. **Long term**: Publish as standalone artifact to Maven Central (Option 2)

The current architecture makes any of these transitions straightforward.
