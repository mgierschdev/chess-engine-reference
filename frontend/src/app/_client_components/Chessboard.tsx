'use client';

import {ChessService} from "@/app/_services/ChessService";
import React, {useState} from "react";
import ChessPieceCell from "@/app/_client_components/ChessPieceCell";
import {ChessPieceType, Color, GameState} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {getArrayCord, getPosition} from "./ChessUtil";
import {Position} from "@/app/_models/Position";
import PromotionModal from "@/app/_client_components/PromotionModal";

let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfo}: any) {
    let [chessboard, setChessboard] = useState(gameInfo.chessboard);
    let chessPieces = printChessBoard();
    let [allowedPositions, setAllowedPositions ]= useState(new Set());
    let [selectedPiece, setSelectedPiece] = useState(-1);
    let [playerTurn, setPlayerTurn] = useState(gameInfo.turn);
    let [gameState, setGameState] = useState(gameInfo.gameState);
    let [showPromotion, setShowPromotion] = useState(false);
    let [promotionMove, setPromotionMove] = useState<{source: Position, target: Position} | null>(null);


    async function onCellClick(chessPiece: ChessPiece) {
        if(gameState === GameState.Checkmate){
            return;
        }
        let clickedPosition = getPosition(chessPiece.position);
        let source = getPosition(selectedPiece);
        let validMoves: Position[] = [];
        let newValidPositions = new Set();

        console.log(chessPiece);

        // we are clicking at the same position as the valid
        if(allowedPositions.has(chessPiece.position)){
            const movingPiece = chessboard.find((p: ChessPiece) => p.position === selectedPiece);
            if(movingPiece && movingPiece.type === ChessPieceType.Pawn){
                const promotionRank = movingPiece.color === Color.White ? 8 : 1;
                if(clickedPosition.row === promotionRank){
                    setPromotionMove({source: source, target: clickedPosition});
                    setShowPromotion(true);
                    return;
                }
            }

            let moved = await gameService.move(source, clickedPosition);

            if(moved){
                gameInfo = await gameService.getChessGame();
                await updateChessBoard();
                return;
            }else {
                //console.log("could not move");
            }

        }else if(chessPiece.type != ChessPieceType.Empty){
            validMoves = await gameService.getValidMoves(clickedPosition);
            setSelectedPiece(chessPiece.position);
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

    async function handlePromotion(type: ChessPieceType){
        if(promotionMove){
            let moved = await gameService.move(promotionMove.source, promotionMove.target, type);
            if(moved){
                gameInfo = await gameService.getChessGame();
                await updateChessBoard();
            }
        }
        setShowPromotion(false);
        setPromotionMove(null);
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
        let game = await gameService.getChessGame();
        setSelectedPiece(-1);
        setPlayerTurn(game.turn);
        setGameState(game.gameState);

        setChessboard(game.chessboard.map(
            (currentChessPiece: ChessPiece) => {
                return {
                    ...currentChessPiece,
                    isSelected: false
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
        <>
            {showPromotion && <PromotionModal onSelect={handlePromotion}/>}    
            <div className="chessboard-grid">
                {chessPieces}
            </div>
        </>
    );
}
