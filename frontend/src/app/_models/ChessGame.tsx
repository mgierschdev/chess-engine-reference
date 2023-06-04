import {ChessPieceRequest} from "@/app/_models/ChessPieceRequest";
import {Property} from "csstype";
import Color = Property.Color;

export interface ChessGame {
    id: number,
    content : string;
    gameStarted : boolean;
    capturedWhite: any;
    capturedBlack: any;
    chessboard: ChessPieceRequest[][];
    turn : Color;
}