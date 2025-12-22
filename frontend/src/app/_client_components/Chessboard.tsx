'use client';

/**
 * Problem: Need to render an interactive 8x8 chessboard UI where users can select pieces,
 * see valid moves, make moves, and handle pawn promotion.
 * 
 * Goal: Provide a responsive chess UI that communicates with the backend API for move
 * validation and game state updates.
 * 
 * Approach: React client component using useState hooks to manage board state, selected piece,
 * valid move highlights, and turn. On cell click, fetches valid moves from backend or executes
 * move. Shows promotion modal when pawn reaches final rank. Re-fetches game state after each move.
 * 
 * Time: O(1) for rendering (64 cells), O(network) for API calls
 * Space: O(64) for board state array
 * 
 * Tags: react, chess-ui, interactive-board
 */

import {ChessService} from "@/app/_services/ChessService";
import React, {useState, useEffect} from "react";
import ChessPieceCell from "@/app/_client_components/ChessPieceCell";
import {ChessPieceType, Color, GameState} from "@/app/_models/enums";
import {ChessPiece} from "@/app/_models/ChessPiece";
import {getArrayCord, getPosition} from "./ChessUtil";
import {Position} from "@/app/_models/Position";
import PromotionModal from "@/app/_client_components/PromotionModal";

let gameService: ChessService = new ChessService();

export default function Chessboard({gameInfo, isBotMode, onGameInfoUpdate}: any) {
    let [chessboard, setChessboard] = useState(gameInfo?.chessboard || []);
    let [allowedPositions, setAllowedPositions ]= useState(new Set());
    let [selectedPiece, setSelectedPiece] = useState(-1);
    let [playerTurn, setPlayerTurn] = useState(gameInfo?.turn);
    let [gameState, setGameState] = useState(gameInfo?.gameState);
    let [showPromotion, setShowPromotion] = useState(false);
    let [promotionMove, setPromotionMove] = useState<{source: Position, target: Position} | null>(null);
    let [isComputerThinking, setIsComputerThinking] = useState(false);
    let chessPieces = printChessBoard();

    // Reset board state when gameInfo changes (e.g., when game ends/starts)
    useEffect(() => {
        if (gameInfo) {
            setChessboard(gameInfo.chessboard || []);
            setPlayerTurn(gameInfo.turn);
            setGameState(gameInfo.gameState);
            setSelectedPiece(-1);
            setAllowedPositions(new Set());
        }
    }, [gameInfo]);


    async function onCellClick(chessPiece: ChessPiece) {
        if(gameState === GameState.Checkmate || isComputerThinking){
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
                // Update the board state immediately after move
                await updateChessBoard();
                
                // Get the latest game info for bot mode check
                const latestGame = await gameService.getChessGame();
                
                // If bot mode and it's black's turn, make AI move
                if (isBotMode && latestGame.turn === Color.Black && latestGame.gameState !== GameState.Checkmate) {
                    await makeComputerMove();
                }
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
                await updateChessBoard();
                
                // Get the latest game info for bot mode check
                const latestGame = await gameService.getChessGame();
                
                // If bot mode and it's black's turn, make AI move
                if (isBotMode && latestGame.turn === Color.Black && latestGame.gameState !== GameState.Checkmate) {
                    await makeComputerMove();
                }
            }
        }
        setShowPromotion(false);
        setPromotionMove(null);
    }

    async function makeComputerMove() {
        setIsComputerThinking(true);
        
        // Small delay to show "thinking" state
        await new Promise(resolve => setTimeout(resolve, 500));
        
        try {
            const aiMove = await gameService.getAIMove();
            if (aiMove) {
                const moved = await gameService.move(aiMove.from, aiMove.to);
                if (moved) {
                    await updateChessBoard();
                }
            }
        } catch (error) {
            console.error("AI move failed:", error);
        } finally {
            setIsComputerThinking(false);
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
        
        // Notify parent component of the updated game state
        if (onGameInfoUpdate) {
            onGameInfoUpdate(game);
        }
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
            {isComputerThinking && (
                <div className="computer-thinking" data-testid="computer-thinking">
                    Computer is thinking...
                </div>
            )}
            <div className="chessboard-grid">
                {chessPieces}
            </div>
        </>
    );
}
