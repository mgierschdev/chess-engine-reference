'use client';

import React, {useState} from 'react';
import Chessboard from "@/app/_client_components/Chessboard";
import RightSidePanel from "@/app/_client_components/RightSidePanel";

export default function ChessGameWrapper({gameInfo}: any) {
    const [isBotMode, setIsBotMode] = useState(false);

    return (
        <>
            <div className="left-panel">
                <Chessboard gameInfo={gameInfo} isBotMode={isBotMode}/>
            </div>

            <div className="right-panel">
                <RightSidePanel gameInfoProp={gameInfo} onBotModeChange={setIsBotMode} />
            </div>
        </>
    );
}
