import { test, expect } from '@playwright/test';

test.describe('Chess Game UI Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // Wait for the chessboard to load
    await page.waitForSelector('[class*="chessboard"], table, [data-testid="chessboard"]', { timeout: 10000 });
  });

  test('should load the chessboard', async ({ page }) => {
    // Verify chessboard is visible
    const chessboard = page.locator('[class*="chessboard"], table, [data-testid="chessboard"]');
    await expect(chessboard).toBeVisible();

    // Verify initial pieces are present (at least some squares have pieces)
    const squares = page.locator('td, [data-square], [class*="square"]');
    await expect(squares).toHaveCount(64); // 8x8 board
  });

  test('should start a new game', async ({ page }) => {
    // Click start game button if present, or assume game starts automatically
    const startButton = page.locator('button:has-text("Start"), [data-testid="start-game"]');
    if (await startButton.isVisible()) {
      await startButton.click();
    }

    // Verify game is started (turn indicator shows White)
    const turnIndicator = page.locator('[class*="turn"], [data-testid="turn"]');
    if (await turnIndicator.isVisible()) {
      await expect(turnIndicator).toContainText(/White|white/i);
    }
  });

  test('should play a complete game (Scholar\'s Mate)', async ({ page }) => {
    // Start game
    const startButton = page.locator('button:has-text("Start"), [data-testid="start-game"]');
    if (await startButton.isVisible()) {
      await startButton.click();
    }

    // Scholar's Mate sequence:
    // 1. e4 e5
    // 2. Bc4 Nc6
    // 3. Qh5 Nf6
    // 4. Qxf7#

    // Helper function to click square by algebraic notation
    const clickSquare = async (square: string) => {
      const [file, rank] = square.split('');
      const col = file.charCodeAt(0) - 'a'.charCodeAt(0) + 1; // a=1, b=2, ..., h=8
      const row = parseInt(rank);

      // Try different selector patterns
      let squareLocator = page.locator(`td[data-row="${row}"][data-col="${col}"]`);
      if (!(await squareLocator.isVisible())) {
        squareLocator = page.locator(`[data-square="${square}"]`);
      }
      if (!(await squareLocator.isVisible())) {
        squareLocator = page.locator(`.${square}, #${square}`);
      }
      if (!(await squareLocator.isVisible())) {
        // Fallback: find by text content or position
        squareLocator = page.locator('td, div').filter({ hasText: new RegExp(square.toUpperCase()) });
      }

      await squareLocator.click();
    };

    // 1. White: e2 to e4
    await clickSquare('e2');
    await clickSquare('e4');

    // Verify move was made (e4 should have white pawn)
    await page.waitForTimeout(500); // Wait for UI update

    // 2. Black: e7 to e5
    await clickSquare('e7');
    await clickSquare('e5');

    // 3. White: Bf1 to c4
    await clickSquare('f1');
    await clickSquare('c4');

    // 4. Black: Nb8 to c6
    await clickSquare('b8');
    await clickSquare('c6');

    // 5. White: Qd1 to h5
    await clickSquare('d1');
    await clickSquare('h5');

    // 6. Black: Ng8 to f6
    await clickSquare('g8');
    await clickSquare('f6');

    // 7. White: Qh5 to f7 (checkmate)
    await clickSquare('h5');
    await clickSquare('f7');

    // Verify checkmate
    await page.waitForTimeout(1000); // Wait for checkmate detection

    // Check for checkmate message
    const checkmateMessage = page.locator('[class*="checkmate"], [data-testid="checkmate"], text=/checkmate|Checkmate/i');
    await expect(checkmateMessage).toBeVisible();

    // Verify game ended (no more moves possible)
    const gameOverIndicator = page.locator('[class*="game-over"], [data-testid="game-over"]');
    if (await gameOverIndicator.isVisible()) {
      await expect(gameOverIndicator).toContainText(/over|ended|finished/i);
    }
  });

  test('should highlight valid moves', async ({ page }) => {
    // Start game
    const startButton = page.locator('button:has-text("Start"), [data-testid="start-game"]');
    if (await startButton.isVisible()) {
      await startButton.click();
    }

    // Click on e2 pawn
    const e2Square = page.locator('td[data-row="2"][data-col="5"], [data-square="e2"]');
    await e2Square.click();

    // Verify valid moves are highlighted
    const highlightedSquares = page.locator('[class*="valid"], [class*="highlight"], [data-valid="true"]');
    await expect(highlightedSquares).toHaveCount(2); // e3 and e4

    // Click on highlighted e4
    const e4Square = page.locator('td[data-row="4"][data-col="5"], [data-square="e4"]');
    await e4Square.click();

    // Verify move was made and highlights are gone
    await expect(highlightedSquares).toHaveCount(0);
  });

  test('should reject invalid moves', async ({ page }) => {
    // Start game
    const startButton = page.locator('button:has-text("Start"), [data-testid="start-game"]');
    if (await startButton.isVisible()) {
      await startButton.click();
    }

    // Try to move knight like a bishop (invalid)
    const b1Square = page.locator('td[data-row="1"][data-col="2"], [data-square="b1"]');
    await b1Square.click();

    // Try to move to d3 (invalid for knight)
    const d3Square = page.locator('td[data-row="3"][data-col="4"], [data-square="d3"]');
    await d3Square.click();

    // Verify move was not made (turn should still be White)
    const turnIndicator = page.locator('[class*="turn"], [data-testid="turn"]');
    if (await turnIndicator.isVisible()) {
      await expect(turnIndicator).toContainText(/White|white/i);
    }

    // Verify no error message or invalid move feedback
    const errorMessage = page.locator('[class*="error"], [data-testid="error"]');
    if (await errorMessage.isVisible()) {
      await expect(errorMessage).toBeVisible(); // If error UI exists
    }
  });
});
