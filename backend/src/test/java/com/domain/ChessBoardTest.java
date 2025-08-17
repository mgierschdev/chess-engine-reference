package com.domain;

import com.backend.domain.Chessboard;
import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChessBoardTest {

    @Test
    public void TestTwoMoves() {
        Chessboard chessboard = new Chessboard();

        Position source = new Position(1, 0);
        Position target = new Position(2, 0);

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assertions.assertEquals(ChessPieceType.Empty, resultA.type());
        Assertions.assertEquals(ChessPieceType.Pawn, resultB.type());
        Assertions.assertEquals(ChessPieceType.Empty, result.type());

        // Black
        source = new Position(6, 0);
        target = new Position(5, 0);
        result = chessboard.movePiece(source, target, Color.Black);
        resultA = chessboard.getBoardPosition(source.row, source.col);
        resultB = chessboard.getBoardPosition(target.row, target.col);

        Assertions.assertEquals(ChessPieceType.Empty, resultA.type());
        Assertions.assertEquals(ChessPieceType.Pawn, resultB.type());
        Assertions.assertEquals(ChessPieceType.Empty, result.type());
    }

    @Test
    public void TestOccupiedSameColor() {
        Chessboard chessboard = new Chessboard();

        Position source = new Position(0, 0);
        Position target = new Position(1, 0);

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assertions.assertEquals(ChessPieceType.Rock, resultA.type());
        Assertions.assertEquals(ChessPieceType.Pawn, resultB.type());
        Assertions.assertEquals(ChessPieceType.Invalid, result.type());
    }

    @Test
    public void TestEmptySource() {
        Chessboard chessboard = new Chessboard();

        Position source = new Position(2, 0);
        Position target = new Position(1, 0);

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assertions.assertEquals(ChessPieceType.Empty, resultA.type());
        Assertions.assertEquals(ChessPieceType.Pawn, resultB.type());
        Assertions.assertEquals(ChessPieceType.Invalid, result.type());
    }

    @Test
    public void TestEmptySourceEmptyTarget() {
        Chessboard chessboard = new Chessboard();

        Position source = new Position(2, 0);
        Position target = new Position(3, 0);

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assertions.assertEquals(ChessPieceType.Empty, resultA.type());
        Assertions.assertEquals(ChessPieceType.Empty, resultB.type());
        Assertions.assertEquals(ChessPieceType.Invalid, result.type());
    }

    @Test
    public void TestRookValidMovesAfterClearingPath() {
        Chessboard chessboard = new Chessboard();

        // Move pawn from column a to clear rook's vertical path
        chessboard.movePiece(new Position(1, 0), new Position(3, 0), Color.White);

        Position[] validMoves = chessboard.getValidMoves(new Position(0, 0));

        Assertions.assertEquals(2, validMoves.length);
        Assertions.assertTrue(Arrays.stream(validMoves)
                .anyMatch(p -> p.row == 1 && p.col == 0));
        Assertions.assertTrue(Arrays.stream(validMoves)
                .anyMatch(p -> p.row == 2 && p.col == 0));
    }

    // TODO: check other methods, set white/black pieces
    // add pieces movement rules
    // display board on the ui

}
