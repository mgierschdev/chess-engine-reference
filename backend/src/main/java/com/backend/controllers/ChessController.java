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

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ChessController {
    private final AtomicLong counter = new AtomicLong();
    private ChessGame chessGame;

    @GetMapping("/startGame")
    public MessageResponseRequest startGame() {
        if(chessGame != null){
            return new MessageResponseRequest(counter.incrementAndGet(), String.format(Log.ChessGame.gameAlreadyStarted, chessGame.uid));
        }
        chessGame = new ChessGame();
        return new MessageResponseRequest(counter.incrementAndGet(), String.format(Log.ChessGame.gameStarted, chessGame.uid));
    }

    @GetMapping("/endGame")
    public MessageResponseRequest endGame() {
        if (chessGame == null) {
            return new MessageResponseRequest(counter.incrementAndGet(), Log.ChessGame.endIsOver);
        }
        chessGame = null;
        return new MessageResponseRequest(counter.incrementAndGet(), Log.ChessGame.endGame);
    }

    @GetMapping("/move")
    public MoveResponseRequest moveChessNotation(@RequestParam(value = "position", defaultValue = "") String position) {
        if (chessGame == null || position.isEmpty()) {
            return new MoveResponseRequest(new ChessPiece(ChessPieceType.Invalid, Color.None), Log.ChessGame.endIsOver);
        }
        ChessPiece result = chessGame.Move(position);
        return new MoveResponseRequest(result, Log.ChessGame.pieceMoved);
    }

    @GetMapping("/getBoard")
    public ChessboardResponseRequest getBoard() {
        if (chessGame == null) {
            return null;
        }
        return new ChessboardResponseRequest(chessGame.getChessboard());
    }

    @GetMapping("/getTurn")
    public TurnResponseRequest getTurn() {
        if (chessGame == null) {
            return null;
        }
        return new TurnResponseRequest(chessGame.getTurn());
    }

    @GetMapping("/getCaptured")
    public CapturedResponseRequest getCaptured(@RequestParam(value = "color", defaultValue = "") String color) {
        if (chessGame == null || color.isEmpty()) {
            return new CapturedResponseRequest(new HashSet<>());
        }

        return new CapturedResponseRequest(chessGame.getCaptured(color));
    }

    @GetMapping("/*")
    public MessageResponseRequest defaultAll() {
        return new MessageResponseRequest(counter.incrementAndGet(), Log.empty);
    }
}
