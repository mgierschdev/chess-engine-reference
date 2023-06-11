'use client';

import {ChessService} from "@/app/_services/ChessService";
import React, {useState} from "react";
import ChessPieceCell from "@/app/_client_components/ChessPieceCell";
import {ChessPieceType, Color} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {getArrayCord, getPlayerTurn, getPosition} from "./ChessUtil";
import {Position} from "@/app/_models/Position";

let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfoProp}: any) {
    let [chessboard, setChessboard] = useState(gameInfoProp.chessboard);
    let chessPieces = printChessBoard();
    let [allowedPositions, setAllowedPositions ]= useState(new Set());
    let [selectedPiece, setSelectedPiece] = useState(-1);
    let [playerTurn, setPlayerTurn] = useState(Color.White);

    async function onCellClick(position: number) {
        let clickedPosition = getPosition(position);
        let source = getPosition(selectedPiece);
        let validMoves: Position[] = [];
        let newValidPositions = new Set();

        // we are clicking at the same position as the valid
        if(allowedPositions.has(position)){
            let moved = await gameService.move(source, clickedPosition, playerTurn);

            if(moved){
                // we refresh chessboard
               // setChessboard(await gameService.getChessGame());
                setPlayerTurn(getPlayerTurn(playerTurn));
                setSelectedPiece(-1);

            }else {
                //console.log("could not move");
            }

        }else if(chessboard[position].type != ChessPieceType.Empty){
            validMoves = await gameService.getValidMoves(clickedPosition);
            setSelectedPiece(position);
        }else{
            // clicked an empty not available cell
            setSelectedPiece(-1);
        }

        // update the grid
        for (let i = 0; i < validMoves.length; i++) {
            let current = [validMoves[i].row, validMoves[i].col];
            newValidPositions.add(getArrayCord(current));
        }

        highlightPosition(newValidPositions);
        setAllowedPositions(newValidPositions);
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

    async function updateChessBoard(){
      //  console.log("updating chessboard");
        let update = await gameService.getChessGame();
        setChessboard(update);
       // highlightPosition(new Set());
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
