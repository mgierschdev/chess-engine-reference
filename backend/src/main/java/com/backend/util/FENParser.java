package com.backend.util;

import com.backend.models.ChessPiece;
import com.backend.models.ChessPieceType;
import com.backend.models.Color;
import com.backend.models.Position;

/**
 * Utility class for parsing FEN (Forsyth-Edwards Notation) strings.
 * FEN is the standard format for describing chess positions.
 * 
 * FEN format: [pieces] [turn] [castling] [en-passant] [halfmove] [fullmove]
 * Example: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
 */
public class FENParser {
    
    /**
     * Result of FEN parsing containing all game state information.
     */
    public static class FENParseResult {
        public ChessPiece[][] board;
        public Color activeColor;
        public boolean whiteKingMoved;
        public boolean blackKingMoved;
        public boolean whiteKingsideRookMoved;
        public boolean whiteQueensideRookMoved;
        public boolean blackKingsideRookMoved;
        public boolean blackQueensideRookMoved;
        public Position enPassantTarget;
        public int halfMoveClock;
        public int fullMoveNumber;
        
        public FENParseResult() {
            board = new ChessPiece[8][8];
            activeColor = Color.White;
            whiteKingMoved = true; // assume moved unless castling rights say otherwise
            blackKingMoved = true;
            whiteKingsideRookMoved = true;
            whiteQueensideRookMoved = true;
            blackKingsideRookMoved = true;
            blackQueensideRookMoved = true;
            enPassantTarget = null;
            halfMoveClock = 0;
            fullMoveNumber = 1;
        }
    }
    
    /**
     * Parses a FEN string and returns the board state and game metadata.
     * 
     * @param fen The FEN string to parse
     * @return FENParseResult containing board and game state
     * @throws IllegalArgumentException if FEN string is invalid
     */
    public static FENParseResult parseFEN(String fen) {
        if (fen == null || fen.trim().isEmpty()) {
            throw new IllegalArgumentException("FEN string cannot be null or empty");
        }
        
        String[] parts = fen.trim().split("\\s+");
        if (parts.length < 1 || parts.length > 6) {
            throw new IllegalArgumentException("Invalid FEN format: expected 1-6 parts, got " + parts.length);
        }
        
        FENParseResult result = new FENParseResult();
        
        // Parse piece placement (required)
        parsePiecePlacement(parts[0], result);
        
        // Parse active color (default: white)
        if (parts.length >= 2) {
            parseActiveColor(parts[1], result);
        }
        
        // Parse castling rights (default: none)
        if (parts.length >= 3) {
            parseCastlingRights(parts[2], result);
        }
        
        // Parse en passant target (default: none)
        if (parts.length >= 4) {
            parseEnPassantTarget(parts[3], result);
        }
        
        // Parse halfmove clock (default: 0)
        if (parts.length >= 5) {
            parseHalfMoveClock(parts[4], result);
        }
        
        // Parse fullmove number (default: 1)
        if (parts.length >= 6) {
            parseFullMoveNumber(parts[5], result);
        }
        
        return result;
    }
    
    /**
     * Parses the piece placement portion of FEN.
     * Format: 8 ranks separated by '/', each rank describes pieces from a-h (left to right)
     * Uppercase = white, lowercase = black, digits = empty squares
     */
    private static void parsePiecePlacement(String piecePlacement, FENParseResult result) {
        String[] ranks = piecePlacement.split("/");
        if (ranks.length != 8) {
            throw new IllegalArgumentException("Invalid FEN: expected 8 ranks, got " + ranks.length);
        }
        
        // FEN ranks are from 8 to 1 (top to bottom), but our array is 0-7 (bottom to top)
        // So rank 8 in FEN = row 7 in our array (black's back rank)
        for (int fenRank = 0; fenRank < 8; fenRank++) {
            int row = 7 - fenRank; // Convert FEN rank to array row
            String rank = ranks[fenRank];
            int col = 0;
            
            for (char c : rank.toCharArray()) {
                if (col >= 8) {
                    throw new IllegalArgumentException("Invalid FEN: rank " + (fenRank + 1) + " has too many squares");
                }
                
                if (Character.isDigit(c)) {
                    // Empty squares
                    int emptySquares = Character.getNumericValue(c);
                    for (int i = 0; i < emptySquares; i++) {
                        result.board[row][col++] = new ChessPiece(ChessPieceType.Empty, Color.None);
                    }
                } else {
                    // Piece
                    ChessPiece piece = parsePiece(c);
                    result.board[row][col++] = piece;
                }
            }
            
            if (col != 8) {
                throw new IllegalArgumentException("Invalid FEN: rank " + (fenRank + 1) + " has " + col + " squares (expected 8)");
            }
        }
    }
    
    /**
     * Converts a FEN character to a ChessPiece.
     */
    private static ChessPiece parsePiece(char c) {
        Color color = Character.isUpperCase(c) ? Color.White : Color.Black;
        ChessPieceType type;
        
        switch (Character.toLowerCase(c)) {
            case 'p' -> type = ChessPieceType.Pawn;
            case 'n' -> type = ChessPieceType.Knight;
            case 'b' -> type = ChessPieceType.Bishop;
            case 'r' -> type = ChessPieceType.Rock;
            case 'q' -> type = ChessPieceType.Queen;
            case 'k' -> type = ChessPieceType.King;
            default -> throw new IllegalArgumentException("Invalid piece character in FEN: " + c);
        }
        
        return new ChessPiece(type, color);
    }
    
