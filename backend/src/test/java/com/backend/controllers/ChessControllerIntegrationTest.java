package com.backend.controllers;

import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.GameState;
import com.backend.models.Position;
import com.backend.models.requests.ChessGameResponse;
import com.backend.models.requests.ChessboardMoveRequest;
import com.backend.models.requests.PositionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Chess API.
 * Tests the full flow: start game → get valid moves → make move → verify state
 */
@SpringBootTest
public class ChessControllerIntegrationTest {

    @Autowired
    private ChessController chessController;

    @BeforeEach
    public void setUp() {
        // End any existing game before each test
        chessController.endGame();
    }

    @Test
    public void testCompleteGameFlow() {
        // 1. Start a new game
        ChessGameResponse startResponse = chessController.startGame();
        assertTrue(startResponse.gameStarted, "Game should be started");
        assertEquals(Color.White, startResponse.turn, "White should move first");
        assertEquals(GameState.Free, startResponse.gameState, "Game should be in Free state");
        assertNotNull(startResponse.chessboard, "Chessboard should not be null");

        // 2. Get valid moves for a white pawn (e2)
        Position pawnPosition = new Position(2, 5); // e2 in 1-indexed coordinates
        Position[] validMoves = chessController.getValidMoves(pawnPosition);
        assertTrue(validMoves.length >= 2, "Pawn should have at least 2 valid moves (1 or 2 squares forward)");

        // 3. Make a move (e2 to e4)
        ChessboardMoveRequest moveRequest = new ChessboardMoveRequest();
        moveRequest.source = new Position(2, 5);
        moveRequest.target = new Position(4, 5);
        moveRequest.promotionType = ChessPieceType.Queen;

        PositionResponse moveResponse = chessController.moveChessNotation(moveRequest);
        assertEquals(ChessPieceType.Empty, moveResponse.chessPiece().type(), "Move should be successful (empty target)");

        // 4. Get updated game state
        ChessGameResponse gameState = chessController.gameStarted();
        assertEquals(Color.Black, gameState.turn, "Turn should switch to Black after White's move");
        assertEquals(GameState.Free, gameState.gameState, "Game should still be in Free state");

        // 5. Make a black move (e7 to e5)
        ChessboardMoveRequest blackMoveRequest = new ChessboardMoveRequest();
        blackMoveRequest.source = new Position(7, 5);
        blackMoveRequest.target = new Position(5, 5);
        blackMoveRequest.promotionType = ChessPieceType.Queen;

        PositionResponse blackMoveResponse = chessController.moveChessNotation(blackMoveRequest);
        assertEquals(ChessPieceType.Empty, blackMoveResponse.chessPiece().type(), "Black move should be successful");

        // 6. Verify turn switched back to White
        ChessGameResponse updatedGameState = chessController.gameStarted();
        assertEquals(Color.White, updatedGameState.turn, "Turn should switch back to White");
    }

    @Test
    public void testInvalidMoveReturnsError() {
        // Start a new game
        chessController.startGame();

        // Try to move a piece that can't move there (e.g., pawn moving backward)
        ChessboardMoveRequest invalidMove = new ChessboardMoveRequest();
        invalidMove.source = new Position(2, 1); // White pawn at b2
        invalidMove.target = new Position(1, 1); // Try to move backward to b1
        invalidMove.promotionType = ChessPieceType.Queen;

        PositionResponse response = chessController.moveChessNotation(invalidMove);
        assertEquals(ChessPieceType.Invalid, response.chessPiece().type(), "Invalid move should return Invalid piece");
    }

    @Test
    public void testGetValidMovesForEmptySquare() {
        // Start a new game
        chessController.startGame();

        // Try to get valid moves for an empty square
        Position emptySquare = new Position(4, 4); // e4 is empty at start
        Position[] validMoves = chessController.getValidMoves(emptySquare);

        assertEquals(0, validMoves.length, "Empty square should have no valid moves");
    }

    @Test
    public void testEndGameClearsState() {
        // Start a new game
        ChessGameResponse startResponse = chessController.startGame();
        assertTrue(startResponse.gameStarted, "Game should be started");

        // End the game
        ChessGameResponse endResponse = chessController.endGame();
        assertFalse(endResponse.gameStarted, "Game should not be started after ending");

        // Try to make a move after ending
        ChessboardMoveRequest moveRequest = new ChessboardMoveRequest();
        moveRequest.source = new Position(2, 5);
        moveRequest.target = new Position(4, 5);
        moveRequest.promotionType = ChessPieceType.Queen;

        PositionResponse moveResponse = chessController.moveChessNotation(moveRequest);
        assertEquals(ChessPieceType.Invalid, moveResponse.chessPiece().type(), "Move after game end should be invalid");
    }

    @Test
    public void testCannotStartGameTwice() {
        // Start a new game
        ChessGameResponse firstStart = chessController.startGame();
        assertTrue(firstStart.gameStarted, "First game start should succeed");

        // Try to start again without ending
        ChessGameResponse secondStart = chessController.startGame();
        assertTrue(secondStart.gameStarted, "Game should still be started");
        assertTrue(secondStart.content.contains("already"), "Response should indicate game already started");
    }
}
