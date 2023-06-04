import {Property} from "csstype";
import Color = Property.Color;
import {ChessPieceType} from "@/app/_models/enums";

export interface ChessPieceRequest {
    type: ChessPieceType,
    color: Color
}
