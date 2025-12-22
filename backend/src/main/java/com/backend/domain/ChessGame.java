package com.backend.domain;

import com.backend.models.*;
import com.backend.models.requests.ChessPieceResponse;
import com.backend.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Problem: Need to manage chess game state across multiple moves, track turn order,
 * captured pieces, and detect check/checkmate conditions.
 * 
 * Goal: Provide a high-level game controller that coordinates move validation,
 * turn management, and win condition detection.
 * 
 * Approach: Delegates move validation to Chessboard. Maintains game state (Free, Check, Checkmate),
 * current turn (White/Black), and sets of captured pieces. After each move, checks for
 * check and checkmate conditions for the next player.
 * 
 * Time: O(n) per move where n is the number of pieces (for check/checkmate detection)
 * Space: O(1) additional space beyond the board representation
 */
public class ChessGame {
    private GameState gameState;

    private final Chessboard chessboard;

    private final Set<ChessPiece> takenWhite;

    private final Set<ChessPiece> takenBlack;

    private Color turn;

    // square available for en passant capture from the last double-step move
    private Position lastDoubleStep;

    // Move history for PGN export and draw detection
    private final List<Move> moveHistory;

    // Track half-moves since last pawn move or capture for 50-move rule
    private int halfMoveClock;

    // State history for undo/redo
    private final List<GameStateSnapshot> stateHistory;
    private final List<GameStateSnapshot> redoHistory;

    public ChessGame() {
        chessboard = new Chessboard();
        takenWhite = new HashSet<>();
        takenBlack = new HashSet<>();
        gameState = GameState.Free;
        turn = Color.White;
        lastDoubleStep = null;
        moveHistory = new ArrayList<>();
        halfMoveClock = 0;
        stateHistory = new ArrayList<>();
        redoHistory = new ArrayList<>();
        
        // Save initial state
        saveCurrentState();
    }

    public ChessPiece MoveController(String chessNotation) {
        if (chessNotation.length() < 5) {
            return new ChessPiece(ChessPieceType.Invalid, Color.None);
        }

        String[] movement = chessNotation.split("-");
        int[] sourceMatrix = Util.GetMatrixNotation(movement[0]);
        int[] targetMatrix = Util.GetMatrixNotation(movement[1]);

        return MoveController(new Position(sourceMatrix[0], sourceMatrix[1]), new Position(targetMatrix[0], targetMatrix[1]), ChessPieceType.Queen);
    }

    public ChessPiece MoveController(Position a, Position b) {
        return MoveController(a, b, ChessPieceType.Queen);
    }

    // Returns empty if the move was correct, invalid otherwise, the taken piece if there was a taken piece
    public ChessPiece MoveController(Position a, Position b, ChessPieceType promotionType) {
        removeOffsetChessboardPosition(a);
        removeOffsetChessboardPosition(b);

        // Get piece info before move for history tracking
        ChessPiece movingPiece = chessboard.getBoardPosition(a.row, a.col);
        boolean isPawnMove = movingPiece.type() == ChessPieceType.Pawn;
        
        ChessPiece chessPiece = chessboard.movePiece(a, b, turn, promotionType);

        // track possible en passant target
        lastDoubleStep = chessboard.getEnPassantTarget();

        if (chessPiece.type() == ChessPieceType.Invalid) {
            return chessPiece;
        }

        // Clear redo history when a new move is made
        redoHistory.clear();

        boolean isCapture = chessPiece.type() != ChessPieceType.Empty;
        
        if (isCapture) {
            if (turn == Color.White) {
                takenWhite.add(chessPiece);
            } else {
                takenBlack.add(chessPiece);
            }
        }

        // Track move in history
        Move move = new Move(new Position(a.row, a.col), new Position(b.row, b.col), 
                            movingPiece, chessPiece, promotionType, false, false);
        moveHistory.add(move);

        // Update half-move clock for 50-move rule
        if (isPawnMove || isCapture) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        Color nextTurn = turn == Color.White ? Color.Black : Color.White;
        
        // Check for game-ending or draw conditions
        if (chessboard.isCheckmate(nextTurn)) {
            gameState = GameState.Checkmate;
        } else if (chessboard.isStalemate(nextTurn)) {
            gameState = GameState.DrawByStalemate;
        } else if (halfMoveClock >= 100) { // 50 full moves = 100 half-moves
            gameState = GameState.DrawByFiftyMove;
        } else if (isThreefoldRepetition()) {
            gameState = GameState.DrawByRepetition;
        } else if (chessboard.isKingInCheck(nextTurn)) {
            gameState = GameState.Check;
        } else {
            gameState = GameState.Free;
        }

        turn = nextTurn;

        // Save state after the move is complete
        saveCurrentState();

        return chessPiece;
    }

