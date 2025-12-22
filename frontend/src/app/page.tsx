// noinspection JSUnusedGlobalSymbols

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_models/ChessGame";
import {metadata} from "@/app/layout";
import ChessGameWrapper from "@/app/_client_components/ChessGameWrapper";

// Force dynamic rendering to avoid build-time fetch issues
export const dynamic = 'force-dynamic';

export default async function Home() {
    let title: 'Chess';
    let description: 'Chess Engine';
    let gameService: ChessService = new ChessService();
    let gameInfo: ChessGame | null = null;
    
    try {
        gameInfo = await gameService.getChessGame();
    } catch (error) {
        console.error("Failed to fetch initial game state:", error);
        // gameInfo will remain null, client components will handle it
    }
    
    let currentDate = new Date();

    return (
        <main>
            <div className="main">
                <div className="header">{metadata.title}</div>

                <ChessGameWrapper gameInfo={gameInfo} />

                <div className="footer"> {metadata.description} - {currentDate.getFullYear()}</div>
            </div>

        </main>
    )
}
