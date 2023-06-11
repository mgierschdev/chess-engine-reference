package com.backend.domain;

import com.backend.models.*;
import com.backend.models.requests.ChessPieceResponse;
import com.backend.util.Util;

import java.util.HashSet;
import java.util.Set;

public class ChessGame {
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

    public ChessPiece MoveController(String chessNotation) {
        if (chessNotation.length() < 5) {
            return new ChessPiece(ChessPieceType.Invalid, Color.None);
        }

        String[] movement = chessNotation.split("-");
        int[] sourceMatrix = Util.GetMatrixNotation(movement[0]);
        int[] targetMatrix = Util.GetMatrixNotation(movement[1]);

        return MoveController(new Position(sourceMatrix[0], sourceMatrix[1]), new Position(targetMatrix[0], targetMatrix[1]));
    }

    // Returns empty if the move was correct, invalid otherwise, the taken piece if there was a taken piece
    public ChessPiece MoveController(Position a, Position b) {
        removeOffsetChessboardPosition(a);
        removeOffsetChessboardPosition(b);

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

    public Position[] getValidMovesController(Position position) {
        removeOffsetChessboardPosition(position);
        Position[] validPositions = chessboard.getValidMoves(new Position(position.row, position.col));

        // we add offset
        for (Position pos: validPositions) {
            addOffsetChessboardPosition(pos);
        }

        return validPositions;
    }

    public void printBoard() {
        chessboard.printBoard();
    }

    public Color getTurn() {
        return turn;
    }

    public Set<ChessPiece> getCaptured(Color color) {
        if (color == Color.White) {
            return takenWhite;
        } else {
            return takenBlack;
        }
    }

    public ChessPieceResponse[] getChessboard() {
        return Chessboard.GetArrayBoard(chessboard.getBoard());
    }

    // the UI chessboard is represented starting from the position [1,1], and backend [0,0]
    public void removeOffsetChessboardPosition(Position a){
        a.row -= 1;
        a.col -= 1;
    }

    public void addOffsetChessboardPosition(Position a){
        a.row += 1;
        a.col += 1;
    }
}
