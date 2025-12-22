package com.backend.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Immutable snapshot of the complete game state at a point in time.
 * Used for undo/redo functionality.
 */
public class GameStateSnapshot {
    private final ChessPiece[][] boardCopy;
    private final GameState gameState;
    private final Color turn;
    private final Position enPassantTarget;
    private final Set<ChessPiece> takenWhite;
    private final Set<ChessPiece> takenBlack;
    private final int halfMoveClock;
    private final boolean whiteKingMoved;
    private final boolean blackKingMoved;
    private final boolean whiteKingsideRookMoved;
    private final boolean whiteQueensideRookMoved;
    private final boolean blackKingsideRookMoved;
    private final boolean blackQueensideRookMoved;
    
    public GameStateSnapshot(ChessPiece[][] board, GameState gameState, Color turn,
                           Position enPassantTarget, Set<ChessPiece> takenWhite,
                           Set<ChessPiece> takenBlack, int halfMoveClock,
                           boolean whiteKingMoved, boolean blackKingMoved,
                           boolean whiteKingsideRookMoved, boolean whiteQueensideRookMoved,
                           boolean blackKingsideRookMoved, boolean blackQueensideRookMoved) {
        // Deep copy the board
        this.boardCopy = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                this.boardCopy[row][col] = board[row][col];
            }
        }
        
        this.gameState = gameState;
        this.turn = turn;
        this.enPassantTarget = enPassantTarget;
        this.takenWhite = new HashSet<>(takenWhite);
        this.takenBlack = new HashSet<>(takenBlack);
        this.halfMoveClock = halfMoveClock;
        this.whiteKingMoved = whiteKingMoved;
        this.blackKingMoved = blackKingMoved;
        this.whiteKingsideRookMoved = whiteKingsideRookMoved;
        this.whiteQueensideRookMoved = whiteQueensideRookMoved;
        this.blackKingsideRookMoved = blackKingsideRookMoved;
        this.blackQueensideRookMoved = blackQueensideRookMoved;
    }
    
    public ChessPiece[][] getBoardCopy() {
        // Return a deep copy to prevent modification
        ChessPiece[][] copy = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                copy[row][col] = boardCopy[row][col];
            }
        }
        return copy;
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public Color getTurn() {
        return turn;
    }
    
    public Position getEnPassantTarget() {
        return enPassantTarget;
    }
    
    public Set<ChessPiece> getTakenWhite() {
        return new HashSet<>(takenWhite);
    }
    
    public Set<ChessPiece> getTakenBlack() {
        return new HashSet<>(takenBlack);
    }
    
    public int getHalfMoveClock() {
        return halfMoveClock;
    }
    
    public boolean getWhiteKingMoved() {
        return whiteKingMoved;
    }
    
    public boolean getBlackKingMoved() {
        return blackKingMoved;
    }
    
    public boolean getWhiteKingsideRookMoved() {
        return whiteKingsideRookMoved;
    }
    
    public boolean getWhiteQueensideRookMoved() {
        return whiteQueensideRookMoved;
    }
    
    public boolean getBlackKingsideRookMoved() {
        return blackKingsideRookMoved;
    }
    
    public boolean getBlackQueensideRookMoved() {
        return blackQueensideRookMoved;
    }
}
