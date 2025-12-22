"use client";

import React from 'react';
import { Move } from '../_models/Move';
import { ChessPieceType } from '../_models/enums';
import { Position } from '../_models/Position';

interface MoveHistoryProps {
    moves: Move[];
}

const MoveHistory: React.FC<MoveHistoryProps> = ({ moves }) => {
    // Convert position to chess notation (e.g., {row: 4, col: 4} -> "e4")
    const positionToNotation = (pos: Position): string => {
        const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
        const ranks = ['8', '7', '6', '5', '4', '3', '2', '1'];
        return files[pos.col] + ranks[pos.row];
    };

    // Get piece symbol for display
    const getPieceSymbol = (pieceType: ChessPieceType): string => {
        switch (pieceType) {
            case ChessPieceType.King: return 'K';
            case ChessPieceType.Queen: return 'Q';
            case ChessPieceType.Rock: return 'R';
            case ChessPieceType.Bishop: return 'B';
            case ChessPieceType.Knight: return 'N';
            case ChessPieceType.Pawn: return '';
            default: return '';
        }
    };

    // Format move to algebraic notation (simplified)
    const formatMove = (move: Move): string => {
        const pieceSymbol = getPieceSymbol(move.piece.type);
        const from = positionToNotation(move.from);
        const to = positionToNotation(move.to);
        const isCapture = move.capturedPiece && move.capturedPiece.type !== ChessPieceType.Empty;
        const captureSymbol = isCapture ? 'x' : '';
        
        // For pawns captures, include the file they came from
        if (move.piece.type === ChessPieceType.Pawn && isCapture) {
            return `${from[0]}${captureSymbol}${to}`;
        }
        
        return `${pieceSymbol}${captureSymbol}${to}`;
    };

    // Group moves into pairs (White and Black)
    const movePairs: Array<{white?: Move; black?: Move; number: number}> = [];
    for (let i = 0; i < moves.length; i += 2) {
        movePairs.push({
            white: moves[i],
            black: moves[i + 1],
            number: (i / 2) + 1
        });
    }

    return (
        <div className="move-history" style={{
            width: '100%',
            maxHeight: '400px',
            overflowY: 'auto',
            border: '1px solid #ccc',
            borderRadius: '4px',
            padding: '10px',
            backgroundColor: '#f9f9f9',
            fontFamily: 'monospace',
            fontSize: '14px'
        }}>
            <div style={{ fontWeight: 'bold', marginBottom: '10px', color: '#333' }}>
                Move History
            </div>
            {movePairs.length === 0 ? (
                <div style={{ color: '#999', fontStyle: 'italic' }}>No moves yet</div>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid #ddd' }}>
                            <th style={{ textAlign: 'left', padding: '5px', width: '15%' }}>#</th>
                            <th style={{ textAlign: 'left', padding: '5px', width: '42.5%' }}>White</th>
                            <th style={{ textAlign: 'left', padding: '5px', width: '42.5%' }}>Black</th>
                        </tr>
                    </thead>
                    <tbody>
                        {movePairs.map((pair, index) => (
                            <tr key={index} style={{ 
                                backgroundColor: index % 2 === 0 ? 'white' : '#f5f5f5',
                                borderBottom: '1px solid #eee'
                            }}>
                                <td style={{ padding: '5px', color: '#666' }}>{pair.number}.</td>
                                <td style={{ padding: '5px', color: '#000' }}>
                                    {pair.white ? formatMove(pair.white) : ''}
                                </td>
                                <td style={{ padding: '5px', color: '#000' }}>
                                    {pair.black ? formatMove(pair.black) : ''}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default MoveHistory;
