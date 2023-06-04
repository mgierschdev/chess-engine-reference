'use client';
import cn from 'classnames';
import {ChessPieceType, Color} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {useState} from "react";

export default function ChessPieceCell({chessPiece}: any) {

    const [chessPieceClass, SetChessPieceClass] =
        useState(getChessPieceClass(chessPiece));

    function onCellClick() {
         console.log('Clicking over '+chessPiece.type+'-'+chessPiece.color);
    }

    return (
        <div
            draggable={chessPiece.isDraggable}
            onClick={e => onCellClick()}
            className={chessPieceClass}></div>
    );

    function getChessPieceClass(chessPiece: ChessPiece): string {

        let className = 'chessboard-grid-cell';

        if (chessPiece.type === ChessPieceType.Empty ||
            chessPiece.type === ChessPieceType.Invalid ||
            chessPiece.color === Color.None) {
            return className;
        }

        if(chessPiece.isSelected){
            className += ' chess-cell-selected';
        }

        className+= ' chess-cell-piece chess-' + chessPiece.type.toString().toLowerCase() + '-' + chessPiece.color.toString().toLowerCase();

        return className;
    }
}
