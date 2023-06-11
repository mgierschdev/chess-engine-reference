import {Position} from "@/app/_models/Position";
import {Color} from "@/app/_models/enums";

export interface ChessboardMoveRequest {
   source: Position;
   target: Position;
}
