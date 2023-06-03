package com.backend.models.requests;


import com.backend.models.ChessPiece;

public record MoveResponseRequest(ChessPiece chessPiece, String message) { }