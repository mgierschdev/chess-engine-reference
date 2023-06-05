package com.backend.models.requests;

import com.backend.models.ChessPieceType;
import com.backend.models.Color;

public class ChessPieceResponse {
    public ChessPieceType type;
    public Color color;
    public boolean isDraggable;
    public boolean isSelected = false;
    public int position;

    public ChessPieceResponse(ChessPieceType type, Color color, boolean isDraggable, int position) {
        this.type = type;
        this.color = color;
        this.isDraggable = isDraggable;
        this.position = position;
    }
}
