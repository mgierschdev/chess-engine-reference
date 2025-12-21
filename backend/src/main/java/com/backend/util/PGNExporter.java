package com.backend.util;

import com.backend.models.*;
import com.backend.domain.Chessboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting chess games to PGN (Portable Game Notation) format.
 * PGN is the standard format for recording chess games.
 */
public class PGNExporter {
    
    /**
     * Exports a chess game to PGN format.
     * 
     * @param moves List of moves in the game
     * @param result Game result (1-0 for white win, 0-1 for black win, 1/2-1/2 for draw, * for ongoing)
     * @param gameState Final game state
     * @return PGN formatted string
     */
    public static String exportToPGN(List<Move> moves, String result, GameState gameState) {
        StringBuilder pgn = new StringBuilder();
        
        // PGN headers (Seven Tag Roster - required tags)
        pgn.append("[Event \"Casual Game\"]\n");
        pgn.append("[Site \"Chess Engine Reference\"]\n");
        pgn.append("[Date \"").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))).append("\"]\n");
        pgn.append("[Round \"1\"]\n");
        pgn.append("[White \"Player 1\"]\n");
        pgn.append("[Black \"Player 2\"]\n");
        pgn.append("[Result \"").append(result).append("\"]\n");
        pgn.append("\n");
        
        // Move text
        if (moves.isEmpty()) {
            pgn.append(result);
        } else {
            int moveNumber = 1;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                
                // Add move number for white's moves
                if (i % 2 == 0) {
                    pgn.append(moveNumber).append(". ");
                }
                
                // Add the move in algebraic notation
                pgn.append(convertToAlgebraicNotation(move, moves, i));
                pgn.append(" ");
                
                // Increment move number after black's move
                if (i % 2 == 1) {
                    moveNumber++;
                }
            }
            
            pgn.append(result);
        }
        
        return pgn.toString();
    }
    
    /**
     * Converts a Move to standard algebraic notation (SAN).
     * Simplified implementation - a complete version would need to check for ambiguities.
     */
    private static String convertToAlgebraicNotation(Move move, List<Move> allMoves, int moveIndex) {
        StringBuilder notation = new StringBuilder();
        ChessPiece piece = move.getPiece();
        Position from = move.getFrom();
        Position to = move.getTo();
        
        // Castling
        if (move.isCastling()) {
            // Kingside or queenside castling
            if (to.col > from.col) {
                return "O-O"; // Kingside
            } else {
                return "O-O-O"; // Queenside
            }
        }
        
        // Piece prefix (not for pawns)
        if (piece.type() != ChessPieceType.Pawn) {
            notation.append(getPieceSymbol(piece.type()));
        }
        
        // For pawn captures, include the file of origin
        if (piece.type() == ChessPieceType.Pawn && move.isCapture()) {
            notation.append(getFileChar(from.col));
        }
        
        // Capture indicator
        if (move.isCapture()) {
            notation.append("x");
        }
        
        // Destination square
        notation.append(getFileChar(to.col)).append(to.row + 1);
        
        // Promotion
        if (move.getPromotionType() != null && piece.type() == ChessPieceType.Pawn &&
            (to.row == 7 || to.row == 0)) {
            notation.append("=").append(getPieceSymbol(move.getPromotionType()));
        }
        
        // En passant
        if (move.isEnPassant()) {
            notation.append(" e.p.");
        }
        
        return notation.toString();
    }
    
    /**
     * Gets the piece symbol for algebraic notation.
     */
    private static String getPieceSymbol(ChessPieceType type) {
        return switch (type) {
            case King -> "K";
            case Queen -> "Q";
            case Rock -> "R";
            case Bishop -> "B";
            case Knight -> "N";
            default -> "";
        };
    }
    
    /**
     * Converts a column index (0-7) to a file character (a-h).
     */
    private static char getFileChar(int col) {
        return (char) ('a' + col);
    }
    
    /**
     * Determines the game result string for PGN.
     */
    public static String getResultString(GameState gameState, Color currentTurn) {
        return switch (gameState) {
            case Checkmate -> currentTurn == Color.White ? "0-1" : "1-0";
            case DrawByStalemate, DrawByRepetition, DrawByFiftyMove -> "1/2-1/2";
            default -> "*"; // Game ongoing
        };
    }
}
