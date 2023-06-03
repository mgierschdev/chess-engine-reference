package com.backend.domain;
import backend.models.*;
import com.backend.models.*;
import com.backend.util.Util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChessGame {

    public final UUID uid = UUID.randomUUID();

    public final GameState gameState;

    private final Chessboard chessboard;

    private final Set<ChessPiece> takenWhite;

    private final Set<ChessPiece> takenBlack;

    private Color turn;

    public ChessGame() {
        chessboard = new Chessboard();
        takenWhite = new HashSet<>();
        takenBlack = new HashSet<>();
        gameState = GameState.Free;
        turn = Color.White;
    }

    public boolean isMove(Position a, Position b) {
        return chessboard.isMove(a, b, turn);
    }

    public void Move(String chessNotation){
        if(chessNotation.length() < 5){
            return;
        }
        String[] movement = chessNotation.split("-");

        int[] sourceMatrix = Util.GetMatrixNotation(movement[0]);
        int[] targetMatrix = Util.GetMatrixNotation(movement[1]);

        Move(new Position(sourceMatrix[0], sourceMatrix[1]), new Position(targetMatrix[0], targetMatrix[1]));
    }

    public ChessPiece Move(Position a, Position b) {
        ChessPiece chessPiece = chessboard.movePiece(a, b, turn);

        if (chessPiece.type() == ChessPieceType.Invalid) {
            return chessPiece;
        }

        if (chessPiece.type() != ChessPieceType.Empty) {
            if (turn == Color.White) {
                takenWhite.add(chessPiece);
            } else {
                takenBlack.add(chessPiece);
            }
        }

        if (turn == Color.White) {
            turn = Color.Black;
        } else {
            turn = Color.White;
        }

        return chessPiece;
    }

    public void printBoard(){
        chessboard.printBoard();
    }

    public Color getTurn() {
        return turn;
    }
}
