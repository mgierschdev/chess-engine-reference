import React from 'react';
import StartGameButton from "@/app/_client_components/StartGameButton";
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_services/model/ChessGame";

export default async function Home() {

    let gameService: ChessService = new ChessService();
    let gameInfo: ChessGame = await gameService.getChessGame();

    let test = "test";

    return (
        <main>
            {gameInfo.gameStarted}
            <div className="grid grid-rows-3 grid-flow-col">

                <div className="grid row-span-3 justify-end p-2">

                    <div
                        className="grid grid-rows-8 grid-cols-8 bg-white rounded-md drop-shadow-md justify-items-center chess-board-background">
                    </div>
                </div>

                <div className="grid row-span-2 box-container p-5">
                    <div className="left-side-panel grid-cols-1">

                        <StartGameButton
                            gameStarted={test}
                        />

                        {/*<button type="button" className="button">End Game</button>*/}
                        {/*<StartGameButton started={gameStarted.response}/>*/}

                        <div
                            className="right-side-panel-item">
                            Turn: White
                        </div>

                        <div
                            className="right-side-panel-item">
                            Black Pieces:
                        </div>

                        <div
                            className="right-side-panel-item">
                            White Pieces:
                        </div>
                    </div>
                </div>
            </div>

        </main>
    )
}
