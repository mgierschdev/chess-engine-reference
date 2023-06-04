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

        console.log("board");
        console.log(board);

        for (let row = 0; row < board.length; row++) {
            for (let col = 0; col < board[0].length; col++) {
                let current = board[row][col];
                console.log(current.Type);
                output.push(
                    <div key={col + "-" + row}>{board[row][col].Type +" "} - {board[row][col].Color}</div>
                );
            }
        }

        return output;
    }

    return (
        <div>
            {/*// className="chess-board-background">*/}
            {chessPieces}
        </div>
    );
}