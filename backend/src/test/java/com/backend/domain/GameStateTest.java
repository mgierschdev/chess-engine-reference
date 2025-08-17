package com.backend.domain;

import com.backend.models.GameState;
import com.backend.models.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStateTest {

    @Test
    public void testCheckScenario() {
        ChessGame game = new ChessGame();

        // 1. f3 e5 2. e4 Qh4+
        game.MoveController(new Position(2, 6), new Position(3, 6)); // f2 -> f3
        game.MoveController(new Position(7, 5), new Position(5, 5)); // e7 -> e5
        game.MoveController(new Position(2, 5), new Position(4, 5)); // e2 -> e4
        game.MoveController(new Position(8, 4), new Position(4, 8)); // Qd8 -> h4

        assertEquals(GameState.Check, game.getGameState());
    }

    @Test
    public void testCheckmateScenario() {
        ChessGame game = new ChessGame();

        // 1. f3 e5 2. g4 Qh4#
        game.MoveController(new Position(2, 6), new Position(3, 6)); // f2 -> f3
        game.MoveController(new Position(7, 5), new Position(5, 5)); // e7 -> e5
        game.MoveController(new Position(2, 7), new Position(4, 7)); // g2 -> g4
        game.MoveController(new Position(8, 4), new Position(4, 8)); // Qd8 -> h4

        assertEquals(GameState.Checkmate, game.getGameState());
    }
}

