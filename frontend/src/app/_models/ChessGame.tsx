import {Property} from "csstype";
import Color = Property.Color;
import {ChessPiece} from "@/app/_models/ChessPiece";

export interface ChessGame {
    id: number,
    content : string;
    gameStarted : boolean;
    capturedWhite: any;
    capturedBlack: any;
    chessboard: ChessPiece[];
    turn : Color;
}
