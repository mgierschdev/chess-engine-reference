'use client';
import {ChessPieceType, Color} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";

export default function ChessPieceCell({chessPiece, onCellClick}: any) {
    function getChessPieceClass(chessPiece: ChessPiece): string {
        let className = 'chessboard-grid-cell';

        if (chessPiece.isSelected) {
            className += ' chess-cell-selected';
        }

        if (chessPiece.type === ChessPieceType.Empty ||
            chessPiece.type === ChessPieceType.Invalid ||
            chessPiece.color === Color.None) {
            return className;
        }

        className += ' chess-cell-piece chess-' + chessPiece.type.toString().toLowerCase() + '-' + chessPiece.color.toString().toLowerCase();
        return className;
    }

    return (
        <div
            draggable={chessPiece.isDraggable}
            onClick={() => { return onCellClick(chessPiece.position);}}
            className={getChessPieceClass(chessPiece)}>
        </div>
    );
}
