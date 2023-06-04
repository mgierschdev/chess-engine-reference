'use client';

import React from 'react';
import {ChessService} from "@/app/_services/ChessService";

// Game Service
let gameService: ChessService = new ChessService();

export default function StartGameButton({gameStarted}: any) {

    let started = gameStarted;

    return (
        <div>
            {!started ?
                <button onClick={() => gameService.startGame()}>Start Game</button> :
                <button onClick={() => gameService.endGame()}>End Game</button>}
        </div>
    );
}
