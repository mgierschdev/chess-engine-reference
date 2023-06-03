package backend.domain;

import backend.models.ChessPiece;
import backend.models.ChessPieceType;
import backend.models.Color;
import backend.models.Position;

import java.util.Arrays;

// Basic chessboard

//8- rock, horse, bishop, king, horse, bishop, horse, rock
//7-  pawn, pawn,   pawn,  pawn,  pawn,  pawn,  pawn,  pawn
//6-  Black side
//5-
//4-
//3- White side
//2-  pawn, pawn,   pawn,  pawn,  pawn,  pawn,  pawn,  pawn
//1-  rock, horse, bishop, queen, king, bishop, horse, rock
//    a ,  b ,    c,      , d ,   e    , f    , g    , h

public class Chessboard {
    private final ChessPiece[][] board;

    private final ChessPiece invalid;

    private final ChessPiece emptySpace;

    public Chessboard() {
        board = new ChessPiece[8][8];
        invalid =  new ChessPiece(ChessPieceType.Empty, Color.None);
        emptySpace = new ChessPiece(ChessPieceType.Empty, Color.None);

        for (int row = 2; row <= 6; row++) {
            Arrays.fill(board[row], emptySpace);
        }

        Arrays.fill(board[1], new ChessPiece(ChessPieceType.Pawn, Color.White));
        Arrays.fill(board[7], new ChessPiece(ChessPieceType.Pawn, Color.Black));

        board[7][0] = new ChessPiece(ChessPieceType.Rock, Color.Black);
        board[7][1] = new ChessPiece(ChessPieceType.Horse, Color.Black);
        board[7][2] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][3] = new ChessPiece(ChessPieceType.King, Color.Black);
        board[7][4] = new ChessPiece(ChessPieceType.Queen, Color.Black);
        board[7][5] = new ChessPiece(ChessPieceType.Bishop, Color.Black);
        board[7][6] = new ChessPiece(ChessPieceType.Horse, Color.Black);
        board[7][7] = new ChessPiece(ChessPieceType.Rock, Color.Black);

        board[0][0] = new ChessPiece(ChessPieceType.Rock, Color.White);
        board[0][1] = new ChessPiece(ChessPieceType.Horse, Color.White);
        board[0][2] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][3] = new ChessPiece(ChessPieceType.King, Color.White);
        board[0][4] = new ChessPiece(ChessPieceType.Queen, Color.White);
        board[0][5] = new ChessPiece(ChessPieceType.Bishop, Color.White);
        board[0][6] = new ChessPiece(ChessPieceType.Horse, Color.White);
        board[0][7] = new ChessPiece(ChessPieceType.Rock, Color.White);
    }

    public ChessPiece movePiece(Position source, Position target, Color player) {
        if (!isMove(source, target, player)) {
            return invalid;
        }

        ChessPiece sourcePosition = board[source.row][source.col];
        ChessPiece targetPosition = board[target.row][target.col];

        board[source.row][source.col] = sourcePosition;

        if(targetPosition.color() != player){
            return targetPosition;
        }

        return emptySpace;
    }

    public boolean isMove(Position source, Position target, Color player){
        // moving outside board
        if(target.col < 0 || target.col >= board[0].length || source.row < 0 || source.row >= board.length){
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

    public void printBoard() {
        for (ChessPiece[] chessPieces : board) {
            for (int j = board[0].length - 1; j >= 0 ; j--) {
                System.out.print(" " + chessPieces[j].toString());
            }
            System.out.println(" ");
        }
    }
}
