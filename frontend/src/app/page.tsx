// noinspection JSUnusedGlobalSymbols

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_models/ChessGame";
import Chessboard from "@/app/_client_components/Chessboard";
import RightSidePanel from "@/app/_client_components/RightSidePanel";
import {metadata} from "@/app/layout";


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

                <div className="left-panel">
                    <Chessboard gameInfo={gameInfo}/>
                </div>

                <div className="right-panel">
                        <RightSidePanel gameInfoProp={gameInfo} />
                </div>

                <div className="footer"> {metadata.description} - {currentDate.getFullYear()}</div>
            </div>

        </main>
    )
}
