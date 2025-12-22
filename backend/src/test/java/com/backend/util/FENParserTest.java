package com.backend.util;

import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;
import com.backend.util.FENParser.FENParseResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FENParserTest {
    
    @Test
    void testParseStartingPosition() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParseResult result = FENParser.parseFEN(fen);
        
        // Check active color
        assertEquals(Color.White, result.activeColor);
        
        // Check castling rights - all should be available (pieces haven't moved)
        assertFalse(result.whiteKingMoved);
        assertFalse(result.blackKingMoved);
        assertFalse(result.whiteKingsideRookMoved);
        assertFalse(result.whiteQueensideRookMoved);
        assertFalse(result.blackKingsideRookMoved);
        assertFalse(result.blackQueensideRookMoved);
        
        // Check en passant
        assertNull(result.enPassantTarget);
        
        // Check clocks
        assertEquals(0, result.halfMoveClock);
        assertEquals(1, result.fullMoveNumber);
        
        // Check some piece positions
        assertEquals(ChessPieceType.Rock, result.board[0][0].type());
        assertEquals(Color.White, result.board[0][0].color());
        assertEquals(ChessPieceType.King, result.board[0][4].type());
        assertEquals(ChessPieceType.Pawn, result.board[1][0].type());
        assertEquals(ChessPieceType.Empty, result.board[4][4].type());
        assertEquals(ChessPieceType.Rock, result.board[7][0].type());
        assertEquals(Color.Black, result.board[7][0].color());
    }
    
    @Test
    void testParsePositionAfterE4() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        FENParseResult result = FENParser.parseFEN(fen);
        
        // Check active color
        assertEquals(Color.Black, result.activeColor);
        
        // Check en passant target
        assertNotNull(result.enPassantTarget);
        assertEquals(2, result.enPassantTarget.row); // e3 = row 2
        assertEquals(4, result.enPassantTarget.col); // e file = col 4
        
        // Check pawn position
        assertEquals(ChessPieceType.Pawn, result.board[3][4].type());
        assertEquals(Color.White, result.board[3][4].color());
        assertEquals(ChessPieceType.Empty, result.board[1][4].type());
    }
    
    @Test
    void testParsePositionWithNoCastlingRights() {
        String fen = "r3k2r/8/8/8/8/8/8/R3K2R w - - 0 1";
        FENParseResult result = FENParser.parseFEN(fen);
        
        // All castling rights should be lost
        assertTrue(result.whiteKingMoved);
        assertTrue(result.blackKingMoved);
        assertTrue(result.whiteKingsideRookMoved);
        assertTrue(result.whiteQueensideRookMoved);
        assertTrue(result.blackKingsideRookMoved);
        assertTrue(result.blackQueensideRookMoved);
    }
    
    @Test
    void testParsePositionWithPartialCastlingRights() {
        String fen = "r3k2r/8/8/8/8/8/8/R3K2R w Kq - 0 1";
        FENParseResult result = FENParser.parseFEN(fen);
        
        // White can castle kingside, black can castle queenside
        assertFalse(result.whiteKingMoved);
        assertFalse(result.blackKingMoved);
        assertFalse(result.whiteKingsideRookMoved);
        assertTrue(result.whiteQueensideRookMoved);
        assertTrue(result.blackKingsideRookMoved);
        assertFalse(result.blackQueensideRookMoved);
    }
    
    @Test
    void testParseMinimalFEN() {
        // Only piece placement
        String fen = "8/8/8/4k3/4K3/8/8/8";
        FENParseResult result = FENParser.parseFEN(fen);
        
        // Defaults should apply
        assertEquals(Color.White, result.activeColor);
        assertNull(result.enPassantTarget);
        assertEquals(0, result.halfMoveClock);
        assertEquals(1, result.fullMoveNumber);
        
        // Check king positions
        // FEN ranks: 8=row7, 7=row6, 6=row5, 5=row4 (black k), 4=row3 (white K), 3=row2, 2=row1, 1=row0
        assertEquals(ChessPieceType.King, result.board[3][4].type());
        assertEquals(Color.White, result.board[3][4].color());
        assertEquals(ChessPieceType.King, result.board[4][4].type());
        assertEquals(Color.Black, result.board[4][4].color());
    }
    
    @Test
    void testParseInvalidFENThrowsException() {
        // Empty string
        assertThrows(IllegalArgumentException.class, () -> FENParser.parseFEN(""));
        
        // Null
        assertThrows(IllegalArgumentException.class, () -> FENParser.parseFEN(null));
        
        // Wrong number of ranks
        assertThrows(IllegalArgumentException.class, () -> 
            FENParser.parseFEN("rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        
        // Invalid piece character
        assertThrows(IllegalArgumentException.class, () -> 
            FENParser.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBXKBNR w KQkq - 0 1"));
        
        // Invalid active color
        assertThrows(IllegalArgumentException.class, () -> 
            FENParser.parseFEN("8/8/8/8/8/8/8/8 x - - 0 1"));
        
        // Invalid en passant square
        assertThrows(IllegalArgumentException.class, () -> 
            FENParser.parseFEN("8/8/8/8/8/8/8/8 w - z9 0 1"));
    }
    
    @Test
    void testGenerateFENStartingPosition() {
        // Parse the starting position
        String originalFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        FENParseResult result = FENParser.parseFEN(originalFEN);
        
        // Generate FEN from parsed result
        String generatedFEN = FENParser.generateFEN(
            result.board, result.activeColor,
            result.whiteKingMoved, result.blackKingMoved,
            result.whiteKingsideRookMoved, result.whiteQueensideRookMoved,
            result.blackKingsideRookMoved, result.blackQueensideRookMoved,
            result.enPassantTarget, result.halfMoveClock, result.fullMoveNumber
        );
        
        // Should match original
        assertEquals(originalFEN, generatedFEN);
    }
    
    @Test
    void testGenerateFENWithEnPassant() {
        String originalFEN = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        FENParseResult result = FENParser.parseFEN(originalFEN);
        
        String generatedFEN = FENParser.generateFEN(
            result.board, result.activeColor,
            result.whiteKingMoved, result.blackKingMoved,
            result.whiteKingsideRookMoved, result.whiteQueensideRookMoved,
            result.blackKingsideRookMoved, result.blackQueensideRookMoved,
            result.enPassantTarget, result.halfMoveClock, result.fullMoveNumber
        );
        
        assertEquals(originalFEN, generatedFEN);
    }
    
    @Test
    void testRoundTripFEN() {
        String[] testFENs = {
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1",
            "8/8/8/4k3/4K3/8/8/8 w - - 0 1",
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"
        };
        
        for (String fen : testFENs) {
            FENParseResult result = FENParser.parseFEN(fen);
            String regenerated = FENParser.generateFEN(
                result.board, result.activeColor,
                result.whiteKingMoved, result.blackKingMoved,
                result.whiteKingsideRookMoved, result.whiteQueensideRookMoved,
                result.blackKingsideRookMoved, result.blackQueensideRookMoved,
                result.enPassantTarget, result.halfMoveClock, result.fullMoveNumber
            );
            assertEquals(fen, regenerated, "Round-trip failed for FEN: " + fen);
        }
    }
}
