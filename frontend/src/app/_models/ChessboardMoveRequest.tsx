import {Position} from "@/app/_models/Position";

export interface ChessboardMoveRequest {
   source: Position;
   target: Position;
}
