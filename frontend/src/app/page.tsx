import React from 'react';
import RightSidePanel from "@/app/_client_components/RightSidePanel";
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_services/model/ChessGame";

export default async function Home() {

    let gameService: ChessService = new ChessService();
    let gameInfo: ChessGame = await gameService.getChessGame();

    return (
        <main>
            <div className="grid grid-rows-3 grid-flow-col">

                <div className="grid row-span-3 justify-end p-2">

                    <div
                        className="grid grid-rows-8 grid-cols-8 bg-white rounded-md drop-shadow-md justify-items-center chess-board-background">
                    </div>
                </div>

                <div className="grid row-span-2 box-container p-5">
                    <div className="left-side-panel grid-cols-1">
                        <RightSidePanel gameStarted={gameInfo} />
                    </div>
                </div>
            </div>

        </main>
    )
}
