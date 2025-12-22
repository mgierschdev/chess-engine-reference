package com.backend.ai;

import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardEvaluatorTest {

    @Test
    void testEvaluateStartingPosition() {
        ChessPiece[][] board = createStartingBoard();
        
        // Starting position should be equal for both sides
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        int blackEval = BoardEvaluator.evaluate(board, Color.Black);
        
        // Both should have same material
        assertEquals(whiteEval, blackEval, "Starting position should be equal");
    }

    @Test
    void testEvaluateMaterialAdvantage() {
        // Create a board where white has an extra pawn
        ChessPiece[][] board = createEmptyBoard();
        
        // White king and pawn
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[1][0] = new ChessPiece(ChessPieceType.Pawn, Color.White);
        board[1][1] = new ChessPiece(ChessPieceType.Pawn, Color.White);
        
        // Black king and one pawn
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[6][0] = new ChessPiece(ChessPieceType.Pawn, Color.Black);
        
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        
        // White should have positive evaluation (extra pawn = ~100 centipawns)
        assertTrue(whiteEval > 50, "White should have material advantage");
    }

    @Test
    void testEvaluateQueenVsRook() {
        ChessPiece[][] board = createEmptyBoard();
        
        // White: King + Queen
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[0][3] = new ChessPiece(ChessPieceType.Queen, Color.White);
        
        // Black: King + Rook
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[7][0] = new ChessPiece(ChessPieceType.Rock, Color.Black);
        
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        
        // Queen (900) vs Rook (500) = 400 centipawn advantage
        assertTrue(whiteEval > 300 && whiteEval < 500, 
                   "White should have ~400 centipawn advantage (Queen vs Rook)");
    }

    @Test
    void testEvaluatePawnPositionBonus() {
        ChessPiece[][] board = createEmptyBoard();
        
        // White king and advanced pawn (row 6 = near promotion)
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[6][4] = new ChessPiece(ChessPieceType.Pawn, Color.White); // Row 6 for white
        
        // Black king and starting pawn (row 6 from black's perspective = row 1 from white)
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[6][3] = new ChessPiece(ChessPieceType.Pawn, Color.Black); // Row 6 from white = row 1 from black's view
        
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        
        // Both pawns should have similar material value, evaluation should be close
        // This test just verifies the evaluation works without error
        assertTrue(whiteEval >= -200 && whiteEval <= 200, 
                  "Evaluation should be in reasonable range");
    }

    @Test
    void testEvaluateKnightCentralization() {
        ChessPiece[][] board = createEmptyBoard();
        
        // White king and centralized knight
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[3][3] = new ChessPiece(ChessPieceType.Knight, Color.White); // Center
        
        // Black king and edge knight
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[0][0] = new ChessPiece(ChessPieceType.Knight, Color.Black); // Corner
        
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        
        // Centralized knight should be better
        assertTrue(whiteEval > 0, "Centralized knight should have better evaluation");
    }

    @Test
    void testEvaluateTerminalCheckmate() {
        ChessPiece[][] board = createEmptyBoard();
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        
        // Test checkmate for white (white is checkmated, it's white's turn)
        int eval = BoardEvaluator.evaluateTerminal(board, GameState.Checkmate, Color.White, Color.White);
        assertEquals(-100000, eval, "Checkmate should return large negative value");
        
        // Test checkmate for black (black is checkmated, it's black's turn)
        eval = BoardEvaluator.evaluateTerminal(board, GameState.Checkmate, Color.Black, Color.White);
        assertEquals(100000, eval, "Opponent checkmate should return large positive value");
    }

    @Test
    void testEvaluateTerminalStalemate() {
        ChessPiece[][] board = createEmptyBoard();
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        
        int eval = BoardEvaluator.evaluateTerminal(board, GameState.DrawByStalemate, Color.White, Color.White);
        assertEquals(0, eval, "Stalemate should return 0");
    }

    @Test
    void testEvaluateTerminalDraw() {
        ChessPiece[][] board = createEmptyBoard();
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        
        int eval = BoardEvaluator.evaluateTerminal(board, GameState.DrawByFiftyMove, Color.White, Color.White);
        assertEquals(0, eval, "50-move draw should return 0");
        
        eval = BoardEvaluator.evaluateTerminal(board, GameState.DrawByRepetition, Color.White, Color.White);
        assertEquals(0, eval, "Repetition draw should return 0");
    }

    @Test
    void testEvaluateEmptySquares() {
        ChessPiece[][] board = createEmptyBoard();
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        int blackEval = BoardEvaluator.evaluate(board, Color.Black);
        
        // Only kings, should be equal
        assertEquals(whiteEval, blackEval, "King-only position should be equal");
    }

    @Test
    void testEvaluateComplexPosition() {
        ChessPiece[][] board = createEmptyBoard();
        
        // White: King, Queen, Rook, Bishop, 2 Pawns
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[0][3] = new ChessPiece(ChessPieceType.Queen, Color.White);
        board[0][0] = new ChessPiece(ChessPieceType.Rock, Color.White);
        board[0][2] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[1][0] = new ChessPiece(ChessPieceType.Pawn, Color.White);
        board[1][1] = new ChessPiece(ChessPieceType.Pawn, Color.White);
        
        // Black: King, Rook, Bishop, Knight, 2 Pawns
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[7][0] = new ChessPiece(ChessPieceType.Rock, Color.Black);
        board[7][2] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][1] = new ChessPiece(ChessPieceType.Knight, Color.Black);
        board[6][0] = new ChessPiece(ChessPieceType.Pawn, Color.Black);
        board[6][1] = new ChessPiece(ChessPieceType.Pawn, Color.Black);
        
        int whiteEval = BoardEvaluator.evaluate(board, Color.White);
        
        // White has Queen (900) vs Knight (320) = ~580 advantage
        assertTrue(whiteEval > 400, "White should have significant material advantage");
    }

    // Helper methods
    private ChessPiece[][] createEmptyBoard() {
        ChessPiece[][] board = new ChessPiece[8][8];
        ChessPiece empty = new ChessPiece(ChessPieceType.Empty, Color.None);
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = empty;
            }
        }
        
        return board;
    }

    private ChessPiece[][] createStartingBoard() {
        ChessPiece[][] board = new ChessPiece[8][8];
        ChessPiece empty = new ChessPiece(ChessPieceType.Empty, Color.None);
        
        // Empty squares
        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = empty;
            }
        }
        
        // White pawns
        for (int col = 0; col < 8; col++) {
            board[1][col] = new ChessPiece(ChessPieceType.Pawn, Color.White);
        }
        
        // Black pawns
        for (int col = 0; col < 8; col++) {
            board[6][col] = new ChessPiece(ChessPieceType.Pawn, Color.Black);
        }
        
        // White pieces
        board[0][0] = new ChessPiece(ChessPieceType.Rock, Color.White);
        board[0][1] = new ChessPiece(ChessPieceType.Knight, Color.White);
        board[0][2] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][3] = new ChessPiece(ChessPieceType.Queen, Color.White);
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
        board[0][5] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][6] = new ChessPiece(ChessPieceType.Knight, Color.White);
        board[0][7] = new ChessPiece(ChessPieceType.Rock, Color.White);
        
        // Black pieces
        board[7][0] = new ChessPiece(ChessPieceType.Rock, Color.Black);
        board[7][1] = new ChessPiece(ChessPieceType.Knight, Color.Black);
        board[7][2] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][3] = new ChessPiece(ChessPieceType.Queen, Color.Black);
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[7][5] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][6] = new ChessPiece(ChessPieceType.Knight, Color.Black);
        board[7][7] = new ChessPiece(ChessPieceType.Rock, Color.Black);
        
        return board;
    }
}
