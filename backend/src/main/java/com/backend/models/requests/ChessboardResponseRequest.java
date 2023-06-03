package com.backend.models.requests;

import com.backend.models.ChessPiece;

public record ChessboardResponseRequest(ChessPiece[][] chessboard) { }