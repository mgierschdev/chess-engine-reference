package com.backend.ai;

import com.backend.domain.Chessboard;
import com.backend.models.*;

/**
 * Chess board evaluation utility for AI move selection.
 * Provides static methods to evaluate board positions.
 */
public class BoardEvaluator {
    
    // Piece values in centipawns (1 pawn = 100)
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000; // King is invaluable
    
    // Position bonuses for pieces (simplified)
    // Pawns get bonus for being advanced
    private static final int[][] PAWN_POSITION_BONUS = {
        {0,  0,  0,  0,  0,  0,  0,  0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {5,  5, 10, 25, 25, 10,  5,  5},
        {0,  0,  0, 20, 20,  0,  0,  0},
        {5, -5,-10,  0,  0,-10, -5,  5},
        {5, 10, 10,-20,-20, 10, 10,  5},
        {0,  0,  0,  0,  0,  0,  0,  0}
    };
    
    // Knights get bonus for being in center
    private static final int[][] KNIGHT_POSITION_BONUS = {
        {-50,-40,-30,-30,-30,-30,-40,-50},
        {-40,-20,  0,  0,  0,  0,-20,-40},
        {-30,  0, 10, 15, 15, 10,  0,-30},
        {-30,  5, 15, 20, 20, 15,  5,-30},
        {-30,  0, 15, 20, 20, 15,  0,-30},
        {-30,  5, 10, 15, 15, 10,  5,-30},
        {-40,-20,  0,  5,  5,  0,-20,-40},
        {-50,-40,-30,-30,-30,-30,-40,-50}
    };
    
    /**
     * Evaluates the board position from the perspective of a given color.
     * Positive score means the color is winning, negative means losing.
     * 
     * @param board The chess board to evaluate
     * @param color The color to evaluate for
     * @return Evaluation score in centipawns
     */
    public static int evaluate(ChessPiece[][] board, Color color) {
        int score = 0;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece.type() != ChessPieceType.Empty) {
                    int pieceValue = getPieceValue(piece.type());
                    int positionBonus = getPositionBonus(piece, row, col);
                    int totalValue = pieceValue + positionBonus;
                    
                    if (piece.color() == color) {
                        score += totalValue;
                    } else {
                        score -= totalValue;
                    }
                }
            }
        }
        
        return score;
    }
    
    /**
     * Gets the base value of a piece type.
     */
    private static int getPieceValue(ChessPieceType type) {
        return switch (type) {
            case Pawn -> PAWN_VALUE;
            case Knight -> KNIGHT_VALUE;
            case Bishop -> BISHOP_VALUE;
            case Rock -> ROOK_VALUE;
            case Queen -> QUEEN_VALUE;
            case King -> KING_VALUE;
            default -> 0;
        };
    }
    
    /**
     * Gets position bonus for a piece based on its location.
     * Returns 0 for pieces without position tables.
     */
    private static int getPositionBonus(ChessPiece piece, int row, int col) {
        // Adjust row for black pieces (flip the board)
        int adjustedRow = piece.color() == Color.White ? row : 7 - row;
        
        return switch (piece.type()) {
            case Pawn -> PAWN_POSITION_BONUS[adjustedRow][col];
            case Knight -> KNIGHT_POSITION_BONUS[adjustedRow][col];
            default -> 0; // No position bonus for other pieces in this simple evaluation
        };
    }
    
    /**
     * Quick evaluation for terminal positions (checkmate/stalemate).
     * 
     * @param board The chess board
     * @param gameState The current game state
     * @param turn Whose turn it is
     * @param evaluatingFor The color we're evaluating for
     * @return Large positive/negative value for checkmate, 0 for stalemate/draw
     */
    public static int evaluateTerminal(ChessPiece[][] board, GameState gameState, Color turn, Color evaluatingFor) {
        if (gameState == GameState.Checkmate) {
            // If it's turn's move and they're checkmated, they lost
            // If evaluatingFor lost, return very negative
            // If opponent lost, return very positive
            if (turn == evaluatingFor) {
                return -100000; // We're checkmated
            } else {
                return 100000; // Opponent is checkmated
            }
        } else if (gameState == GameState.DrawByStalemate ||
                   gameState == GameState.DrawByFiftyMove ||
                   gameState == GameState.DrawByRepetition) {
            return 0; // Draw
        }
        
        // Not a terminal state, use regular evaluation
        return evaluate(board, evaluatingFor);
    }
}