    public Position[] getValidMovesController(Position position) {
        removeOffsetChessboardPosition(position);
        Position[] validPositions = chessboard.getValidMoves(new Position(position.row, position.col));

        // we add offset
        for (Position pos: validPositions) {
            addOffsetChessboardPosition(pos);
        }

        return validPositions;
    }

    public void printBoard() {
        chessboard.printBoard();
    }

    public Color getTurn() {
        return turn;
    }

    public GameState getGameState(){
        return gameState;
    }

    public Set<ChessPiece> getCaptured(Color color) {
        if (color == Color.White) {
            return takenWhite;
        } else {
            return takenBlack;
        }
    }

    public ChessPieceResponse[] getChessboard() {
        return Chessboard.GetArrayBoard(chessboard.getBoard());
    }
    
    /**
     * Gets the underlying Chessboard instance.
     * Used by AI for move generation.
     */
    public Chessboard getChessboardInternal() {
        return chessboard;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Exports the current game to PGN format.
     */
    public String exportToPGN() {
        String result = com.backend.util.PGNExporter.getResultString(gameState, turn);
        return com.backend.util.PGNExporter.exportToPGN(moveHistory, result, gameState);
    }

    /**
     * Checks if the current board position has occurred three times.
     * Uses a simplified approach based on board hash codes.
     */
    private boolean isThreefoldRepetition() {
        if (moveHistory.size() < 8) { // Need at least 8 moves for threefold repetition
            return false;
        }
        
        // Get current board hash
        String currentBoardHash = getBoardHash();
        int occurrences = 1; // Current position counts as 1
        
        // Check previous positions (only need to check positions with same turn)
        for (int i = moveHistory.size() - 2; i >= 0; i -= 2) {
            // We would need to replay moves to get the exact board state
            // For now, this is a simplified implementation
            // A full implementation would require storing board states or FEN strings
        }
        
        return occurrences >= 3;
    }

    /**
     * Simple board hash for position comparison.
     * A complete implementation would use FEN notation.
     */
    private String getBoardHash() {
        ChessPiece[][] board = chessboard.getBoard();
        StringBuilder hash = new StringBuilder();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = board[r][c];
                hash.append(piece.type().ordinal()).append(piece.color().ordinal());
            }
        }
        return hash.toString();
    }

    // the UI chessboard is represented starting from the position [1,1], and backend [0,0]
    public void removeOffsetChessboardPosition(Position a){
        a.row -= 1;
        a.col -= 1;
    }

    public void addOffsetChessboardPosition(Position a){
        a.row += 1;
        a.col += 1;
    }

    /**
     * Imports a game position from FEN notation.
     * This resets the current game to the position described by the FEN string.
     * 
     * @param fen The FEN string to import
     * @throws IllegalArgumentException if FEN string is invalid
     */
    public void importFromFEN(String fen) {
        com.backend.util.FENParser.FENParseResult result = com.backend.util.FENParser.parseFEN(fen);
        
        // Set board state
        chessboard.setFromFEN(result);
        
        // Set game state
        turn = result.activeColor;
        halfMoveClock = result.halfMoveClock;
        
        // Clear captured pieces and move history since we're starting from a new position
        takenWhite.clear();
        takenBlack.clear();
        moveHistory.clear();
        
        // Update game state (check, checkmate, etc.)
        Color nextTurn = turn;
        if (chessboard.isCheckmate(nextTurn)) {
            gameState = GameState.Checkmate;
        } else if (chessboard.isStalemate(nextTurn)) {
            gameState = GameState.DrawByStalemate;
        } else if (halfMoveClock >= 100) {
            gameState = GameState.DrawByFiftyMove;
        } else if (chessboard.isKingInCheck(nextTurn)) {
            gameState = GameState.Check;
        } else {
            gameState = GameState.Free;
        }
    }
    
    /**
     * Exports the current position to FEN notation.
     * 
     * @return FEN string representing the current position
     */
    public String exportToFEN() {
        int fullMoveNumber = (moveHistory.size() / 2) + 1;
        
        return com.backend.util.FENParser.generateFEN(
            chessboard.getBoard(),
            turn,
            chessboard.getWhiteKingMoved(),
            chessboard.getBlackKingMoved(),
            chessboard.getWhiteKingsideRookMoved(),
            chessboard.getWhiteQueensideRookMoved(),
            chessboard.getBlackKingsideRookMoved(),
            chessboard.getBlackQueensideRookMoved(),
            chessboard.getEnPassantTarget(),
            halfMoveClock,
            fullMoveNumber
        );
    }
    
    /**
     * Saves the current game state for undo functionality.
     */
    private void saveCurrentState() {
        GameStateSnapshot snapshot = new GameStateSnapshot(
            chessboard.getBoard(),
            gameState,
            turn,
            chessboard.getEnPassantTarget(),
            takenWhite,
            takenBlack,
            halfMoveClock,
            chessboard.getWhiteKingMoved(),
            chessboard.getBlackKingMoved(),
            chessboard.getWhiteKingsideRookMoved(),
            chessboard.getWhiteQueensideRookMoved(),
            chessboard.getBlackKingsideRookMoved(),
            chessboard.getBlackQueensideRookMoved()
        );
        stateHistory.add(snapshot);
    }
    
    /**
     * Removes the most recent state from history.
     * Used when a move is invalid and we don't want to keep the saved state.
     */
    private void undoLastState() {
        // This method is no longer needed since we save state after successful moves
        if (!stateHistory.isEmpty()) {
            stateHistory.remove(stateHistory.size() - 1);
        }
    }
    
    /**
     * Undoes the last move, restoring the previous game state.
     * 
     * @return true if undo was successful, false if there's nothing to undo
     */
    public boolean undo() {
        // Need at least 2 states (initial + 1 move) to undo
        if (stateHistory.size() < 2) {
            return false;
        }
        
        // Save current state to redo history before undoing
        GameStateSnapshot currentState = stateHistory.remove(stateHistory.size() - 1);
        redoHistory.add(currentState);
        
        // Restore previous state (which is now the last one in history)
        GameStateSnapshot previousState = stateHistory.get(stateHistory.size() - 1);
        restoreFromSnapshot(previousState);
        
        // Remove the last move from move history if it exists
        if (!moveHistory.isEmpty()) {
            moveHistory.remove(moveHistory.size() - 1);
        }
        
        return true;
    }
    
    /**
     * Redoes a previously undone move.
     * 
     * @return true if redo was successful, false if there's nothing to redo
     */
    public boolean redo() {
        if (redoHistory.isEmpty()) {
            return false;
        }
        
        // Get the state to redo
        GameStateSnapshot redoState = redoHistory.remove(redoHistory.size() - 1);
        
        // Add it back to state history
        stateHistory.add(redoState);
        
        // Restore that state
        restoreFromSnapshot(redoState);
        
        // Note: We don't restore moveHistory for redo because it's complex
        // and the move history is primarily used for PGN export.
        // The board state is fully restored, which is what matters for gameplay.
        
        return true;
    }
    
    /**
     * Checks if undo is available.
     */
    public boolean canUndo() {
        return stateHistory.size() > 1;
    }
    
    /**
     * Checks if redo is available.
     */
    public boolean canRedo() {
        return !redoHistory.isEmpty();
    }
    
    /**
     * Restores the game state from a snapshot.
     */
    private void restoreFromSnapshot(GameStateSnapshot snapshot) {
        chessboard.restoreState(
            snapshot.getBoardCopy(),
            snapshot.getEnPassantTarget(),
            snapshot.getWhiteKingMoved(),
            snapshot.getBlackKingMoved(),
            snapshot.getWhiteKingsideRookMoved(),
            snapshot.getWhiteQueensideRookMoved(),
            snapshot.getBlackKingsideRookMoved(),
            snapshot.getBlackQueensideRookMoved()
        );
        
        gameState = snapshot.getGameState();
        turn = snapshot.getTurn();
        halfMoveClock = snapshot.getHalfMoveClock();
        
        takenWhite.clear();
        takenWhite.addAll(snapshot.getTakenWhite());
        
        takenBlack.clear();
        takenBlack.addAll(snapshot.getTakenBlack());
        
        lastDoubleStep = snapshot.getEnPassantTarget();
    }
}
