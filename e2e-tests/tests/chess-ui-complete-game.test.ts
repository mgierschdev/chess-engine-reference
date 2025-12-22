import { test, expect } from '@playwright/test';

test.describe('Chess Game Complete Flow with Screenshots', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.chessboard-grid', { timeout: 10000 });
  });

  test('should play a complete game with visual verification', async ({ page }) => {
    // Take screenshot of initial state
    await page.screenshot({ path: 'test-results/01-initial-state.png', fullPage: true });
    
    // Start game
    const startButton = page.locator('button:has-text("Start Game")');
    const isStartButtonVisible = await startButton.isVisible().catch(() => false);
    
    if (isStartButtonVisible) {
      await startButton.click();
      await page.waitForTimeout(500);
    }
    
    // Screenshot after game started
    await page.screenshot({ path: 'test-results/02-game-started.png', fullPage: true });
    
    // Helper function to click square and take screenshot
    const clickSquare = async (row: number, col: number, moveName: string) => {
      const squareLocator = page.locator(`.chessboard-grid-cell[data-row="${row}"][data-col="${col}"]`);
      await squareLocator.click();
      await page.waitForTimeout(300);
    };
    
    const makeMove = async (fromRow: number, fromCol: number, toRow: number, toCol: number, moveName: string, screenshotNum: string) => {
      await clickSquare(fromRow, fromCol, moveName);
      await clickSquare(toRow, toCol, moveName);
      await page.waitForTimeout(500);
      await page.screenshot({ path: `test-results/${screenshotNum}-${moveName}.png`, fullPage: true });
    };
    
    // Play a complete game (Scholar's Mate)
    // Move 1: e4
    await makeMove(2, 5, 4, 5, 'e4', '03');
    
    // Verify e4 has white pawn
    let square = page.locator('.chessboard-grid-cell[data-row="4"][data-col="5"]');
    let classes = await square.getAttribute('class');
    expect(classes).toContain('chess-pawn-white');
    
    // Move 2: e5
    await makeMove(7, 5, 5, 5, 'e5', '04');
    
    // Verify e5 has black pawn
    square = page.locator('.chessboard-grid-cell[data-row="5"][data-col="5"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-pawn-black');
    
    // Move 3: Bc4
    await makeMove(1, 6, 4, 3, 'Bc4', '05');
    
    // Verify c4 has white bishop
    square = page.locator('.chessboard-grid-cell[data-row="4"][data-col="3"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-bishop-white');
    
    // Move 4: Nc6
    await makeMove(8, 2, 6, 3, 'Nc6', '06');
    
    // Verify c6 has black knight
    square = page.locator('.chessboard-grid-cell[data-row="6"][data-col="3"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-knight-black');
    
    // Move 5: Qh5
    await makeMove(1, 4, 5, 8, 'Qh5', '07');
    
    // Verify h5 has white queen
    square = page.locator('.chessboard-grid-cell[data-row="5"][data-col="8"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-queen-white');
    
    // Move 6: Nf6
    await makeMove(8, 7, 6, 6, 'Nf6', '08');
    
    // Verify f6 has black knight
    square = page.locator('.chessboard-grid-cell[data-row="6"][data-col="6"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-knight-black');
    
    // Move 7: Qxf7# (checkmate)
    await makeMove(5, 8, 7, 6, 'Qxf7-checkmate', '09');
    
    // Verify f7 has white queen
    square = page.locator('.chessboard-grid-cell[data-row="7"][data-col="6"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-queen-white');
    
    // Final screenshot
    await page.waitForTimeout(1000);
    await page.screenshot({ path: 'test-results/10-final-checkmate.png', fullPage: true });
    
    console.log('All moves verified successfully!');
  });
});
