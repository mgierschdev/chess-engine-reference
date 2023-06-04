// noinspection JSUnusedGlobalSymbols

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_services/model/ChessGame";
import Chessboard from "@/app/_client_components/Chessboard";
import RightSidePanel from "@/app/_client_components/RightSidePanel";

export default async function Home() {

    let gameService: ChessService = new ChessService();
    let gameInfo: ChessGame = await gameService.getChessGame();
    let currentDate = new Date();


    return (
        <main>
            <div className="main">
                <div className="header">Header</div>

                <div className="left-panel">
                    <Chessboard gameInfoProp={gameInfo}/>
                </div>

                <div className="right-panel">
                        <RightSidePanel gameInfoProp={gameInfo} />
                </div>

                <div className="footer">{currentDate.getUTCDate()}</div>
            </div>

        </main>
    )
}
