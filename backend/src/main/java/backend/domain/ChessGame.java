package backend.domain;

import backend.domain.Chessboard;
import backend.models.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChessGame {

    public final UUID uid = UUID.randomUUID();

    private final Chessboard chessboard;

    private final Set<ChessPiece> takenWhite;

    private final Set<ChessPiece> takenBlack;

    private Color turn;

    private final GameState gameState;

    public ChessGame() {
        chessboard = new Chessboard();
        takenWhite = new HashSet<>();
        takenBlack = new HashSet<>();
        gameState = GameState.Free;
        turn = Color.White;
    }

    public void StartGame() {
        chessboard.printBoard();
    }

    public boolean isMove(Position a, Position b) {
        return chessboard.isMove(a, b, turn);
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

    public Color getTurn() {
        return turn;
    }
}
