'use client';

import {ChessService} from "@/app/_services/ChessService";
import React, {useState} from "react";
import ChessPieceCell from "@/app/_client_components/ChessPieceCell";
import {ChessPieceType, Color} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {getCords} from "./ChessUtil";

// let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfoProp}: any) {
    let [chessboard, setChessboard] = useState(gameInfoProp.chessboard);
    let chessPieces = printChessBoard();

    function onCellClick(position: number) {

        console.log(getCords(position));

        setChessboard(chessboard.map(
            (chessPiece: ChessPiece) => {
                if (chessPiece.position == position) {
                    return {
                        ...chessPiece,
                        isSelected: true,
                        type: ChessPieceType.Queen,
                        color: Color.White
                    }
                } else {
                    return chessPiece;
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
