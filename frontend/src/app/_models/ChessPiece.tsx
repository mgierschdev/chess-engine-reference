import {ChessPieceType, Color} from "@/app/_models/enums";

export interface ChessPiece {
    type: ChessPieceType,
    color: Color,
    isDraggable: boolean,
    position: number,
    isSelected: boolean
}
