// returns the x,y position given a 64 array position, O(c)
import {Position} from "@/app/_models/Position";

export function getArrayCord(position: number[]): number {
    //col , row
    let col = Math.abs(8 - position[1]) + 1;
    let row = position[0];
    return ((row - 1) * 8 ) + col;
}

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

export function getPosition(position: number): Position {
    const coords = getCords(position);
    return {row: coords[0], col: coords[1]};
}
