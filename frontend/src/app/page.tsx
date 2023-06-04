// noinspection JSUnusedGlobalSymbols

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_services/model/ChessGame";
import Chessboard from "@/app/_client_components/Chessboard";
import RightSidePanel from "@/app/_client_components/RightSidePanel";

export default async function Home() {

    let gameService: ChessService = new ChessService();
    let gameInfo: ChessGame = await gameService.getChessGame();

    return (
        <main>
            <div className="grid grid-rows-3 grid-flow-col">

                <div className="grid row-span-3 justify-end p-2">
                    <Chessboard gameInfoProp={gameInfo}/>
                </div>

                <div className="grid row-span-2 box-container p-5">
                    <div className="left-side-panel grid-cols-1">
                        <RightSidePanel gameInfoProp={gameInfo} />
                    </div>
                </div>
            </div>

        </main>
    )
}
