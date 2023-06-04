'use client';

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";
import {ChessGame} from "@/app/_services/model/ChessGame";

// Game Service
let gameService: ChessService = new ChessService();



export default function StartGameButton({gameStarted}: any) {

    let started = gameStarted;

    async function startGame(): Promise<void> {
        let response: ChessGame;
        if (started) {
            response = await gameService.endGame();
        }else{
            response = await gameService.startGame();
        }
        started = response.gameStarted;
    }

    return (
        <div>
            {!started ?
                <button onClick={() => startGame()}>Start Game</button> :
                <button onClick={() => startGame()}>End Game</button>}
        </div>
    );
}
