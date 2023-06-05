import {ChessPiece} from "@/app/_models/ChessPiece";
import {BoardPosition} from "@/app/_models/BoardPosition";

// returns the x,y position given a 64 array position, O(c)
export function getCords(position: number): number[] {
    // invalid position
    if (position > 64) {
        return [-1, -1];
    }

    let col = position == 8 ? 8 : position % 8;
    let row = Math.ceil(position / 8);
    let numberElementsTillRow = (row - 1) * 8;

    if (row > 1) {
        col = position - numberElementsTillRow;
    }

    // we invert the col position
    col = Math.abs(8 - col) + 1;

    return [row, col];
}

export function getValidMovesChessPiece(chessPiece : ChessPiece): BoardPosition[]{
    let positions : BoardPosition[] = [];

    // Pawns O(c)
    // if a pawn is on the 1 or 7 rank, can move 2 or 1 space forward
    // evaluate on passant
    // evaluate if it can eat one space diagonally each side
    // Pawns can become a different piece if they reach the end of the board
    // depending on the color only one direction

    // Bishops O(n + m) where n,m is the size of each diagonal

    // Rocks O(n + m) where n,m is the size of each line, straight lines

    // Queen (n + m + a + b) each line / diagonal, All spaces

    // Knights O(c) 8 positions in an L shape

    // King O(c) 8 spaces around which are not attacked

    positions.push(
        {
            col: 0, row: 0
        }
    );

    return positions;
}
