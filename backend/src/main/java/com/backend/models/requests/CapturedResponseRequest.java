package com.backend.models.requests;

import com.backend.models.ChessPiece;

import java.util.Set;

public record CapturedResponseRequest(Set<ChessPiece> chessboard) { }