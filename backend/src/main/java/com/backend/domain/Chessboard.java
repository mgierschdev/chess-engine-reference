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

/**
 * Problem: Need to represent an 8x8 chess board, validate moves according to chess rules,
 * handle special moves (castling, en passant, pawn promotion), and detect check/checkmate.
 * 
 * Goal: Implement all chess piece movement rules, prevent illegal moves (including those
 * that would leave the king in check), and determine game-ending conditions.
 * 
 * Approach: Stores board as 8x8 matrix of ChessPiece objects. For each piece type, defines
 * valid move patterns. Validates moves by checking piece-specific rules, path obstruction,
 * and whether the resulting position leaves the player's king in check. Detects checkmate
 * by verifying no valid moves exist that would remove check condition.
 * 
 * Time: O(n) for move validation where n is the number of pieces (worst case: king check detection)
 * Space: O(64) = O(1) for the 8x8 board
 * 
 * Tags: game-logic, chess-rules, state-validation
 */
public class Chessboard {
    private final ChessPiece[][] board;

    private final ChessPiece invalid;

    private final ChessPiece emptySpace;

    // Square that can be targeted by an en passant capture
    private Position enPassantTarget;

    // Track if kings and rooks have moved (for castling)
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteKingsideRookMoved = false;
    private boolean whiteQueensideRookMoved = false;
    private boolean blackKingsideRookMoved = false;
    private boolean blackQueensideRookMoved = false;

    public Chessboard() {
        board = GetInitMatrixBoard();
        invalid = new ChessPiece(ChessPieceType.Invalid, Color.None);
        emptySpace = new ChessPiece(ChessPieceType.Empty, Color.None);
        enPassantTarget = null;
    }

