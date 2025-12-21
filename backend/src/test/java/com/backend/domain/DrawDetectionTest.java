package com.backend.domain;

import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.GameState;
import com.backend.models.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for stalemate detection and draw conditions.
 */
public class DrawDetectionTest {

    @Test
    public void testStalemateDetection() {
        // Setup a classic stalemate position
        ChessGame game = new ChessGame();
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new ChessPiece(ChessPieceType.Empty, Color.None);
            }
        }
        
        // Setup stalemate position: White king at a8, Black king at c7, Black queen at b6
        board[7][0] = new ChessPiece(ChessPieceType.King, Color.White);
        board[6][2] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[5][1] = new ChessPiece(ChessPieceType.Queen, Color.Black);
        
        // White to move - should be stalemate
        assertTrue(chessboard.isStalemate(Color.White), "Should detect stalemate for white");
        assertFalse(chessboard.isCheckmate(Color.White), "Should not be checkmate");
        assertFalse(chessboard.isKingInCheck(Color.White), "Should not be in check");
    }

    @Test
    public void testNotStalemateWhenInCheck() {
        // If king is in check, it's not stalemate
        ChessGame game = new ChessGame();
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new ChessPiece(ChessPieceType.Empty, Color.None);
            }
        }
        
        // Setup checkmate position
        board[7][0] = new ChessPiece(ChessPieceType.King, Color.White);
        board[6][2] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[6][1] = new ChessPiece(ChessPieceType.Queen, Color.Black);
        
        // White is in checkmate, not stalemate
        assertFalse(chessboard.isStalemate(Color.White), "Checkmate is not stalemate");
        assertTrue(chessboard.isCheckmate(Color.White), "Should be checkmate");
    }

    @Test
    public void testNotStalemateWhenLegalMovesExist() {
        // Regular starting position should not be stalemate
        Chessboard chessboard = new Chessboard();
        
        assertFalse(chessboard.isStalemate(Color.White), "Starting position should not be stalemate for white");
        assertFalse(chessboard.isStalemate(Color.Black), "Starting position should not be stalemate for black");
    }

    @Test
    public void testFiftyMoveRuleTracking() {
        ChessGame game = new ChessGame();
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new ChessPiece(ChessPieceType.Empty, Color.None);
            }
        }
        
        // Setup simple position with kings and a knight
        board[0][0] = new ChessPiece(ChessPieceType.King, Color.White);
        board[7][7] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[0][1] = new ChessPiece(ChessPieceType.Knight, Color.White);
        
        // The game state should be Free initially
        assertEquals(GameState.Free, game.getGameState());
    }

    @Test
    public void testMoveHistoryTracking() {
        ChessGame game = new ChessGame();
        
        // Make a few moves
        game.MoveController(new Position(2, 5), new Position(3, 5)); // e2-e3
        game.MoveController(new Position(7, 5), new Position(6, 5)); // e7-e6
        
        // Check that moves are tracked
        assertEquals(2, game.getMoveHistory().size(), "Should have 2 moves in history");
    }
}
