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
    type: Piece,
    color: Color,

}

export enum Piece {
    Pawn = "Pawn",
    Horse = "Horse",
    Rock = "Rock",
    Bishop = "Bishop",
    Queen = "Queen",
    King = "King"
}

export enum Color {
    Black = "Black",
    White = "White"
}
