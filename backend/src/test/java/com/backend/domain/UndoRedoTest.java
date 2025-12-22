package com.backend.domain;

import com.backend.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UndoRedoTest {
    private ChessGame game;

    @BeforeEach
    void setUp() {
        game = new ChessGame();
    }

    @Test
    void testUndoSingleMove() {
        // Make a move (e2 to e4)
        Position source = new Position(2, 5); // e2 in 1-indexed becomes row 1, col 4 in 0-indexed
        Position target = new Position(4, 5); // e4
        
        ChessPiece result = game.MoveController(source, target);
        assertNotEquals(ChessPieceType.Invalid, result.type());
        
        // Verify the move was made
        assertEquals(Color.Black, game.getTurn());
        
        // Undo the move
        assertTrue(game.undo());
        
        // Verify state is restored
        assertEquals(Color.White, game.getTurn());
        assertEquals(GameState.Free, game.getGameState());
    }

    @Test
    void testUndoMultipleMoves() {
        // Make several moves
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        game.MoveController(new Position(7, 5), new Position(5, 5)); // e5
        game.MoveController(new Position(1, 7), new Position(3, 6)); // Nf3 - corrected
        
        assertEquals(Color.Black, game.getTurn());
        
        // Undo last move
        assertTrue(game.undo());
        assertEquals(Color.White, game.getTurn());
        
        // Undo second move
        assertTrue(game.undo());
        assertEquals(Color.Black, game.getTurn());
        
        // Undo first move
        assertTrue(game.undo());
        assertEquals(Color.White, game.getTurn());
    }

    @Test
    void testCannotUndoBeyondStart() {
        // Try to undo when no moves have been made
        assertFalse(game.undo());
        
        // Make one move
        game.MoveController(new Position(2, 5), new Position(4, 5));
        
        // Undo should work once
        assertTrue(game.undo());
        
        // Second undo should fail
        assertFalse(game.undo());
    }

    @Test
    void testRedoAfterUndo() {
        // Make a move
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        assertEquals(Color.Black, game.getTurn());
        
        // Undo
        assertTrue(game.undo());
        assertEquals(Color.White, game.getTurn());
        
        // Redo
        assertTrue(game.redo());
        assertEquals(Color.Black, game.getTurn());
    }

    @Test
    void testRedoMultipleMoves() {
        // Make several moves
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        game.MoveController(new Position(7, 5), new Position(5, 5)); // e5
        game.MoveController(new Position(1, 7), new Position(3, 6)); // Nf3 - corrected from (1,6) to (1,7)
        
        // Undo all moves
        game.undo();
        game.undo();
        game.undo();
        assertEquals(Color.White, game.getTurn());
        
        // Redo all moves
        assertTrue(game.redo());
        assertEquals(Color.Black, game.getTurn());
        
        assertTrue(game.redo());
        assertEquals(Color.White, game.getTurn());
        
        assertTrue(game.redo());
        assertEquals(Color.Black, game.getTurn());
    }

    @Test
    void testCannotRedoWithoutUndo() {
        // Try to redo when nothing has been undone
        assertFalse(game.redo());
        
        // Make a move
        game.MoveController(new Position(2, 5), new Position(4, 5));
        
        // Still can't redo
        assertFalse(game.redo());
    }

    @Test
    void testRedoHistoryClearedOnNewMove() {
        // Make moves
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        game.MoveController(new Position(7, 5), new Position(5, 5)); // e5
        
        // Undo one move
        game.undo();
        
        // Verify we can redo
        assertTrue(game.canRedo());
        
        // Make a different move
        game.MoveController(new Position(7, 4), new Position(5, 4)); // d5
        
        // Redo history should be cleared
        assertFalse(game.canRedo());
    }

    @Test
    void testCanUndoAndCanRedo() {
        // Initial state
        assertFalse(game.canUndo());
        assertFalse(game.canRedo());
        
        // After a move
        game.MoveController(new Position(2, 5), new Position(4, 5));
        assertTrue(game.canUndo());
        assertFalse(game.canRedo());
        
        // After undo
        game.undo();
        assertFalse(game.canUndo());
        assertTrue(game.canRedo());
        
        // After redo
        game.redo();
        assertTrue(game.canUndo());
        assertFalse(game.canRedo());
    }

    @Test
    void testUndoRestoresCapturedPieces() {
        // Set up a capture scenario
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        game.MoveController(new Position(7, 4), new Position(5, 4)); // d5
        game.MoveController(new Position(4, 5), new Position(5, 4)); // exd5 (capture)
        
        // Verify white has captured a pawn
        assertEquals(1, game.getCaptured(Color.White).size());
        
        // Undo the capture
        game.undo();
        
        // Captured pieces should be restored
        assertEquals(0, game.getCaptured(Color.White).size());
    }

    @Test
    void testUndoSpecialMoves() {
        // Test undo with castling
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        game.MoveController(new Position(7, 5), new Position(5, 5)); // e5
        game.MoveController(new Position(1, 7), new Position(3, 6)); // Nf3 - corrected
        game.MoveController(new Position(8, 7), new Position(6, 6)); // Nf6 - corrected from (8,6)
        game.MoveController(new Position(1, 6), new Position(4, 3)); // Bc4 - corrected
        game.MoveController(new Position(8, 6), new Position(5, 3)); // Bc5 - corrected
        
        // Try to castle (if conditions are met)
        Color turnBeforeCastle = game.getTurn();
        ChessPiece castleResult = game.MoveController(new Position(1, 5), new Position(1, 7)); // O-O - corrected
        
        if (castleResult.type() != ChessPieceType.Invalid) {
            // Castling succeeded, test undo
            assertTrue(game.undo());
            assertEquals(turnBeforeCastle, game.getTurn());
        }
    }

    @Test
    void testUndoInvalidMoveDoesNotAffectHistory() {
        // Make a valid move
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e4
        
        // Try an invalid move
        ChessPiece result = game.MoveController(new Position(2, 5), new Position(5, 5)); // Invalid: piece not there
        assertEquals(ChessPieceType.Invalid, result.type());
        
        // Undo should only undo the valid move
        assertTrue(game.undo());
        assertFalse(game.undo()); // Can't undo before start
    }
}
