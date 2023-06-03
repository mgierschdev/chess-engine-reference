import {ChessPiece} from "@/app/_private/model/ChessGame";

export interface ChessMove {
    chessPiece: ChessPiece;
    message: string;
}
