package com.backend.models.requests;


import com.backend.models.ChessPiece;

public record PositionResponse(ChessPiece chessPiece, String message) { }