'use client';
import cn from 'classnames';
import {ChessPieceType, Color} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";

export default function ChessPieceCell({chessPiece}: any) {

    function onCellClick() {
        console.log("Clicking over "+chessPiece.type+"-"+chessPiece.color)
    }


    return (
        <div
            draggable={chessPiece.isDraggable}
            onClick={e => onCellClick()}
            className={cn('chessboard-grid-cell', getChessPieceClass(chessPiece))}></div>
    );

    function getChessPieceClass(chessPiece: ChessPiece): string {
        if (chessPiece.type === ChessPieceType.Empty ||
            chessPiece.type === ChessPieceType.Invalid ||
            chessPiece.color === Color.None) {
            return "";
        }
        return "chess-cell-piece chess-" + chessPiece.type.toString().toLowerCase() + "-" + chessPiece.color.toString().toLowerCase();
    }
}
