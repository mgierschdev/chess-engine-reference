# Chess Bot Mode Documentation

## Overview

The chess application now supports two game modes:
1. **Human vs Human** - Two players take turns
2. **Human vs Computer** - Player (White) vs AI Bot (Black)

## How to Use

### Enabling Bot Mode

1. Navigate to the chess game
2. Locate the "Play vs Computer" checkbox on the right panel
3. Check the box to enable bot mode
4. Start a new game

### Playing Against the Bot

- When bot mode is enabled, you play as **White**
- The computer plays as **Black**
- After you make your move, the AI automatically calculates and plays its response
- You'll see a "Computer is thinking..." indicator while the AI computes its move
- The AI uses the backend `/aiMove` endpoint to calculate best moves

### Disabling Bot Mode

- Simply uncheck the "Play vs Computer" checkbox to return to human vs human mode

## Implementation Details

### Frontend Changes

1. **ChessService.tsx**
   - Added `getAIMove()` method that calls the backend `/aiMove` endpoint
   - Parses the AI response format: `fromRow,fromCol,toRow,toCol,score`

2. **RightSidePanel.tsx**
   - Added checkbox for toggling bot mode
   - Added `onBotModeChange` callback prop
   - Checkbox has `data-testid="bot-mode-toggle"` for testing

3. **Chessboard.tsx**
   - Added `isBotMode` prop
   - Added `isComputerThinking` state
   - Automatically triggers AI move after player's move when in bot mode
   - Displays "Computer is thinking..." indicator
   - Prevents player moves while computer is calculating

4. **ChessGameWrapper.tsx** (new)
   - Client component wrapper that manages bot mode state
   - Passes bot mode state to both Chessboard and RightSidePanel

5. **page.tsx**
   - Updated to use ChessGameWrapper instead of direct components

### Backend Integration

The bot mode uses the existing `/aiMove` endpoint:
- **GET** `/aiMove`
- Returns: `{id: number, content: string}` where content is `"row,col,row,col,score"`
- The AI analyzes the current game state and returns the best move

## Testing

### Automated Tests

**Bot Mode Tests** (`chess-bot-mode.test.ts`):
1. **Visual Verification Test** - Plays 3 moves against the bot with screenshots
   - Verifies bot mode can be enabled
   - Makes player moves and waits for AI responses
   - Captures screenshots after each player and computer move
   - Total of 11 screenshots showing complete interaction

2. **Toggle Test** - Verifies the checkbox works correctly
   - Checks/unchecks the bot mode toggle
   - Verifies state changes

### Manual Testing

1. Start the application
2. Enable "Play vs Computer"
3. Start a new game
4. Make a move (e.g., e2-e4)
5. Observe the AI's response
6. Continue playing to verify the game flow

## Visual Evidence

Screenshots from the automated test show:
- Bot mode checkbox enabled
- Player making moves
- "Computer is thinking..." indicator
- AI responses on the board
- Complete game progression

All screenshots are saved in `e2e-tests/test-results/bot-screenshots/`
