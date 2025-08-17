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
        Assert.assertEquals(result.type(), ChessPieceType.Invalid);
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
        Assert.assertEquals(result.type(), ChessPieceType.Invalid);
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
        Assert.assertEquals(result.type(), ChessPieceType.Invalid);
    }

    @Test
    public void TestRookMovesAfterClearingPath() {
        Chessboard chessboard = new Chessboard();

        chessboard.movePiece(new Position(1, 0), new Position(3, 0), Color.White);

        Position[] moves = chessboard.getValidMoves(new Position(0, 0));

        Assert.assertEquals(moves.length, 2);
        Assert.assertEquals(moves[0].row, 1);
        Assert.assertEquals(moves[0].col, 0);
        Assert.assertEquals(moves[1].row, 2);
        Assert.assertEquals(moves[1].col, 0);
    }

    @Test
    public void TestPawnDiagonalCapture() {
        Chessboard chessboard = new Chessboard();

        // advance white pawn two spaces
        chessboard.movePiece(new Position(1, 0), new Position(3, 0), Color.White);
        // advance black pawn to be capturable
        chessboard.movePiece(new Position(6, 1), new Position(4, 1), Color.Black);

        Position[] moves = chessboard.getValidMoves(new Position(3, 0));
        Assert.assertEquals(moves.length, 2);

        // capture the black pawn diagonally
        ChessPiece capture = chessboard.movePiece(new Position(3, 0), new Position(4, 1), Color.White);

        Assert.assertEquals(capture.type(), ChessPieceType.Pawn);
        Assert.assertEquals(chessboard.getBoardPosition(4, 1).color(), Color.White);
    }

    @Test
    public void TestInvalidKnightMove() {
        Chessboard chessboard = new Chessboard();

        ChessPiece result = chessboard.movePiece(new Position(0, 1), new Position(2, 1), Color.White);

        Assert.assertEquals(result.type(), ChessPieceType.Invalid);
        Assert.assertEquals(chessboard.getBoardPosition(0, 1).type(), ChessPieceType.Knight);
    }

    @Test
    public void TestEnPassantCapture() {
        Chessboard chessboard = new Chessboard();

        // Position a black pawn next to where a white pawn will double step
        chessboard.movePiece(new Position(6, 1), new Position(4, 1), Color.Black);
        chessboard.movePiece(new Position(4, 1), new Position(3, 1), Color.Black);

        // White pawn performs double step to enable en passant
        chessboard.movePiece(new Position(1, 0), new Position(3, 0), Color.White);

        // Black pawn should have en passant move available
        Position[] moves = chessboard.getValidMoves(new Position(3, 1));
        boolean hasEnPassant = false;
        for (Position p : moves) {
            if (p.row == 2 && p.col == 0) {
                hasEnPassant = true;
                break;
            }
        }
        Assert.assertTrue(hasEnPassant);

        // Execute en passant capture
        ChessPiece captured = chessboard.movePiece(new Position(3, 1), new Position(2, 0), Color.Black);

        Assert.assertEquals(captured.type(), ChessPieceType.Pawn);
        Assert.assertEquals(captured.color(), Color.White);
        Assert.assertEquals(chessboard.getBoardPosition(2, 0).color(), Color.Black);
        Assert.assertEquals(chessboard.getBoardPosition(3, 0).type(), ChessPieceType.Empty);
    }

    // TODO: check other methods, set white/black pieces
    // add pieces movement rules
    // display board on the ui

}
