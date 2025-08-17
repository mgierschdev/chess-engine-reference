import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import RightSidePanel from './RightSidePanel';
import {ChessGame} from "@/app/_models/ChessGame";
import {Color, GameState} from "@/app/_models/enums";

test('renders finished game state', () => {
    const gameInfo: ChessGame = {
        id: 1,
        content: '',
        gameStarted: true,
        capturedWhite: [],
        capturedBlack: [],
        chessboard: [],
        turn: Color.White,
        gameState: GameState.Checkmate
    };

    render(<RightSidePanel gameInfoProp={gameInfo} />);
    expect(screen.getByText(/Checkmate/i)).toBeInTheDocument();
});

