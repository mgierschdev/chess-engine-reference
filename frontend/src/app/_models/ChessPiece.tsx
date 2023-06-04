import {Property} from "csstype";
import {ChessPieceType, Color} from "@/app/_models/enums";

export interface ChessPiece {
    type: ChessPieceType,
    color: Color,
    isDraggable: boolean,
    row: number,
    col: number
}
