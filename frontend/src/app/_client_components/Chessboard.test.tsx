import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import Chessboard from './Chessboard';
import { ChessGame } from "@/app/_models/ChessGame";
import { Color, GameState, ChessPieceType } from "@/app/_models/enums";

describe('Chessboard Component', () => {
    test('renders empty board when game is not started', () => {
        const gameInfo: ChessGame = {
            id: 0,
            content: '',
            gameStarted: false,
            capturedWhite: [],
            capturedBlack: [],
            chessboard: [],
            turn: Color.None,
            gameState: GameState.Free
        };

        const { container } = render(<Chessboard gameInfo={gameInfo} />);
        
        // Verify component renders without crashing
        expect(container).toBeTruthy();
    });

    test('renders chessboard with pieces', () => {
        const gameInfo: ChessGame = {
            id: 1,
            content: '',
            gameStarted: true,
            capturedWhite: [],
            capturedBlack: [],
            chessboard: [
                { type: ChessPieceType.King, color: Color.White, position: { row: 1, col: 5 } },
                { type: ChessPieceType.King, color: Color.Black, position: { row: 8, col: 5 } },
            ],
            turn: Color.White,
            gameState: GameState.Free
        };

        const { container } = render(<Chessboard gameInfo={gameInfo} />);
        
        // Verify component renders with game state
        expect(container).toBeTruthy();
        
        // The component should render a table or grid for the chessboard
        // This is a basic smoke test to ensure no runtime errors
    });

    test('renders chessboard in checkmate state', () => {
        const gameInfo: ChessGame = {
            id: 1,
            content: '',
            gameStarted: true,
            capturedWhite: [],
            capturedBlack: [],
            chessboard: [
                { type: ChessPieceType.King, color: Color.White, position: { row: 1, col: 5 } },
                { type: ChessPieceType.King, color: Color.Black, position: { row: 8, col: 5 } },
            ],
            turn: Color.White,
            gameState: GameState.Checkmate
        };

        const { container } = render(<Chessboard gameInfo={gameInfo} />);
        
        // Verify component renders in checkmate state without crashing
        expect(container).toBeTruthy();
    });
});
