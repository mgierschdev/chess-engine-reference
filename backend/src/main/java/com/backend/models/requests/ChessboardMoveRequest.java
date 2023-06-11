package com.backend.models.requests;

import com.backend.models.Color;
import com.backend.models.Position;

public class ChessboardMoveRequest {
    public Position source;
    public Position target;
    public Color player;
}
