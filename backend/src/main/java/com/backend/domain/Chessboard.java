package com.backend.domain;

import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;
import com.backend.models.requests.ChessPieceResponse;
import com.backend.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Basic chessboard
//8- rock, horse, bishop, king, horse, bishop, horse, rock
//7-  pawn, pawn,   pawn,  pawn,  pawn,  pawn,  pawn,  pawn
//6-  Black side
//5-
//4-
//3- White side
//2-  pawn, pawn,   pawn,  pawn,  pawn,  pawn,  pawn,  pawn
//1-  rock, horse, bishop, queen, king, bishop, horse, rock
//      a ,  b ,    c,      , d ,   e    , f    , g    , h

public class Chessboard {
    private final ChessPiece[][] board;

    private final ChessPiece invalid;

    private final ChessPiece emptySpace;

    public Chessboard() {
        board = GetInitMatrixBoard();
        invalid = new ChessPiece(ChessPieceType.Empty, Color.None);
        emptySpace = new ChessPiece(ChessPieceType.Empty, Color.None);
    }

    // This method returns the captured piece if any otherwhise an empty space
    public ChessPiece movePiece(Position source, Position target, Color player) {
        if (!isMove(source, target, player)) {
            return invalid;
        }

        ChessPiece sourcePosition = board[source.row][source.col];
        ChessPiece targetPosition = board[target.row][target.col];

        board[source.row][source.col] = new ChessPiece(ChessPieceType.Empty, Color.None);
        board[target.row][target.col] = sourcePosition;

        if (targetPosition.color() != player) {
            return targetPosition;
        }

        return emptySpace;
    }

    public boolean isMove(Position source, Position target, Color player) {
        // moving outside board
        if (target.col < 0 || target.col >= board[0].length || source.row < 0 || source.row >= board.length) {
            return false;
        }

        ChessPiece sourcePosition = board[source.row][source.col];
        ChessPiece targetPosition = board[target.row][target.col];

        // moving occupied space by same player
        return sourcePosition.color() == player &&  // moving incorrect piece
                sourcePosition.type() != ChessPieceType.Empty && // moving empty space
                sourcePosition.color() != Color.None && // moving empty space
                targetPosition.color() != player;
    }

    public ChessPiece getBoardPosition(int row, int col) {
        return board[row][col];
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    public void printBoard() {

        for (int row = board.length - 1; row >= 0; row--) {
            for (int col = 0; col < board[0].length; col++) {

                System.out.print(" " + board[row][col].toString() + "(" + Util.GetChessNotation(row, col) + ")");

            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }

    public static ChessPieceResponse[] GetArrayBoard(ChessPiece[][] board) {
        List<ChessPieceResponse> chessboard = new ArrayList<>();
        int position = 1;

        for (ChessPiece[] row : board) {
            for (ChessPiece piece : row) {
                chessboard.add(new ChessPieceResponse(
                        piece.type(),
                        piece.color(),
                        piece.type() != ChessPieceType.Empty,
                        position++
                ));
            }
        }

        return chessboard.toArray(ChessPieceResponse[]::new);
    }

    public static ChessPiece[][] GetInitMatrixBoard() {
        ChessPiece[][] board = new ChessPiece[8][8];

        for (int row = 2; row <= 6; row++) {
            Arrays.fill(board[row], new ChessPiece(ChessPieceType.Empty, Color.None));
        }

        Arrays.fill(board[1], new ChessPiece(ChessPieceType.Pawn, Color.White));
        Arrays.fill(board[6], new ChessPiece(ChessPieceType.Pawn, Color.Black));

        board[7][0] = new ChessPiece(ChessPieceType.Rock, Color.Black);
        board[7][1] = new ChessPiece(ChessPieceType.Knight, Color.Black);
        board[7][2] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][3] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[7][4] = new ChessPiece(ChessPieceType.Queen, Color.Black);
        board[7][5] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][6] = new ChessPiece(ChessPieceType.Knight, Color.Black);
        board[7][7] = new ChessPiece(ChessPieceType.Rock, Color.Black);

        board[0][0] = new ChessPiece(ChessPieceType.Rock, Color.White);
        board[0][1] = new ChessPiece(ChessPieceType.Knight, Color.White);
        board[0][2] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][3] = new ChessPiece(ChessPieceType.King, Color.White);
        board[0][4] = new ChessPiece(ChessPieceType.Queen, Color.White);
        board[0][5] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][6] = new ChessPiece(ChessPieceType.Knight, Color.White);
        board[0][7] = new ChessPiece(ChessPieceType.Rock, Color.White);

