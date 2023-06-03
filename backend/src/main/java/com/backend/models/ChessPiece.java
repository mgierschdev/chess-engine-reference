package com.backend.models;

public record ChessPiece(ChessPieceType type, Color color) {

    public String toString() {
        if (type == ChessPieceType.Empty) {
            return "";
        }
        return color.toString().charAt(0) +"." + type.toString().substring(0, 1);
    }
}
