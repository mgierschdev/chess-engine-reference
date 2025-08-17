import {Position} from "@/app/_models/Position";
import {ChessPieceType} from "@/app/_models/enums";

export interface ChessboardMoveRequest {
   source: Position;
   target: Position;
   promotionType: ChessPieceType;
}
