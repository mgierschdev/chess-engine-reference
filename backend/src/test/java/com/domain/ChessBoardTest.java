package com.domain;

import com.backend.domain.Chessboard;
import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;
import org.testng.Assert;
import org.junit.jupiter.api.Test;

public class ChessBoardTest {

    @Test
    public void TestTwoMoves() {
        Chessboard chessboard = new Chessboard();

        System.out.println(chessboard.getBoardPosition(1 , 0));

        Position source = new Position(1, 0);
        Position target = new Position(2, 0);

        chessboard.printBoard();

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assert.assertEquals(resultA.type(), ChessPieceType.Empty);
        Assert.assertEquals(resultB.type(), ChessPieceType.Pawn);
        Assert.assertEquals(result.type(), ChessPieceType.Empty);

        // Black
        source = new Position(6, 0);
        target = new Position(5, 0);
        result = chessboard.movePiece(source, target, Color.Black);
        resultA = chessboard.getBoardPosition(source.row, source.col);
        resultB = chessboard.getBoardPosition(target.row, target.col);

        Assert.assertEquals(resultA.type(), ChessPieceType.Empty);
        Assert.assertEquals(resultB.type(), ChessPieceType.Pawn);
        Assert.assertEquals(result.type(), ChessPieceType.Empty);
        chessboard.printBoard();
    }

    @Test
    public void TestOccupiedSameColor() {
        Chessboard chessboard = new Chessboard();

        System.out.println(chessboard.getBoardPosition(1 , 0));

        Position source = new Position(0, 0);
        Position target = new Position(1, 0);

        chessboard.printBoard();

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assert.assertEquals(resultA.type(), ChessPieceType.Rock);
        Assert.assertEquals(resultB.type(), ChessPieceType.Pawn);
        Assert.assertEquals(result.type(), ChessPieceType.Empty);
    }

    @Test
    public void TestEmptySource() {
        Chessboard chessboard = new Chessboard();

        System.out.println(chessboard.getBoardPosition(1 , 0));

        Position source = new Position(2, 0);
        Position target = new Position(1, 0);

        chessboard.printBoard();

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assert.assertEquals(resultA.type(), ChessPieceType.Empty);
        Assert.assertEquals(resultB.type(), ChessPieceType.Pawn);
        Assert.assertEquals(result.type(), ChessPieceType.Empty);
    }

    @Test
    public void TestEmptySourceEmptyTarget() {
        Chessboard chessboard = new Chessboard();

        System.out.println(chessboard.getBoardPosition(1 , 0));

        Position source = new Position(2, 0);
        Position target = new Position(3, 0);

        chessboard.printBoard();

        // White
        ChessPiece result = chessboard.movePiece(source, target, Color.White);
        ChessPiece resultA = chessboard.getBoardPosition(source.row, source.col);
        ChessPiece resultB = chessboard.getBoardPosition(target.row, target.col);

        Assert.assertEquals(resultA.type(), ChessPieceType.Empty);
        Assert.assertEquals(resultB.type(), ChessPieceType.Empty);
        Assert.assertEquals(result.type(), ChessPieceType.Empty);
    }

    // TODO: check other methods, set white/black pieces
    // add pieces movement rules
    // display board on the ui

}