        return board;
    }

    // Calculate backend

    // Bishops O(n + m) where n,m is the size of each diagonal

    // Rocks O(n + m) where n,m is the size of each line, straight lines

    // Queen (n + m + a + b) each line / diagonal, All spaces

    // Knights O(c) 8 positions in an L shape

    // King O(c) 8 spaces around which are not attacked

    public Position[] getValidMoves(Position position) {
        if (isFalse(position)) {
            return new Position[0];
        }

        ChessPiece chessPiece = board[position.row][position.col];

        switch (chessPiece.type()) {
            case Pawn -> {
                return getValidMovesPawn(position, chessPiece);
            }
            case Knight -> {
                return getValidMovesKnight(position, chessPiece);
            }
            case Queen -> {
                return getValidMovesQueen(position, chessPiece);
            }
            case King -> {
                return getValidMovesKing(position, chessPiece);
            }
            case Rock -> {
                return getValidMovesRock(position, chessPiece);
            }
            case Bishop -> {
                return getValidMovesBishop(position, chessPiece);
            }
        }
        return new Position[0];
    }

    private Position[] getValidMovesRock(Position position, ChessPiece chessPiece) {
        if (chessPiece.type() != ChessPieceType.Rock) {
            return new Position[0];
        }

        List<Position> valid = new ArrayList<>();

        evaluateCompleteTour(position, new int[]{0, 1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{1, 0}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{0, -1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{-1, 0}, valid, chessPiece.color());

        return valid.toArray(Position[]::new);
    }

    private Position[] getValidMovesBishop(Position position, ChessPiece chessPiece) {
        if (chessPiece.type() != ChessPieceType.Bishop) {
            return new Position[0];
        }

        List<Position> valid = new ArrayList<>();

        evaluateCompleteTour(position, new int[]{1, 1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{-1, 1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{1, -1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{-1, -1}, valid, chessPiece.color());

        return valid.toArray(Position[]::new);
    }

    private Position[] getValidMovesKing(Position position, ChessPiece chessPiece) {
        if (chessPiece.type() != ChessPieceType.King) {
            return new Position[0];
        }

        List<Position> valid = new ArrayList<>();

        addKingTour(position, new int[]{0, -1}, valid, chessPiece.color());
        addKingTour(position, new int[]{-1, 0}, valid, chessPiece.color());
        addKingTour(position, new int[]{0, 1}, valid, chessPiece.color());
        addKingTour(position, new int[]{1, 0}, valid, chessPiece.color());
        addKingTour(position, new int[]{-1, 1}, valid, chessPiece.color());
        addKingTour(position, new int[]{1, -1}, valid, chessPiece.color());
        addKingTour(position, new int[]{-1, -1}, valid, chessPiece.color());
        addKingTour(position, new int[]{1, 1}, valid, chessPiece.color());

        return valid.toArray(Position[]::new);
    }

    private Position[] getValidMovesQueen(Position position, ChessPiece chessPiece) {
        if (chessPiece.type() != ChessPieceType.Queen) {
            return new Position[0];
        }

        List<Position> valid = new ArrayList<>();

        evaluateCompleteTour(position, new int[]{1, 1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{-1, 1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{1, -1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{-1, -1}, valid, chessPiece.color());

        evaluateCompleteTour(position, new int[]{0, 1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{1, 0}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{0, -1}, valid, chessPiece.color());
        evaluateCompleteTour(position, new int[]{-1, 0}, valid, chessPiece.color());

        return valid.toArray(Position[]::new);
    }

    private Position[] getValidMovesKnight(Position position, ChessPiece chessPiece) {
        if (chessPiece.type() != ChessPieceType.Knight) {
            return new Position[0];
        }

        List<Position> valid = new ArrayList<>();

        Position[] positions = new Position[]{
                new Position(position.row + 2, position.col - 1),
                new Position(position.row + 2, position.col + 1),
                new Position(position.row - 2, position.col - 1),
                new Position(position.row - 2, position.col + 1),
                new Position(position.row + 1, position.col + 2),
                new Position(position.row - 1, position.col + 2),
                new Position(position.row + 1, position.col - 2),
                new Position(position.row - 1, position.col - 2)
        };

        for (Position next : positions) {
            if (!isFalse(next) && board[next.row][next.col].color() != chessPiece.color()) {
                valid.add(next);
            }
        }

        return valid.toArray(Position[]::new);
    }

    private Position[] getValidMovesPawn(Position position, ChessPiece chessPiece) {
        if (chessPiece.type() != ChessPieceType.Pawn) {
            return new Position[0];
        }

        List<Position> valid = new ArrayList<>();

        // if a pawn is on the 1 or 7 rank, can move 2 or 1 space forward
        if (chessPiece.color() == Color.White) {
            if (position.row == 1) {
                addPawnTour(position, new int[]{1, 0}, valid);
            }
        }

        if (chessPiece.color() == Color.Black) {
            if (position.row == 7) {
                addPawnTour(position, new int[]{-1, 0}, valid);
            }
        }

        // evaluate on passant
        // check history of moves
        // evaluate if it can eat one space diagonally each side
        // Pawns can become a different piece if they reach the end of the board
        // depending on the color and direction

        return valid.toArray(Position[]::new);
    }

    private void addPawnTour(Position from, int[] orientation, List<Position> list) {
        //evaluate tour from --- to
//        if(isFalse(from) || board[from.row][from.col].color() == pieceMovingColor){
//            return;
//        }
        list.add(from);
    }

    private void addKingTour(Position from, int[] orientation, List<Position> list, Color move) {
        int row = from.row + orientation[0];
        int col = from.col + orientation[1];
        if (isValid(new int[]{row, col})
                &&
                (board[row][col].type() == ChessPieceType.Empty ||
                        board[row][col].color() != move
                )) {
            list.add(new Position(row, col));
        }
    }

    private void evaluateCompleteTour(Position from, int[] orientation, List<Position> list, Color move) {
        int[] start = new int[]{from.row, from.col};

        while (isValid(start) && board[start[0]][start[1]].type() == ChessPieceType.Empty) {
            start[0] += orientation[0];
            start[1] += orientation[1];
            list.add(new Position(start[0], start[1]));
        }

        if(isValid(start) && board[start[0]][start[1]].color() != move){
            list.add(new Position(start[0], start[1]));
        }
    }

    private boolean isFalse(Position position) {
        return position.col < 0 ||
                position.row < 0 ||
                position.row >= board.length ||
                position.col >= board[0].length;
    }

    private boolean isValid(int[] position) {
        return position[1] >= 0 &&
                position[0] >= 0 &&
                position[0] < board.length &&
                position[1] < board[0].length;
    }
}
