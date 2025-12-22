import {Property} from "csstype";
import Color = Property.Color;
import {ChessPiece} from "@/app/_models/ChessPiece";
import {GameState} from "@/app/_models/enums";
import {Move} from "@/app/_models/Move";

export interface ChessGame {
    id: number,
    content : string;
    gameStarted : boolean;
    capturedWhite: any;
    capturedBlack: any;
    chessboard: ChessPiece[];
    turn : Color;
    gameState: GameState;
    moveHistory: Move[];
}
