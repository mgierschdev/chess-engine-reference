import React from 'react';
import {ChessPiece, Piece, Color} from "@/app/_private/model/ChessPiece";

let Api = "http://localhost:8080/";
// /startGame , /endGame , /move, /getBoard, /getTurn, /getCaptured

async function startGame() {
    const res = await fetch(Api+'startGame');
    // The return value is *not* serialized
    // You can return Date, Map, Set, etc.

    // Recommendation: handle errors
    if (!res.ok) {
        // This will activate the closest `error.js` Error Boundary
        throw new Error('Failed to fetch data');
    }

    console.log(res);

    return res.json();
}

export default async function Home() {

    const gameInfo = await startGame();

    const test ="adastest";

    return (
        <main>
            {gameInfo.id}
            {gameInfo.content}
            <div className="grid grid-rows-3 grid-flow-col">

                <div className="grid row-span-3 justify-end p-2">

                    <div
                        className="grid grid-rows-8 grid-cols-8 bg-white rounded-md drop-shadow-md justify-items-center chess-board-background">


                    </div>
                </div>

                <div className="grid row-span-2 box-container p-5">
                    <div className="left-side-panel grid-cols-1">

                        <button type="button"
                                className="button">
                            Start Game
                        </button>
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
                        {/*<div*/}
                        {/*    className="right-side-panel-item">*/}
                        {/*    Movement list*/}
                        {/*    <ul className="grid justify-center">*/}
                        {/*        <li>d13-d41</li>*/}
                        {/*        <li>d13-d41</li>*/}
                        {/*        <li>d13-d41</li>*/}
                        {/*        <li>d13-d41</li>*/}
                        {/*    </ul>*/}
                        {/*</div>*/}
                    </div>
                </div>
            </div>

        </main>
    )
}
