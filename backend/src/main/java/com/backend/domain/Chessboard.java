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

    // Square that can be targeted by an en passant capture
    private Position enPassantTarget;

    public Chessboard() {
        board = GetInitMatrixBoard();
        invalid = new ChessPiece(ChessPieceType.Invalid, Color.None);
        emptySpace = new ChessPiece(ChessPieceType.Empty, Color.None);
        enPassantTarget = null;
    }

    // This method returns the captured piece if any otherwhise an empty space
    public ChessPiece movePiece(Position source, Position target, Color player) {
        if (!isMove(source, target, player)) {
            return invalid;
        }

        // validate target is within the piece valid moves
        Position[] validMoves = getValidMoves(source);
        boolean isValid = Arrays.stream(validMoves)
                .anyMatch(pos -> pos.row == target.row && pos.col == target.col);
        if (!isValid) {
            return invalid;
        }

        ChessPiece sourcePosition = board[source.row][source.col];
        ChessPiece targetPosition = board[target.row][target.col];

        // handle en passant capture
        boolean isEnPassant = sourcePosition.type() == ChessPieceType.Pawn &&
                targetPosition.type() == ChessPieceType.Empty &&
                enPassantTarget != null &&
                target.row == enPassantTarget.row &&
                target.col == enPassantTarget.col &&
                Math.abs(source.col - target.col) == 1;

        // reset en passant target; will be set again if this move is a double step
        enPassantTarget = null;

        if (isEnPassant) {
            int capturedRow = source.row;
            ChessPiece captured = board[capturedRow][target.col];
            board[source.row][source.col] = emptySpace;
            board[capturedRow][target.col] = emptySpace;
            board[target.row][target.col] = sourcePosition;
            return captured;
        }

        board[source.row][source.col] = emptySpace;
        board[target.row][target.col] = sourcePosition;

        if (sourcePosition.type() == ChessPieceType.Pawn && Math.abs(target.row - source.row) == 2) {
            enPassantTarget = new Position((source.row + target.row) / 2, source.col);
        }

        if (targetPosition.color() != player) {
            return targetPosition;
        }

        return emptySpace;
    }

    public boolean isMove(Position source, Position target, Color player) {
        // moving outside board
        if (this.isInvalidPosition(source) || this.isInvalidPosition(target)) {
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

    public Position getEnPassantTarget() {
        return enPassantTarget;
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

        for (ChessPiece[] chessPieces : board) {
            for (int col = board[0].length - 1; col >= 0; col--) {

                ChessPiece piece = chessPieces[col];

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
        board[7][3] = new ChessPiece(ChessPieceType.Queen, Color.Black);
        board[7][4] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[7][5] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][6] = new ChessPiece(ChessPieceType.Knight, Color.Black);
        board[7][7] = new ChessPiece(ChessPieceType.Rock, Color.Black);

        board[0][0] = new ChessPiece(ChessPieceType.Rock, Color.White);
        board[0][1] = new ChessPiece(ChessPieceType.Knight, Color.White);
        board[0][2] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][3] = new ChessPiece(ChessPieceType.Queen, Color.White);
        board[0][4] = new ChessPiece(ChessPieceType.King, Color.White);
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
        if (isInvalidPosition(position)) {
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
            if (!isInvalidPosition(next) && board[next.row][next.col].color() != chessPiece.color()) {
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
        int direction = chessPiece.color() == Color.White ? 1 : -1;
        int startRow = chessPiece.color() == Color.White ? 1 : 6;

        int forwardRow = position.row + direction;

        // forward move
        if (isValidPosition(forwardRow, position.col) && board[forwardRow][position.col].type() == ChessPieceType.Empty) {
            valid.add(new Position(forwardRow, position.col));

            // double move from starting position
            if (position.row == startRow) {
                int twoForward = forwardRow + direction;
                if (isValidPosition(twoForward, position.col) && board[twoForward][position.col].type() == ChessPieceType.Empty) {
                    valid.add(new Position(twoForward, position.col));
                }
            }
        }

        // captures
        int[] captureCols = new int[]{position.col - 1, position.col + 1};
        for (int c : captureCols) {
            if (isValidPosition(forwardRow, c) && board[forwardRow][c].color() == getOpposite(chessPiece.color())) {
                valid.add(new Position(forwardRow, c));
            }
        }

        // en passant capture
        if (enPassantTarget != null) {
            if (position.row + direction == enPassantTarget.row &&
                    Math.abs(position.col - enPassantTarget.col) == 1) {
                ChessPiece adjacent = board[position.row][enPassantTarget.col];
                if (adjacent.type() == ChessPieceType.Pawn && adjacent.color() == getOpposite(chessPiece.color())) {
                    valid.add(new Position(enPassantTarget.row, enPassantTarget.col));
                }
            }
        }

        // Pawns can become a different piece if they reach the end of the board

        return valid.toArray(Position[]::new);
    }

    private void addKingTour(Position from, int[] orientation, List<Position> list, Color move) {
        int row = from.row + orientation[0];
        int col = from.col + orientation[1];
        if (isValidPosition(row, col) && (board[row][col].type() == ChessPieceType.Empty || board[row][col].color() != move)) {
            list.add(new Position(row, col));
        }
    }

    private void evaluateCompleteTour(Position from, int[] orientation, List<Position> list, Color move) {
        int row = from.row + orientation[0];
        int col = from.col + orientation[1];

        while (isValidPosition(row, col) && board[row][col].type() == ChessPieceType.Empty) {
            list.add(new Position(row, col));
            row += orientation[0];
            col += orientation[1];
        }

        if (isValidPosition(row, col) && board[row][col].color() != move) {
            list.add(new Position(row, col));
        }
    }

    private boolean isInvalidPosition(Position position) {
        return position.col < 0 ||
                position.row < 0 ||
                position.row >= board.length ||
                position.col >= board[0].length;
    }

    private boolean isValidPosition(int[] position) {
        return position[1] >= 0 &&
                position[0] >= 0 &&
                position[0] < board.length &&
                position[1] < board[0].length;
    }

    private boolean isValidPosition(int row, int col) {
        return col >= 0 &&
                row >= 0 &&
                row < board.length &&
               col < board[0].length;
    }

    private Color getOpposite(Color color){
        if(color == Color.None){
            return color;
        }
        return  color == Color.White ? Color.Black : Color.White;
    }
}
