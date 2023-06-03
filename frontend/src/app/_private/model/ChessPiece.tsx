export interface ChessPiece {
    Type: Piece,
    Color: Color,
    Position : Coord
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

export interface Coord {
    x: number,
    y: number
}