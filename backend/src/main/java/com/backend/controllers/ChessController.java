package com.backend.controllers;

import com.backend.domain.ChessGame;
import com.backend.domain.Chessboard;
import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;
import com.backend.models.GameState;
import com.backend.models.requests.*;
import com.backend.util.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Problem: Need to expose chess game functionality via HTTP REST API for a web frontend.
 * 
 * Goal: Provide endpoints for starting/ending games, making moves, querying valid moves,
 * and retrieving game state. Maintain single in-memory game instance per server.
 * 
 * Approach: Spring Boot REST controller with GET/POST endpoints. Singleton game instance
 * stored as instance variable (lost on server restart). CORS configured globally via
 * CorsConfiguration. Delegates all game logic to ChessGame domain class.
 * 
 * Time: O(1) for game state queries, O(n) for moves (delegated to ChessGame)
 * Space: O(1) per game (one game instance stored)
 * 
 * Tags: rest-api, spring-boot, game-controller
 */
@Tag(name = "Chess Game", description = "Chess game management and move operations")
@RestController
public class ChessController {
    private final AtomicLong requestCount = new AtomicLong();
    private ChessGame chessGame;

    private ChessGameResponse chessGameResponse;

    public ChessController(){
        chessGameResponse = new ChessGameResponse();
    }

    @Operation(
        summary = "Start a new chess game",
        description = "Initializes a new chess game with pieces in starting positions. Returns error if game already started."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game started successfully or already started",
                     content = @Content(schema = @Schema(implementation = ChessGameResponse.class)))
    })
    @GetMapping("/startGame")
    public ChessGameResponse startGame() {
        if(chessGame != null){
           SetChessResponse();
           chessGameResponse.content= Log.ChessGame.gameAlreadyStarted;
           return chessGameResponse;
        }
        chessGame = new ChessGame();
        SetChessResponse();
        chessGameResponse.content= Log.ChessGame.gameStarted;
        return chessGameResponse;
    }

    @Operation(
        summary = "End the current chess game",
        description = "Ends the current game and clears game state from memory."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game ended successfully",
                     content = @Content(schema = @Schema(implementation = ChessGameResponse.class)))
    })
    @GetMapping("/endGame")
    public ChessGameResponse endGame() {

        if (chessGame == null) {
            chessGameResponse.content = Log.ChessGame.endIsOver;
        }else{
            chessGame = null;
            chessGameResponse.content = Log.ChessGame.endGame;
        }
        SetChessResponse();
        return chessGameResponse;
    }

    @Operation(
        summary = "Get current game state",
        description = "Returns the current state of the chess game including board positions, turn, captured pieces, and game status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current game state retrieved",
                     content = @Content(schema = @Schema(implementation = ChessGameResponse.class)))
    })
    @GetMapping("/chessGame")
    public ChessGameResponse gameStarted() {
        SetChessResponse();

        if(chessGame != null){
            chessGameResponse.content = Log.ChessGame.gameAlreadyStarted;
        }else {
            chessGameResponse.content = Log.ChessGame.endGame;
        }

        return chessGameResponse;
    }

    @Operation(
        summary = "Make a chess move",
        description = "Attempts to move a chess piece from source to target position. Validates move legality and updates game state. Supports pawn promotion."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Move attempted (check response for success/failure)",
                     content = @Content(schema = @Schema(implementation = PositionResponse.class)))
    })
    @PostMapping(path ="/move")
    public PositionResponse moveChessNotation(@RequestBody ChessboardMoveRequest request) {
        if (chessGame == null || request == null || request.source == null || request.target == null) {
            return new PositionResponse(new ChessPiece(ChessPieceType.Invalid, Color.None), Log.ChessGame.endIsOver);
        }

        ChessPieceType promotion = request.promotionType == null ? ChessPieceType.Queen : request.promotionType;
        ChessPiece result = chessGame.MoveController(request.source, request.target, promotion);
        return new PositionResponse(result, Log.ChessGame.pieceMoved);
    }

    @Operation(
        summary = "Get valid moves for a piece",
        description = "Returns all legal moves for the piece at the specified position, considering current board state and game rules."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Valid moves retrieved",
                     content = @Content(schema = @Schema(implementation = Position[].class)))
    })
    @PostMapping("/getValidMoves")
    public Position[] getValidMoves(@RequestBody Position position) {
        if (chessGame == null || position == null) {
            return new Position[0];
        }
        return chessGame.getValidMovesController(position);
    }

    @Operation(
        summary = "Get move history",
        description = "Returns the list of all moves made in the current game."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Move history retrieved",
                     content = @Content(schema = @Schema(implementation = com.backend.models.Move[].class)))
    })
    @GetMapping("/moveHistory")
    public java.util.List<com.backend.models.Move> getMoveHistory() {
        if (chessGame == null) {
            return new java.util.ArrayList<>();
        }
        return chessGame.getMoveHistory();
    }

    @Operation(
        summary = "Export game to PGN",
        description = "Exports the current game in Portable Game Notation (PGN) format."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PGN export successful",
                     content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/exportPGN")
    public String exportPGN() {
        if (chessGame == null) {
            return "[Event \"No game in progress\"]\n*";
        }
        return chessGame.exportToPGN();
    }

    @GetMapping("/*")
    public MessageResponse defaultAll() {
        return new MessageResponse(requestCount.incrementAndGet(), Log.empty);
    }

    private void SetChessResponse(){
        chessGameResponse.content = "";

        if(chessGame == null){
            chessGameResponse = new ChessGameResponse();
            chessGameResponse.gameStarted = false;
            chessGameResponse.turn = Color.None;
            chessGameResponse.chessboard = Chessboard.GetArrayBoard(Chessboard.GetInitMatrixBoard());
            chessGameResponse.gameState = GameState.Free;
            return;
        }
        chessGameResponse.id = requestCount.incrementAndGet();
        chessGameResponse.turn = chessGame.getTurn();
        chessGameResponse.gameState = chessGame.getGameState();
        chessGameResponse.chessboard = chessGame.getChessboard();
        chessGameResponse.capturedBlack = chessGame.getCaptured(Color.Black);
        chessGameResponse.capturedWhite = chessGame.getCaptured(Color.White);
        chessGameResponse.gameStarted = true;
    }
}
