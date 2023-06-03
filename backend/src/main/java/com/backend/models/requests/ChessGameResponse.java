package com.backend.models.requests;

import com.backend.models.ChessPiece;
import com.backend.models.Color;

import java.util.Set;

public class ChessGameResponse {
    public long id;
    public String content;
    public boolean gameStarted;
    public Set<ChessPiece> capturedWhite;
    public Set<ChessPiece> capturedBlack;
    public ChessPiece[][] chessboard;
    public Color turn;
}
