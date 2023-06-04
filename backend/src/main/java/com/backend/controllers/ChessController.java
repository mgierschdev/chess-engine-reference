package com.backend.controllers;

import com.backend.domain.ChessGame;
import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.requests.*;
import com.backend.util.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

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
           chessGameResponse.content= String.format(Log.ChessGame.gameAlreadyStarted, chessGame.uid);
           return chessGameResponse;
        }
        chessGame = new ChessGame();
        SetChessResponse();
        chessGameResponse.content= String.format(Log.ChessGame.gameStarted, chessGame.uid);
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
            chessGameResponse.content = String.format(Log.ChessGame.gameAlreadyStarted, chessGame.uid);
        }else {
            chessGameResponse.content = String.format(Log.ChessGame.endGame, "None");
        }

        return chessGameResponse;
    }

    @GetMapping("/move")
    public MoveResponse moveChessNotation(@RequestParam(value = "position", defaultValue = "") String position) {
        if (chessGame == null || position.isEmpty()) {
            return new MoveResponse(new ChessPiece(ChessPieceType.Invalid, Color.None), Log.ChessGame.endIsOver);
        }
        ChessPiece result = chessGame.Move(position);
        return new MoveResponse(result, Log.ChessGame.pieceMoved);
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
            chessGameResponse.turn = Color.White;
            return;
        }
        chessGameResponse.id = requestCount.incrementAndGet();
        chessGameResponse.turn = chessGame.getTurn();
        chessGameResponse.chessboard = chessGame.getChessboard();
        chessGameResponse.capturedBlack = chessGame.getCaptured(Color.Black);
        chessGameResponse.capturedWhite = chessGame.getCaptured(Color.White);
        chessGameResponse.gameStarted = true;
    }
}
