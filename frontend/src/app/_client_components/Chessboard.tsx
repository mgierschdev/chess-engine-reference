'use client';

import {ChessService} from "@/app/_services/ChessService";
import {useState} from "react";

let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfoProp}: any) {

    const [gameInfo, setGameInfo] = useState(gameInfoProp);
    const chessPieces = printChessBoard();

    function printChessBoard() {
        let board = gameInfo.chessboard;
        let output = [];

        for (let row = 0; row < board.length; row++) {
            for (let col = 0; col < board[0].length; col++) {
                let current = board[row][col];
                output.push(
                    <div className="chessboard-cell" key={col + "-" + row}>{board[row][col].type} - {board[row][col].color}</div>
                );
            }
        }

        return output;
    }

    return (
        <div className="chessBoard">
            {/*// className="chess-board-background">*/}
            {chessPieces}
        </div>
    );
}
