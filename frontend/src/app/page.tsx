import React from 'react';


// function StartGameButton(started: any){
//     if(started){
//         return <button type="button" className="button">End Game</button>;
//     }else{
//         return <button type="button" className="button">Start Game</button>;
//     }
// }

export default async function Home() {

    // /startGame , /endGame , /move, /getBoard, /getTurn, /getCaptured
    //gameStarted
    //const gameStarted = await startGame("gameStarted");

    return (
        <main>
         {/*//   {gameStarted.content}*/}
            <div className="grid grid-rows-3 grid-flow-col">

                <div className="grid row-span-3 justify-end p-2">

                    <div
                        className="grid grid-rows-8 grid-cols-8 bg-white rounded-md drop-shadow-md justify-items-center chess-board-background">
                    </div>
                </div>

                <div className="grid row-span-2 box-container p-5">
                    <div className="left-side-panel grid-cols-1">

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
