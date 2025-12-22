import { ChessPiece } from "./ChessPiece";
import { ChessPieceType } from "./enums";
import { Position } from "./Position";

export interface Move {
    from: Position;
    to: Position;
    piece: ChessPiece;
    capturedPiece: ChessPiece;
    promotionType: ChessPieceType;
    enPassant: boolean;
    castling: boolean;
    algebraicNotation: string;
}
