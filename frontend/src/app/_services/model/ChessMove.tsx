import {ChessPiece} from "@/app/_services/model/ChessGame";

export interface ChessMove {
    chessPiece: ChessPiece;
    message: string;
}
