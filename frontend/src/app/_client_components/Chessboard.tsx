'use client';

import {ChessService} from "@/app/_services/ChessService";
import React, {useState} from "react";
import ChessPieceCell from "@/app/_client_components/ChessPieceCell";
import {ChessPieceType, Color} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {getArrayCord, getCords, getPosition} from "./ChessUtil";

let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfoProp}: any) {
    let [chessboard, setChessboard] = useState(gameInfoProp.chessboard);
    let chessPieces = printChessBoard();
    let allowedPositions: any;

    async function onCellClick(position: number) {
        let validMoves = await gameService.getValidMoves(getPosition(position));

        allowedPositions = new Set();

        for (let i = 0; i < validMoves.length; i++) {
            let current = [validMoves[i].row, validMoves[i].col];
            allowedPositions.add(getArrayCord(current));
            highlightPosition(allowedPositions)
        }
    }

    function highlightPosition(positionSet: any) {
        setChessboard(chessboard.map(
            (currentChessPiece: ChessPiece) => {
                if (positionSet.has(currentChessPiece.position)) {
                    return {
                        ...currentChessPiece,
                        isSelected: true
                    }
                } else {
                    return {
                        ...currentChessPiece,
                        isSelected: false
                    }
                }
            }
        ));
    }

    function setChessPiece(chessPiece: ChessPiece, position: number) {
        setChessboard(chessboard.map(
            (currentChessPiece: ChessPiece) => {
                if (currentChessPiece.position == position) {
                    return {
                        ...currentChessPiece,
                        isSelected: true,
                        type: ChessPieceType.Queen,
                        color: Color.White
                    }
                } else {
                    return currentChessPiece;
                }
            }
        ));
    }

    //1 2 3 4 5 6 7 8 9 10 .... 64  % 8 = col
    function printChessBoard() {
        let output = [];

        for (let position = chessboard.length - 1; position >= 0; position--) {
            output.push(<ChessPieceCell key={position}
                                        chessPiece={chessboard[position]}
                                        onCellClick={onCellClick}/>);
        }
        return output;
    }

    return (
        <div className="chessboard-grid">
            {chessPieces}
        </div>
    );
}
