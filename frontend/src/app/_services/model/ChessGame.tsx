export interface ChessGame {
    id: number,
    content : string;
    gameStarted : boolean;
    capturedWhite: any;
    capturedBlack: any;
    chessboard: ChessPiece[][];
    turn : Color;
}

export interface ChessPiece {
    Type: Piece,
    Color: Color,
}

export enum Piece {
    Pawn,
    Horse,
    Rock,
    Bishop,
    Queen,
    King
}

export enum Color {
    Black,
    White
}
