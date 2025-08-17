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
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:3000"})
@RestController
public class ChessController {
    private final AtomicLong requestCount = new AtomicLong();
    private ChessGame chessGame;

    private ChessGameResponse chessGameResponse;

    public ChessController(){
        chessGameResponse = new ChessGameResponse();
    }

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

    @PostMapping(path ="/move")
    public PositionResponse moveChessNotation(@RequestBody ChessboardMoveRequest request) {
        if (chessGame == null || request == null || request.source == null || request.target == null) {
            return new PositionResponse(new ChessPiece(ChessPieceType.Invalid, Color.None), Log.ChessGame.endIsOver);
        }

        ChessPieceType promotion = request.promotionType == null ? ChessPieceType.Queen : request.promotionType;
        ChessPiece result = chessGame.MoveController(request.source, request.target, promotion);
        return new PositionResponse(result, Log.ChessGame.pieceMoved);
    }

    @PostMapping("/getValidMoves")
    public Position[] getValidMoves(@RequestBody Position position) {
        if (chessGame == null || position == null) {
            return new Position[0];
        }
        return chessGame.getValidMovesController(position);
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
