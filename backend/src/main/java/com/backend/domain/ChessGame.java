package com.backend.domain;

import com.backend.models.*;
import com.backend.models.requests.ChessPieceResponse;
import com.backend.util.Util;

import java.util.HashSet;
import java.util.Set;

/**
 * Problem: Need to manage chess game state across multiple moves, track turn order,
 * captured pieces, and detect check/checkmate conditions.
 * 
 * Goal: Provide a high-level game controller that coordinates move validation,
 * turn management, and win condition detection.
 * 
 * Approach: Delegates move validation to Chessboard. Maintains game state (Free, Check, Checkmate),
 * current turn (White/Black), and sets of captured pieces. After each move, checks for
 * check and checkmate conditions for the next player.
 * 
 * Time: O(n) per move where n is the number of pieces (for check/checkmate detection)
 * Space: O(1) additional space beyond the board representation
 */
public class ChessGame {
    private GameState gameState;

    private final Chessboard chessboard;

    private final Set<ChessPiece> takenWhite;

    private final Set<ChessPiece> takenBlack;

    private Color turn;

    // square available for en passant capture from the last double-step move
    private Position lastDoubleStep;

    public ChessGame() {
        chessboard = new Chessboard();
        takenWhite = new HashSet<>();
        takenBlack = new HashSet<>();
        gameState = GameState.Free;
        turn = Color.White;
        lastDoubleStep = null;
    }

    public ChessPiece MoveController(String chessNotation) {
        if (chessNotation.length() < 5) {
            return new ChessPiece(ChessPieceType.Invalid, Color.None);
        }

        String[] movement = chessNotation.split("-");
        int[] sourceMatrix = Util.GetMatrixNotation(movement[0]);
        int[] targetMatrix = Util.GetMatrixNotation(movement[1]);

        return MoveController(new Position(sourceMatrix[0], sourceMatrix[1]), new Position(targetMatrix[0], targetMatrix[1]), ChessPieceType.Queen);
    }

    public ChessPiece MoveController(Position a, Position b) {
        return MoveController(a, b, ChessPieceType.Queen);
    }

    // Returns empty if the move was correct, invalid otherwise, the taken piece if there was a taken piece
    public ChessPiece MoveController(Position a, Position b, ChessPieceType promotionType) {
        removeOffsetChessboardPosition(a);
        removeOffsetChessboardPosition(b);

        ChessPiece chessPiece = chessboard.movePiece(a, b, turn, promotionType);

        // track possible en passant target
        lastDoubleStep = chessboard.getEnPassantTarget();

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

        Color nextTurn = turn == Color.White ? Color.Black : Color.White;
        if (chessboard.isCheckmate(nextTurn)) {
            gameState = GameState.Checkmate;
        } else if (chessboard.isKingInCheck(nextTurn)) {
            gameState = GameState.Check;
        } else {
            gameState = GameState.Free;
        }

        turn = nextTurn;

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

    public GameState getGameState(){
        return gameState;
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
