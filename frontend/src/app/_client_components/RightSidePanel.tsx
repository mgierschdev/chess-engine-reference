'use client';

import React, {useState, useEffect} from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_models/ChessGame";
import {GameState} from "@/app/_models/enums";
import MoveHistory from "./MoveHistory";
import CapturedPieces from "./CapturedPieces";

// Game Service
let gameService: ChessService = new ChessService();

export default function RightSidePanel({gameInfoProp, onBotModeChange, onGameInfoUpdate}: any) {

    const [gameInfo, setGameInfo] = useState(gameInfoProp);
    const [isBotMode, setIsBotMode] = useState(false);

    // Update local state when prop changes
    useEffect(() => {
        setGameInfo(gameInfoProp);
    }, [gameInfoProp]);

    async function startGame(): Promise<void> {
        let response: ChessGame;

        if (gameInfo.gameStarted) {
            response = await gameService.endGame();
        } else {
            response = await gameService.startGame();
        }
        setGameInfo(response);
        
        // Notify parent component of the update
        if (onGameInfoUpdate) {
            onGameInfoUpdate(response);
        }
    }

    function handleBotModeToggle() {
        const newBotMode = !isBotMode;
        setIsBotMode(newBotMode);
        if (onBotModeChange) {
            onBotModeChange(newBotMode);
        }
    }

    return (
        <div>
            {!gameInfo.gameStarted ?
                <button onClick={() => startGame()}>Start Game</button> :
                <button onClick={() => startGame()}>End Game</button>}
            
            <div className="right-side-panel-item">
                <label>
                    <input 
                        type="checkbox" 
                        checked={isBotMode}
                        onChange={handleBotModeToggle}
                        data-testid="bot-mode-toggle"
                    />
                    {' '}Play vs Computer
                </label>
            </div>

            <div
                className="right-side-panel-item">
                Game State: {gameInfo.gameState}
            </div>
            <div
                className="right-side-panel-item">
                Turn: {gameInfo.turn}
            </div>

            <div className="right-side-panel-item" style={{ marginTop: '20px' }}>
                <MoveHistory moves={gameInfo.moveHistory || []} />
            </div>

            <div className="right-side-panel-item" style={{ marginTop: '20px' }}>
                <strong>Captured Pieces</strong>
            </div>

            <div className="right-side-panel-item" style={{ marginTop: '10px' }}>
                <div style={{ marginBottom: '5px', fontSize: '14px', fontWeight: 'bold' }}>
                    Black Pieces:
                </div>
                <CapturedPieces pieces={gameInfo.capturedBlack || []} color="Black" />
            </div>

            <div className="right-side-panel-item" style={{ marginTop: '10px' }}>
                <div style={{ marginBottom: '5px', fontSize: '14px', fontWeight: 'bold' }}>
                    White Pieces:
                </div>
                <CapturedPieces pieces={gameInfo.capturedWhite || []} color="White" />
            </div>
        </div>
    );
}
