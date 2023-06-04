'use client';

import {ChessService} from "@/app/_services/ChessService";
import React, {useState} from "react";
import ChessPieceCell from "@/app/_client_components/ChessPieceCell";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {ChessPieceType} from "@/app/_models/enums";

let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfoProp}: any) {

    const [gameInfo, setGameInfo] = useState(gameInfoProp);
    const [chessPieces, setChessPieces] = useState(printChessBoard);

    function printChessBoard() {
        let board = gameInfo.chessboard;
        let output = [];

        for (let row = board.length - 1; row >= 0; row--) {
            for (let col = 0; col < board[0].length; col++) {
                let current: ChessPiece = board[row][col];
                current.row = row;
                current.col = col;

                if (current.type != ChessPieceType.Empty) {
                    current.isDraggable = true;
                } else {
                    current.isDraggable = false;
                }

                output.push(<ChessPieceCell key={row + "-" + col} chessPiece={current}/>);
            }
        }

        return output;
    }

    return (
        <div className="chessboard-grid">
            {chessPieces}
        </div>
    );
}
