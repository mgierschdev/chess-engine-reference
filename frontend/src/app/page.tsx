// noinspection JSUnusedGlobalSymbols

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_models/ChessGame";
import {metadata} from "@/app/layout";
import ChessGameWrapper from "@/app/_client_components/ChessGameWrapper";


export default async function Home() {
    let title: 'Chess';
    let description: 'Chess Engine';
    let gameService: ChessService = new ChessService();
    let gameInfo: ChessGame = await gameService.getChessGame();
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
