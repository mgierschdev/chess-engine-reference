// returns the x,y position given a 64 array position, O(c)
import {Position} from "@/app/_models/Position";

// returns a chessboard number position given a row/col
export function getArrayCord(position: number[]): number {
    //col , row
    let col = Math.abs(8 - position[1]) + 1;
    let row = position[0];
    return ((row - 1) * 8 ) + col;
}

// gets chessboard cords given a number in the array
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

// gets a chessboard position object given a number in the array chessboard
export function getPosition(position: number): Position {
    const coords = getCords(position);
    return {row: coords[0], col: coords[1]};
}
