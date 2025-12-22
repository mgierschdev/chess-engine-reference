'use client';

import React, {useState, useEffect} from 'react';
import Chessboard from "@/app/_client_components/Chessboard";
import RightSidePanel from "@/app/_client_components/RightSidePanel";
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_models/ChessGame";

const gameService = new ChessService();

export default function ChessGameWrapper({gameInfo: initialGameInfo}: {gameInfo: ChessGame | null}) {
    const [isBotMode, setIsBotMode] = useState(false);
    const [gameInfo, setGameInfo] = useState<ChessGame | null>(initialGameInfo);
    
    // If no initial game info, fetch it on the client side
    useEffect(() => {
        if (!gameInfo) {
            gameService.getChessGame().then(info => {
                if (info) {
                    setGameInfo(info);
                }
            }).catch(error => {
                console.error("Failed to fetch game info:", error);
            });
        }
    }, [gameInfo]);

    // Callback to update game info from child components
    const handleGameInfoUpdate = (newGameInfo: ChessGame) => {
        setGameInfo(newGameInfo);
    };
    
    // Show loading state if game info isn't available yet
    if (!gameInfo) {
        return (
            <>
                <div className="left-panel">
                    <div>Loading chess game...</div>
                </div>
                <div className="right-panel">
                    <div>Loading...</div>
                </div>
            </>
        );
    }

    return (
        <>
            <div className="left-panel">
                <Chessboard 
                    gameInfo={gameInfo} 
                    isBotMode={isBotMode}
                    onGameInfoUpdate={handleGameInfoUpdate}
                />
            </div>

            <div className="right-panel">
                <RightSidePanel 
                    gameInfoProp={gameInfo} 
                    onBotModeChange={setIsBotMode}
                    onGameInfoUpdate={handleGameInfoUpdate}
                />
            </div>
        </>
    );
}