    /**
     * Parses the active color (w or b).
     */
    private static void parseActiveColor(String colorStr, FENParseResult result) {
        if (colorStr.equals("w")) {
            result.activeColor = Color.White;
        } else if (colorStr.equals("b")) {
            result.activeColor = Color.Black;
        } else {
            throw new IllegalArgumentException("Invalid active color in FEN: " + colorStr + " (expected 'w' or 'b')");
        }
    }
    
    /**
     * Parses castling rights (KQkq or combinations, or '-' for none).
     */
    private static void parseCastlingRights(String castlingStr, FENParseResult result) {
        if (castlingStr.equals("-")) {
            // All pieces have moved (already set in constructor)
            return;
        }
        
        // If any castling right exists, assume pieces haven't moved
        // We'll set them to moved=true for any missing rights
        result.whiteKingMoved = false;
        result.blackKingMoved = false;
        result.whiteKingsideRookMoved = false;
        result.whiteQueensideRookMoved = false;
        result.blackKingsideRookMoved = false;
        result.blackQueensideRookMoved = false;
        
        if (!castlingStr.contains("K")) {
            result.whiteKingsideRookMoved = true;
        }
        if (!castlingStr.contains("Q")) {
            result.whiteQueensideRookMoved = true;
        }
        if (!castlingStr.contains("k")) {
            result.blackKingsideRookMoved = true;
        }
        if (!castlingStr.contains("q")) {
            result.blackQueensideRookMoved = true;
        }
        
        // If both rooks have moved, king has moved
        if (result.whiteKingsideRookMoved && result.whiteQueensideRookMoved) {
            result.whiteKingMoved = true;
        }
        if (result.blackKingsideRookMoved && result.blackQueensideRookMoved) {
            result.blackKingMoved = true;
        }
    }
    
    /**
     * Parses en passant target square (e.g., "e3" or "-" for none).
     */
    private static void parseEnPassantTarget(String enPassantStr, FENParseResult result) {
        if (enPassantStr.equals("-")) {
            result.enPassantTarget = null;
            return;
        }
        
        if (enPassantStr.length() != 2) {
            throw new IllegalArgumentException("Invalid en passant square in FEN: " + enPassantStr);
        }
        
        char file = enPassantStr.charAt(0);
        char rank = enPassantStr.charAt(1);
        
        if (file < 'a' || file > 'h') {
            throw new IllegalArgumentException("Invalid en passant file in FEN: " + file);
        }
        if (rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid en passant rank in FEN: " + rank);
        }
        
        int col = file - 'a';
        int row = rank - '1';
        
        result.enPassantTarget = new Position(row, col);
    }
    
    /**
     * Parses halfmove clock (number of moves since last pawn move or capture).
     */
    private static void parseHalfMoveClock(String halfMoveStr, FENParseResult result) {
        try {
            result.halfMoveClock = Integer.parseInt(halfMoveStr);
            if (result.halfMoveClock < 0) {
                throw new IllegalArgumentException("Halfmove clock cannot be negative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid halfmove clock in FEN: " + halfMoveStr);
        }
    }
    
    /**
     * Parses fullmove number (increments after black's move).
     */
    private static void parseFullMoveNumber(String fullMoveStr, FENParseResult result) {
        try {
            result.fullMoveNumber = Integer.parseInt(fullMoveStr);
            if (result.fullMoveNumber < 1) {
                throw new IllegalArgumentException("Fullmove number must be at least 1");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid fullmove number in FEN: " + fullMoveStr);
        }
    }
    
    /**
     * Generates FEN string from current board state.
     */
    public static String generateFEN(ChessPiece[][] board, Color activeColor, 
                                     boolean whiteKingMoved, boolean blackKingMoved,
                                     boolean whiteKingsideRookMoved, boolean whiteQueensideRookMoved,
                                     boolean blackKingsideRookMoved, boolean blackQueensideRookMoved,
                                     Position enPassantTarget, int halfMoveClock, int fullMoveNumber) {
        StringBuilder fen = new StringBuilder();
        
        // Piece placement
        for (int row = 7; row >= 0; row--) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece.type() == ChessPieceType.Empty) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(pieceToFENChar(piece));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row > 0) {
                fen.append('/');
            }
        }
        
        // Active color
        fen.append(' ').append(activeColor == Color.White ? 'w' : 'b');
        
        // Castling rights
        fen.append(' ');
        StringBuilder castling = new StringBuilder();
        if (!whiteKingMoved) {
            if (!whiteKingsideRookMoved) castling.append('K');
            if (!whiteQueensideRookMoved) castling.append('Q');
        }
        if (!blackKingMoved) {
            if (!blackKingsideRookMoved) castling.append('k');
            if (!blackQueensideRookMoved) castling.append('q');
        }
        fen.append(castling.length() > 0 ? castling : "-");
        
        // En passant target
        fen.append(' ');
        if (enPassantTarget != null) {
            char file = (char) ('a' + enPassantTarget.col);
            char rank = (char) ('1' + enPassantTarget.row);
            fen.append(file).append(rank);
        } else {
            fen.append('-');
        }
        
        // Halfmove clock and fullmove number
        fen.append(' ').append(halfMoveClock);
        fen.append(' ').append(fullMoveNumber);
        
        return fen.toString();
    }
    
    /**
     * Converts a ChessPiece to FEN character.
     */
    private static char pieceToFENChar(ChessPiece piece) {
        char c;
        switch (piece.type()) {
            case Pawn -> c = 'p';
            case Knight -> c = 'n';
            case Bishop -> c = 'b';
            case Rock -> c = 'r';
            case Queen -> c = 'q';
            case King -> c = 'k';
            default -> throw new IllegalArgumentException("Cannot convert piece type to FEN: " + piece.type());
        }
        return piece.color() == Color.White ? Character.toUpperCase(c) : c;
    }
}
