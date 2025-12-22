package com.backend.ai;

import com.backend.domain.ChessGame;
import com.backend.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple chess AI using minimax algorithm with alpha-beta pruning.
 * This is a basic implementation for demonstration purposes.
 */
public class ChessAI {
    
    private static final int MAX_DEPTH = 3; // Search depth (3 moves ahead)
    private static final Random random = new Random();
    
    /**
     * Represents a possible move with its source and target positions.
     */
    public static class AIMove {
        public final Position from;
        public final Position to;
        public final int score;
        
        public AIMove(Position from, Position to, int score) {
            this.from = from;
            this.to = to;
            this.score = score;
        }
    }
    
    /**
     * Finds the best move for the current player using minimax algorithm.
     * 
     * @param game The current game state
     * @return The best move found, or null if no legal moves
     */
    public static AIMove findBestMove(ChessGame game) {
        return findBestMove(game, MAX_DEPTH);
    }
    
    /**
     * Finds the best move with a specified search depth.
     * 
     * @param game The current game state
     * @param depth Maximum search depth
     * @return The best move found, or null if no legal moves
     */
    public static AIMove findBestMove(ChessGame game, int depth) {
        Color aiColor = game.getTurn();
        List<AIMove> legalMoves = getAllLegalMoves(game, aiColor);
        
        if (legalMoves.isEmpty()) {
            return null; // No legal moves (checkmate or stalemate)
        }
        
        AIMove bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        
        // Evaluate each possible move
        for (AIMove move : legalMoves) {
            // Save current state
            String fenBeforeMove = game.exportToFEN();
            
            // Make the move
            ChessPiece result = game.MoveController(move.from, move.to);
            
            if (result.type() != ChessPieceType.Invalid) {
                // Evaluate this position
                int score = -minimax(game, depth - 1, -beta, -alpha, false, aiColor);
                
                // Undo the move
                game.undo();
                
                // Update best move if this is better
                if (score > bestScore || (score == bestScore && random.nextBoolean())) {
                    bestScore = score;
                    bestMove = new AIMove(move.from, move.to, score);
                }
                
                alpha = Math.max(alpha, score);
            }
        }
        
        return bestMove != null ? new AIMove(bestMove.from, bestMove.to, bestScore) : legalMoves.get(0);
    }
    
    /**
     * Minimax algorithm with alpha-beta pruning.
     * 
     * @param game Current game state
     * @param depth Remaining search depth
     * @param alpha Alpha value for pruning
     * @param beta Beta value for pruning
     * @param isMaximizing True if maximizing, false if minimizing
     * @param aiColor The AI's color
     * @return Best evaluation score
     */
    private static int minimax(ChessGame game, int depth, int alpha, int beta, 
                               boolean isMaximizing, Color aiColor) {
        // Base case: depth limit reached or game over
        if (depth == 0 || isGameOver(game)) {
            return BoardEvaluator.evaluateTerminal(
                game.getChessboardInternal().getBoard(),
                game.getGameState(),
                game.getTurn(),
                aiColor
            );
        }
        
        Color currentColor = game.getTurn();
        List<AIMove> legalMoves = getAllLegalMoves(game, currentColor);
        
        if (legalMoves.isEmpty()) {
            // No legal moves - checkmate or stalemate
            return BoardEvaluator.evaluateTerminal(
                game.getChessboardInternal().getBoard(),
                game.getGameState(),
                game.getTurn(),
                aiColor
            );
        }
        
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (AIMove move : legalMoves) {
                ChessPiece result = game.MoveController(move.from, move.to);
                if (result.type() != ChessPieceType.Invalid) {
                    int eval = minimax(game, depth - 1, alpha, beta, false, aiColor);
                    game.undo();
                    
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break; // Beta cutoff
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (AIMove move : legalMoves) {
                ChessPiece result = game.MoveController(move.from, move.to);
                if (result.type() != ChessPieceType.Invalid) {
                    int eval = minimax(game, depth - 1, alpha, beta, true, aiColor);
                    game.undo();
                    
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break; // Alpha cutoff
                    }
                }
            }
            return minEval;
        }
    }
    
    /**
     * Gets all legal moves for a given color.
     */
    private static List<AIMove> getAllLegalMoves(ChessGame game, Color color) {
        List<AIMove> moves = new ArrayList<>();
        ChessPiece[][] board = game.getChessboardInternal().getBoard();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece.color() == color) {
                    Position from = new Position(row + 1, col + 1); // Add offset for controller
                    Position[] validMoves = game.getValidMovesController(from);
                    
                    for (Position to : validMoves) {
                        moves.add(new AIMove(from, to, 0));
                    }
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Checks if the game is over.
     */
    private static boolean isGameOver(ChessGame game) {
        GameState state = game.getGameState();
        return state == GameState.Checkmate ||
               state == GameState.DrawByStalemate ||
               state == GameState.DrawByFiftyMove ||
               state == GameState.DrawByRepetition;
    }
}
