package com.backend.util;

import com.backend.domain.ChessGame;
import com.backend.models.GameState;
import com.backend.models.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PGN export functionality.
 */
public class PGNExporterTest {

    @Test
    public void testBasicPGNExport() {
        ChessGame game = new ChessGame();
        
        // Make a few moves (opening moves)
        game.MoveController(new Position(2, 5), new Position(3, 5)); // e2-e3 (white)
        game.MoveController(new Position(7, 5), new Position(6, 5)); // e7-e6 (black)
        game.MoveController(new Position(2, 4), new Position(4, 4)); // d2-d4 (white)
        game.MoveController(new Position(7, 4), new Position(5, 4)); // d7-d5 (black)
        
        String pgn = game.exportToPGN();
        
        assertNotNull(pgn, "PGN should not be null");
        assertTrue(pgn.contains("[Event"), "PGN should contain Event header");
        assertTrue(pgn.contains("[Result"), "PGN should contain Result header");
        assertTrue(pgn.contains("1."), "PGN should contain move numbers");
        
        // Game should still be ongoing
        assertTrue(pgn.contains("*"), "PGN should show game ongoing with *");
    }

    @Test
    public void testResultStringForCheckmate() {
        String result = PGNExporter.getResultString(GameState.Checkmate, com.backend.models.Color.White);
        assertEquals("0-1", result, "White turn at checkmate means black won");
        
        result = PGNExporter.getResultString(GameState.Checkmate, com.backend.models.Color.Black);
        assertEquals("1-0", result, "Black turn at checkmate means white won");
    }

    @Test
    public void testResultStringForDraw() {
        String result = PGNExporter.getResultString(GameState.DrawByStalemate, com.backend.models.Color.White);
        assertEquals("1/2-1/2", result, "Stalemate should be a draw");
        
        result = PGNExporter.getResultString(GameState.DrawByRepetition, com.backend.models.Color.Black);
        assertEquals("1/2-1/2", result, "Threefold repetition should be a draw");
        
        result = PGNExporter.getResultString(GameState.DrawByFiftyMove, com.backend.models.Color.White);
        assertEquals("1/2-1/2", result, "Fifty-move rule should be a draw");
    }

    @Test
    public void testResultStringForOngoingGame() {
        String result = PGNExporter.getResultString(GameState.Free, com.backend.models.Color.White);
        assertEquals("*", result, "Ongoing game should show *");
        
        result = PGNExporter.getResultString(GameState.Check, com.backend.models.Color.Black);
        assertEquals("*", result, "Check state should still show game ongoing");
    }
}
