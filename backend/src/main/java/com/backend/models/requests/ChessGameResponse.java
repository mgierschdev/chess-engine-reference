package com.backend.models.requests;

import com.backend.models.ChessPiece;
import com.backend.models.Color;
import com.backend.models.GameState;
import com.backend.models.Move;

import java.util.List;
import java.util.Set;

public class ChessGameResponse {
    public long id;
    public String content;
    public boolean gameStarted;
    public Set<ChessPiece> capturedWhite;
    public Set<ChessPiece> capturedBlack;
    public ChessPieceResponse[] chessboard;
    public Color turn;
    public GameState gameState;
    public List<Move> moveHistory;
}
