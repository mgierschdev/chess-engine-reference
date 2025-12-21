package com.backend.models;

/**
 * Represents a single move in a chess game.
 * Used for move history tracking, PGN export, and draw detection (threefold repetition, 50-move rule).
 */
public class Move {
    private final Position from;
    private final Position to;
    private final ChessPiece piece;
    private final ChessPiece capturedPiece;
    private final ChessPieceType promotionType;
    private final boolean isEnPassant;
    private final boolean isCastling;
    private final String algebraicNotation;
    
    public Move(Position from, Position to, ChessPiece piece, ChessPiece capturedPiece,
                ChessPieceType promotionType, boolean isEnPassant, boolean isCastling) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.promotionType = promotionType;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.algebraicNotation = "";
    }
    
    public Move(Position from, Position to, ChessPiece piece, ChessPiece capturedPiece,
                ChessPieceType promotionType, boolean isEnPassant, boolean isCastling, String algebraicNotation) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.promotionType = promotionType;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.algebraicNotation = algebraicNotation;
    }
    
    public Position getFrom() {
        return from;
    }
    
    public Position getTo() {
        return to;
    }
    
    public ChessPiece getPiece() {
        return piece;
    }
    
    public ChessPiece getCapturedPiece() {
        return capturedPiece;
    }
    
    public ChessPieceType getPromotionType() {
        return promotionType;
    }
    
    public boolean isEnPassant() {
        return isEnPassant;
    }
    
    public boolean isCastling() {
        return isCastling;
    }
    
    public String getAlgebraicNotation() {
        return algebraicNotation;
    }
    
    public boolean isCapture() {
        return capturedPiece.type() != ChessPieceType.Empty;
    }
    
    public boolean isPawnMove() {
        return piece.type() == ChessPieceType.Pawn;
    }
}
