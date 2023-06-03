package backend.models;

import backend.models.ChessPieceType;
import backend.models.Color;

public record ChessPiece(ChessPieceType type, Color color) {

    public String ToString() {
        return type + "_" + color;
    }
}