    // This method returns the captured piece if any otherwise an empty space
    public ChessPiece movePiece(Position source, Position target, Color player, ChessPieceType promotionType) {
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

        // validate promotion type if pawn reaches last rank
        if (sourcePosition.type() == ChessPieceType.Pawn &&
                ((player == Color.White && target.row == 7) || (player == Color.Black && target.row == 0))) {
            if (!isValidPromotionType(promotionType)) {
                return invalid;
            }
        }

        // handle en passant capture
        boolean isEnPassant = sourcePosition.type() == ChessPieceType.Pawn &&
                targetPosition.type() == ChessPieceType.Empty &&
                enPassantTarget != null &&
                target.row == enPassantTarget.row &&
                target.col == enPassantTarget.col &&
                Math.abs(source.col - target.col) == 1;

        // Check for castling
        boolean isCastling = sourcePosition.type() == ChessPieceType.King &&
                Math.abs(target.col - source.col) == 2;

        // reset en passant target; will be set again if this move is a double step
        enPassantTarget = null;

        if (isEnPassant) {
            int capturedRow = source.row;
            ChessPiece captured = board[capturedRow][target.col];
            board[source.row][source.col] = emptySpace;
            board[capturedRow][target.col] = emptySpace;
            board[target.row][target.col] = sourcePosition;

            // handle promotion after en passant (though practically unreachable)
            if (sourcePosition.type() == ChessPieceType.Pawn &&
                    ((player == Color.White && target.row == 7) || (player == Color.Black && target.row == 0))) {
                board[target.row][target.col] = new ChessPiece(promotionType, player);
            }
            return captured;
        }

        // Handle castling - move both king and rook
        if (isCastling) {
            int rookSourceCol = target.col > source.col ? 7 : 0; // Kingside or queenside
            int rookTargetCol = target.col > source.col ? target.col - 1 : target.col + 1;
            
            ChessPiece rook = board[source.row][rookSourceCol];
            board[source.row][source.col] = emptySpace;
            board[target.row][target.col] = sourcePosition;
            board[source.row][rookSourceCol] = emptySpace;
            board[source.row][rookTargetCol] = rook;
            
            // Mark king as moved
            if (player == Color.White) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
            
            return emptySpace;
        }

        board[source.row][source.col] = emptySpace;
        board[target.row][target.col] = sourcePosition;

        // Track king and rook moves for castling eligibility
        if (sourcePosition.type() == ChessPieceType.King) {
            if (player == Color.White) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        } else if (sourcePosition.type() == ChessPieceType.Rock) {
            if (player == Color.White) {
                if (source.row == 0 && source.col == 0) {
                    whiteQueensideRookMoved = true;
                } else if (source.row == 0 && source.col == 7) {
                    whiteKingsideRookMoved = true;
                }
            } else {
                if (source.row == 7 && source.col == 0) {
                    blackQueensideRookMoved = true;
                } else if (source.row == 7 && source.col == 7) {
                    blackKingsideRookMoved = true;
                }
            }
        }

        if (sourcePosition.type() == ChessPieceType.Pawn && Math.abs(target.row - source.row) == 2) {
            enPassantTarget = new Position((source.row + target.row) / 2, source.col);
        }

        // handle promotion when pawn reaches last rank
        if (sourcePosition.type() == ChessPieceType.Pawn &&
                ((player == Color.White && target.row == 7) || (player == Color.Black && target.row == 0))) {
            board[target.row][target.col] = new ChessPiece(promotionType, player);
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

    public boolean isKingInCheck(Color color){
        Position kingPos = findKing(color);
        if(kingPos == null){
            return false;
        }
        Color opponent = getOpposite(color);
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board[r].length; c++){
                ChessPiece piece = board[r][c];
                if(piece.color() == opponent){
                    // Use getCandidateMoves to avoid infinite recursion
                    Position[] moves = getCandidateMoves(new Position(r,c), piece);
                    for(Position pos : moves){
                        if(pos.row == kingPos.row && pos.col == kingPos.col){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(Color color){
        if(!isKingInCheck(color)){
            return false;
        }
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board[r].length; c++){
                if(board[r][c].color() == color){
                    Position from = new Position(r,c);
                    Position[] moves = getValidMoves(from);
                    for(Position to : moves){
                        ChessPiece captured = simulateMove(from, to);
                        boolean stillCheck = isKingInCheck(color);
                        undoMove(from, to, captured);
                        if(!stillCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isStalemate(Color color){
        // Stalemate occurs when the player is NOT in check but has no legal moves
        if(isKingInCheck(color)){
            return false;
        }
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board[r].length; c++){
                if(board[r][c].color() == color){
                    Position from = new Position(r,c);
                    Position[] moves = getValidMoves(from);
                    if(moves.length > 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private ChessPiece simulateMove(Position from, Position to){
        ChessPiece moving = board[from.row][from.col];
        ChessPiece captured = board[to.row][to.col];
        board[to.row][to.col] = moving;
        board[from.row][from.col] = emptySpace;
        return captured;
    }

    private void undoMove(Position from, Position to, ChessPiece captured){
        ChessPiece moving = board[to.row][to.col];
        board[from.row][from.col] = moving;
        board[to.row][to.col] = captured;
    }

    private Position findKing(Color color){
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board[r].length; c++){
                ChessPiece piece = board[r][c];
                if(piece.type() == ChessPieceType.King && piece.color() == color){
                    return new Position(r,c);
                }
            }
        }
        return null;
    }

    private boolean isValidPromotionType(ChessPieceType type) {
        return type == ChessPieceType.Queen ||
                type == ChessPieceType.Rock ||
                type == ChessPieceType.Bishop ||
                type == ChessPieceType.Knight;
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
        Position[] candidateMoves = getCandidateMoves(position, chessPiece);
        
        // For kings, add castling moves before filtering
        if (chessPiece.type() == ChessPieceType.King) {
            List<Position> withCastling = new ArrayList<>(Arrays.asList(candidateMoves));
            addCastlingMoves(position, withCastling, chessPiece.color());
            candidateMoves = withCastling.toArray(Position[]::new);
        }

        // Filter out moves that would leave the player's king in check
        return filterMovesLeavingKingInCheck(position, candidateMoves, chessPiece.color());
    }

    /**
     * Gets candidate moves for a piece without checking king safety.
     * This is used internally to avoid infinite recursion when checking if king is in check.
     * 
     * @param position The position of the piece
     * @param chessPiece The piece to get moves for
     * @return Array of candidate moves (may leave king in check)
     */
    private Position[] getCandidateMoves(Position position, ChessPiece chessPiece) {
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
                return getValidMovesKingBasic(position, chessPiece);
            }
            case Rock -> {
                return getValidMovesRock(position, chessPiece);
            }
            case Bishop -> {
                return getValidMovesBishop(position, chessPiece);
            }
            default -> {
                return new Position[0];
            }
        }
    }

    /**
     * Filters out moves that would leave the player's king in check.
     * Simulates each move and checks if the king is in check after the move.
     * 
     * @param from The source position
     * @param candidateMoves All potential moves for the piece
     * @param playerColor The color of the player making the move
     * @return Array of legal moves that don't leave king in check
     */
    private Position[] filterMovesLeavingKingInCheck(Position from, Position[] candidateMoves, Color playerColor) {
        List<Position> legalMoves = new ArrayList<>();
        ChessPiece piece = board[from.row][from.col];
        boolean isKing = piece.type() == ChessPieceType.King;
        
        for (Position to : candidateMoves) {
            // Castling moves are already validated and don't need simulation
            // (king moves 2 squares horizontally)
            boolean isCastlingMove = isKing && Math.abs(to.col - from.col) == 2;
            
            if (isCastlingMove) {
                legalMoves.add(to);
                continue;
            }
            
            // Simulate the move
            ChessPiece captured = simulateMove(from, to);
            
            // Check if this move leaves the king in check
            boolean kingInCheck = isKingInCheck(playerColor);
            
            // Undo the move
            undoMove(from, to, captured);
            
            // If the king is not in check after this move, it's legal
            if (!kingInCheck) {
                legalMoves.add(to);
            }
        }
        
        return legalMoves.toArray(Position[]::new);
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

    /**
     * Adds castling moves if conditions are met.
     * Castling is allowed if:
     * 1. King hasn't moved
     * 2. Rook hasn't moved
     * 3. No pieces between king and rook
     * 4. King is not in check
     * 5. King doesn't move through check
     * 6. King doesn't land in check
     */
    private void addCastlingMoves(Position kingPos, List<Position> valid, Color color) {
        // Check if king is in check - can't castle out of check
        if (isKingInCheck(color)) {
            return;
        }

        if (color == Color.White) {
            // White kingside castling
            if (!whiteKingMoved && !whiteKingsideRookMoved &&
                kingPos.row == 0 && kingPos.col == 4) {
                if (canCastleKingside(0, color)) {
                    valid.add(new Position(0, 6));
                }
            }
            // White queenside castling
            if (!whiteKingMoved && !whiteQueensideRookMoved &&
                kingPos.row == 0 && kingPos.col == 4) {
                if (canCastleQueenside(0, color)) {
                    valid.add(new Position(0, 2));
                }
            }
        } else {
            // Black kingside castling
            if (!blackKingMoved && !blackKingsideRookMoved &&
                kingPos.row == 7 && kingPos.col == 4) {
                if (canCastleKingside(7, color)) {
                    valid.add(new Position(7, 6));
                }
            }
            // Black queenside castling
            if (!blackKingMoved && !blackQueensideRookMoved &&
                kingPos.row == 7 && kingPos.col == 4) {
                if (canCastleQueenside(7, color)) {
                    valid.add(new Position(7, 2));
                }
            }
        }
    }

    /**
     * Check if kingside castling is possible (no pieces between, king doesn't move through check).
     */
    private boolean canCastleKingside(int row, Color color) {
        // Check squares between king and rook are empty
        if (board[row][5].type() != ChessPieceType.Empty || 
            board[row][6].type() != ChessPieceType.Empty) {
            return false;
        }

        // Check king doesn't move through check (squares f1/f8 and g1/g8)
        return !isSquareUnderAttack(new Position(row, 5), color) &&
               !isSquareUnderAttack(new Position(row, 6), color);
    }

    /**
     * Check if queenside castling is possible (no pieces between, king doesn't move through check).
     */
    private boolean canCastleQueenside(int row, Color color) {
        // Check squares between king and rook are empty
        if (board[row][1].type() != ChessPieceType.Empty || 
            board[row][2].type() != ChessPieceType.Empty ||
            board[row][3].type() != ChessPieceType.Empty) {
            return false;
        }

        // Check king doesn't move through check (squares d1/d8 and c1/c8)
        // Note: b1/b8 doesn't need to be safe, only the king's path
        return !isSquareUnderAttack(new Position(row, 3), color) &&
               !isSquareUnderAttack(new Position(row, 2), color);
    }

    /**
     * Check if a square is under attack by the opponent.
     */
    private boolean isSquareUnderAttack(Position square, Color defendingColor) {
        Color attackingColor = getOpposite(defendingColor);
        
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                ChessPiece piece = board[r][c];
                if (piece.color() == attackingColor) {
                    Position[] moves = getCandidateMoves(new Position(r, c), piece);
                    for (Position move : moves) {
                        if (move.row == square.row && move.col == square.col) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets basic king moves (one square in any direction) without castling.
     * Used by getCandidateMoves to avoid infinite recursion.
     */
    private Position[] getValidMovesKingBasic(Position position, ChessPiece chessPiece) {
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

    /**
     * Sets the board state from a parsed FEN result.
     * This allows initializing the board from a FEN string.
     */
    public void setFromFEN(com.backend.util.FENParser.FENParseResult fenResult) {
        // Copy board state
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = fenResult.board[row][col];
            }
        }
        
        // Set castling rights
        whiteKingMoved = fenResult.whiteKingMoved;
        blackKingMoved = fenResult.blackKingMoved;
        whiteKingsideRookMoved = fenResult.whiteKingsideRookMoved;
        whiteQueensideRookMoved = fenResult.whiteQueensideRookMoved;
        blackKingsideRookMoved = fenResult.blackKingsideRookMoved;
        blackQueensideRookMoved = fenResult.blackQueensideRookMoved;
        
        // Set en passant target
        enPassantTarget = fenResult.enPassantTarget;
    }
    
    /**
     * Restores the board state from a board array and castling state.
     * Used for undo/redo functionality.
     */
    public void restoreState(ChessPiece[][] boardState, Position enPassant,
                           boolean whiteKMoved, boolean blackKMoved,
                           boolean whiteKRMoved, boolean whiteQRMoved,
                           boolean blackKRMoved, boolean blackQRMoved) {
        // Copy board state
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = boardState[row][col];
            }
        }
        
        // Restore castling rights
        whiteKingMoved = whiteKMoved;
        blackKingMoved = blackKMoved;
        whiteKingsideRookMoved = whiteKRMoved;
        whiteQueensideRookMoved = whiteQRMoved;
        blackKingsideRookMoved = blackKRMoved;
        blackQueensideRookMoved = blackQRMoved;
        
        // Restore en passant target
        enPassantTarget = enPassant;
    }
    
    /**
     * Gets castling rights for FEN export.
     */
    public boolean getWhiteKingMoved() {
        return whiteKingMoved;
    }
    
    public boolean getBlackKingMoved() {
        return blackKingMoved;
    }
    
    public boolean getWhiteKingsideRookMoved() {
        return whiteKingsideRookMoved;
    }
    
    public boolean getWhiteQueensideRookMoved() {
        return whiteQueensideRookMoved;
    }
    
    public boolean getBlackKingsideRookMoved() {
        return blackKingsideRookMoved;
    }
    
    public boolean getBlackQueensideRookMoved() {
        return blackQueensideRookMoved;
    }
}
