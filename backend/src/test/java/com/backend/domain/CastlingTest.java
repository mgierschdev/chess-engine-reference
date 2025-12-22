package com.backend.domain;

import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for castling moves including edge cases.
 */
public class CastlingTest {

    @Test
    public void testKingsideCastlingAllowed() {
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear pieces between king and rook for white
        board[0][5] = new ChessPiece(ChessPieceType.Empty, Color.None); // f1
        board[0][6] = new ChessPiece(ChessPieceType.Empty, Color.None); // g1
        
        // Get valid moves for white king
        Position[] validMoves = chessboard.getValidMoves(new Position(0, 4));
        
        // Check that kingside castling is in the valid moves list
        boolean hasKingsideCastling = false;
        for (Position move : validMoves) {
            if (move.row == 0 && move.col == 6) { // g1 is the castling destination
                hasKingsideCastling = true;
                break;
            }
        }
        
        assertTrue(hasKingsideCastling, "King should be able to castle kingside when path is clear");
    }

    @Test
    public void testQueensideCastlingAllowed() {
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear pieces between king and rook for white
        board[0][1] = new ChessPiece(ChessPieceType.Empty, Color.None); // b1
        board[0][2] = new ChessPiece(ChessPieceType.Empty, Color.None); // c1
        board[0][3] = new ChessPiece(ChessPieceType.Empty, Color.None); // d1
        
        // Get valid moves for white king
        Position[] validMoves = chessboard.getValidMoves(new Position(0, 4));
        
        // Check that queenside castling is in the valid moves list
        boolean hasQueensideCastling = false;
        for (Position move : validMoves) {
            if (move.row == 0 && move.col == 2) { // c1 is the castling destination
                hasQueensideCastling = true;
                break;
            }
        }
        
        assertTrue(hasQueensideCastling, "King should be able to castle queenside when path is clear");
    }

    @Test
    public void testCastlingExecutesCorrectly() {
        ChessGame game = new ChessGame();
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear pieces for kingside castling
        board[0][5] = new ChessPiece(ChessPieceType.Empty, Color.None); // f1
        board[0][6] = new ChessPiece(ChessPieceType.Empty, Color.None); // g1
        
        // Perform castling
        ChessPiece result = chessboard.movePiece(new Position(0, 4), new Position(0, 6), Color.White, ChessPieceType.Queen);
        
        // Verify the move was valid
        assertNotEquals(ChessPieceType.Invalid, result.type(), "Castling should be a valid move");
        
        // Verify king moved to g1
        assertEquals(ChessPieceType.King, board[0][6].type(), "King should be on g1");
        assertEquals(Color.White, board[0][6].color(), "King should be white");
        
        // Verify rook moved to f1
        assertEquals(ChessPieceType.Rock, board[0][5].type(), "Rook should be on f1");
        assertEquals(Color.White, board[0][5].color(), "Rook should be white");
        
        // Verify original squares are empty
        assertEquals(ChessPieceType.Empty, board[0][4].type(), "e1 should be empty");
        assertEquals(ChessPieceType.Empty, board[0][7].type(), "h1 should be empty");
    }

    @Test
    public void testCastlingBlockedByPieces() {
        Chessboard chessboard = new Chessboard();
        
        // In initial position, pieces block castling
        Position[] validMoves = chessboard.getValidMoves(new Position(0, 4));
        
        // Check that no castling moves are available
        boolean hasCastling = false;
        for (Position move : validMoves) {
            if (move.row == 0 && (move.col == 2 || move.col == 6)) {
                hasCastling = true;
                break;
            }
        }
        
        assertFalse(hasCastling, "King should not be able to castle when pieces block the path");
    }

    @Test
    public void testCastlingNotAllowedWhenInCheck() {
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear pieces for kingside castling
        board[0][5] = new ChessPiece(ChessPieceType.Empty, Color.None); // f1
        board[0][6] = new ChessPiece(ChessPieceType.Empty, Color.None); // g1
        
        // Clear pawn and place black rook to attack white king
        board[1][4] = new ChessPiece(ChessPieceType.Empty, Color.None); // Remove e2 pawn
        board[3][4] = new ChessPiece(ChessPieceType.Rock, Color.Black); // Place rook on e5
        
        // Get valid moves for white king
        Position[] validMoves = chessboard.getValidMoves(new Position(0, 4));
        
        // Check that castling is NOT in the valid moves list
        boolean hasCastling = false;
        for (Position move : validMoves) {
            if (move.row == 0 && move.col == 6) {
                hasCastling = true;
                break;
            }
        }
        
        assertFalse(hasCastling, "King should not be able to castle when in check");
    }

    @Test
    public void testBlackKingsideCastling() {
        Chessboard chessboard = new Chessboard();
        ChessPiece[][] board = chessboard.getBoard();
        
        // Clear pieces between king and rook for black
        board[7][5] = new ChessPiece(ChessPieceType.Empty, Color.None); // f8
        board[7][6] = new ChessPiece(ChessPieceType.Empty, Color.None); // g8
        
        // Get valid moves for black king
        Position[] validMoves = chessboard.getValidMoves(new Position(7, 4));
        
        // Check that kingside castling is in the valid moves list
        boolean hasKingsideCastling = false;
        for (Position move : validMoves) {
            if (move.row == 7 && move.col == 6) { // g8 is the castling destination
                hasKingsideCastling = true;
                break;
            }
        }
        
        assertTrue(hasKingsideCastling, "Black king should be able to castle kingside when path is clear");
    }
}
