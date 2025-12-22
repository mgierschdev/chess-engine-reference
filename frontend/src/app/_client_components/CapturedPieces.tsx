'use client';

import React from 'react';
import {ChessPiece} from "@/app/_models/ChessPiece";

interface CapturedPiecesProps {
    pieces: ChessPiece[];
    color: 'White' | 'Black';
}

export default function CapturedPieces({pieces, color}: CapturedPiecesProps) {
    if (!pieces || pieces.length === 0) {
        return <div style={{ fontSize: '14px', color: '#666' }}>None</div>;
    }

    // Count pieces by type
    const pieceCounts: { [key: string]: number } = {};
    pieces.forEach(piece => {
        if (piece.type && piece.type !== 'Empty' && piece.type !== 'Invalid') {
            pieceCounts[piece.type] = (pieceCounts[piece.type] || 0) + 1;
        }
    });

    // Map piece types to symbols (Unicode chess pieces)
    const pieceSymbols: { [key: string]: string } = {
        'Pawn': color === 'White' ? '♙' : '♟',
        'Knight': color === 'White' ? '♘' : '♞',
        'Bishop': color === 'White' ? '♗' : '♝',
        'Rook': color === 'White' ? '♖' : '♜',
        'Queen': color === 'White' ? '♕' : '♛',
        'King': color === 'White' ? '♔' : '♚'
    };

    return (
        <div style={{ 
            display: 'flex', 
            flexWrap: 'wrap', 
            gap: '4px',
            fontSize: '24px',
            minHeight: '30px',
            alignItems: 'center'
        }}>
            {Object.entries(pieceCounts).map(([type, count]) => (
                <div key={type} style={{ display: 'flex', alignItems: 'center' }}>
                    {Array.from({ length: count }).map((_, index) => (
                        <span 
                            key={`${type}-${index}`}
                            style={{ 
                                marginRight: '2px',
                                filter: color === 'White' ? 'invert(1)' : 'none'
                            }}
                        >
                            {pieceSymbols[type] || ''}
                        </span>
                    ))}
                </div>
            ))}
        </div>
    );
}
