# Chess Engine Core API Reference

## Quick Start

```java
import com.backend.domain.ChessGame;
import com.backend.models.*;

// Create new game
ChessGame game = new ChessGame();

// Make a move
Position from = new Position(2, 5); // e2
Position to = new Position(4, 5);   // e4
ChessPiece result = game.MoveController(from, to);

// Check if move was valid
if (result.type() != ChessPieceType.Invalid) {
    System.out.println("Move successful!");
}
```

## ChessGame API

### Core Methods

#### `MoveController(Position from, Position to)`
Make a move on the board.
- **Parameters**: Source and target positions (1-indexed)
- **Returns**: `ChessPiece` - captured piece, or Invalid if move is illegal
- **Side effects**: Updates game state, switches turn

#### `MoveController(Position from, Position to, ChessPieceType promotionType)`
Make a move with pawn promotion.
- **Parameters**: Source, target, and promotion piece type
- **Returns**: `ChessPiece` - captured piece, or Invalid if move is illegal

#### `getValidMovesController(Position position)`
Get all valid moves for a piece.
- **Parameters**: Position of piece (1-indexed)
- **Returns**: Array of valid target positions

### Game State

#### `getTurn()`
Get current player's turn.
- **Returns**: `Color.White` or `Color.Black`

#### `getGameState()`
Get current game state.
- **Returns**: `GameState` enum (Free, Check, Checkmate, DrawByStalemate, etc.)

#### `getChessboard()`
Get board representation for display.
- **Returns**: Array of `ChessPieceResponse` for rendering

#### `getMoveHistory()`
Get list of all moves made.
- **Returns**: `List<Move>` - move history

#### `getCaptured(Color color)`
Get captured pieces for a player.
- **Returns**: `Set<ChessPiece>` - pieces captured by the color

### FEN Support

#### `importFromFEN(String fen)`
Import position from FEN notation.
- **Parameters**: FEN string
- **Throws**: `IllegalArgumentException` for invalid FEN

#### `exportToFEN()`
Export current position to FEN.
- **Returns**: FEN string

#### `exportToPGN()`
Export game in PGN format.
- **Returns**: PGN formatted string

### Undo/Redo

#### `undo()`
Undo last move.
- **Returns**: `true` if successful, `false` if nothing to undo

#### `redo()`
Redo previously undone move.
- **Returns**: `true` if successful, `false` if nothing to redo

#### `canUndo()`
Check if undo is available.
- **Returns**: `boolean`

#### `canRedo()`
Check if redo is available.
- **Returns**: `boolean`

## AI API

### ChessAI.findBestMove(ChessGame game)
Find best move using minimax algorithm.
```java
import com.backend.ai.ChessAI;

ChessAI.AIMove move = ChessAI.findBestMove(game);
if (move != null) {
    game.MoveController(move.from, move.to);
}
```

### ChessAI.findBestMove(ChessGame game, int depth)
Find best move with custom search depth.
- **Parameters**: game state, search depth (default: 3)
- **Returns**: `AIMove` with from/to positions and evaluation score

## FEN Parser API

### FENParser.parseFEN(String fen)
Parse FEN string to board state.
```java
import com.backend.util.FENParser;

FENParser.FENParseResult result = FENParser.parseFEN(fen);
// Access result.board, result.activeColor, result.castlingRights, etc.
```

### FENParser.generateFEN(...)
Generate FEN from board state.
```java
String fen = FENParser.generateFEN(
    board, activeColor,
    whiteKingMoved, blackKingMoved,
    whiteKingsideRookMoved, whiteQueensideRookMoved,
    blackKingsideRookMoved, blackQueensideRookMoved,
    enPassantTarget, halfMoveClock, fullMoveNumber
);
```

## Model Classes

### ChessPiece
Immutable record representing a chess piece.
- `type()` - ChessPieceType (Pawn, Knight, Bishop, Rock, Queen, King)
- `color()` - Color (White, Black, None)

### Position
Represents board position.
- `row` - Row index (1-8 for user-facing APIs, 0-7 internally)
- `col` - Column index (1-8 for user-facing APIs, 0-7 internally)

### Move
Represents a chess move with metadata.
- `getFrom()` - Source position
- `getTo()` - Target position
- `getPiece()` - Piece that moved
- `getCapturedPiece()` - Piece captured (if any)
- `isCapture()` - Whether this was a capture
- `isPawnMove()` - Whether a pawn moved
- `isEnPassant()` - Whether this was en passant
- `isCastling()` - Whether this was castling

### GameState Enum
- `Free` - Normal play
- `Check` - King in check
- `Checkmate` - Game over, checkmate
- `DrawByStalemate` - Game over, stalemate
- `DrawByFiftyMove` - Draw by 50-move rule
- `DrawByRepetition` - Draw by threefold repetition

## Board Evaluator API

### BoardEvaluator.evaluate(ChessPiece[][] board, Color color)
Evaluate board position for a color.
```java
import com.backend.ai.BoardEvaluator;

int score = BoardEvaluator.evaluate(board, Color.White);
// Positive = White winning, Negative = Black winning
```

## Examples

### Basic Game Flow
```java
ChessGame game = new ChessGame();

// White moves e2-e4
game.MoveController(new Position(2, 5), new Position(4, 5));

// Black moves e7-e5
game.MoveController(new Position(7, 5), new Position(5, 5));

// Check game state
if (game.getGameState() == GameState.Check) {
    System.out.println("Check!");
}
```

### FEN Import/Export
```java
// Import from FEN
String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
game.importFromFEN(fen);

// Play some moves...

// Export current position
String currentFEN = game.exportToFEN();
System.out.println(currentFEN);
```

### AI Opponent
```java
ChessGame game = new ChessGame();

while (game.getGameState() == GameState.Free || 
       game.getGameState() == GameState.Check) {
    
    if (game.getTurn() == Color.White) {
        // Human move (get from UI)
        game.MoveController(userFrom, userTo);
    } else {
        // AI move
        ChessAI.AIMove aiMove = ChessAI.findBestMove(game);
        if (aiMove != null) {
            game.MoveController(aiMove.from, aiMove.to);
            System.out.println("AI moved from " + aiMove.from + 
                             " to " + aiMove.to + 
                             " (score: " + aiMove.score + ")");
        }
    }
}

System.out.println("Game over: " + game.getGameState());
```

### Undo/Redo
```java
// Make moves
game.MoveController(new Position(2, 5), new Position(4, 5));
game.MoveController(new Position(7, 5), new Position(5, 5));

// Undo last move
if (game.canUndo()) {
    game.undo();
}

// Redo
if (game.canRedo()) {
    game.redo();
}
```

## Thread Safety

The chess engine classes are **not thread-safe**. If using in a multi-threaded environment:
- Use one `ChessGame` instance per game/thread
- Or synchronize access externally
- `BoardEvaluator` methods are stateless and thread-safe

## Performance

- Move validation: O(n) where n = number of pieces
- AI move generation (depth 3): ~0.5-2 seconds typical
- FEN parsing: O(1) for fixed board size
- Undo/redo: O(1) with memory overhead for snapshots
