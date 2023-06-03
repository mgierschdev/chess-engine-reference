package com.backend.models.requests;


import com.backend.models.ChessPiece;

public record MoveResponse(ChessPiece chessPiece, String message) { }