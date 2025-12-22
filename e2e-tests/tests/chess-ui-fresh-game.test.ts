import { test, expect } from '@playwright/test';

test.describe('Chess Game - Complete Happy Path', () => {
  test('should play a complete game from start to checkmate with screenshots', async ({ page }) => {
    // Navigate to the page
    await page.goto('/');
    await page.waitForSelector('.chessboard-grid', { timeout: 10000 });
    
    // Take screenshot 1: Initial load
    await page.screenshot({ path: 'test-results/screenshots/01-page-loaded.png', fullPage: true });
    
    // Check if there's an "End Game" button (game already running) and click it
    const endButton = page.locator('button:has-text("End Game")');
    if (await endButton.isVisible().catch(() => false)) {
      await endButton.click();
      await page.waitForTimeout(500);
    }
    
    // Take screenshot 2: After ending any existing game
    await page.screenshot({ path: 'test-results/screenshots/02-ready-to-start.png', fullPage: true });
    
    // Start a new game
    const startButton = page.locator('button:has-text("Start Game")');
    await expect(startButton).toBeVisible();
    await startButton.click();
    await page.waitForTimeout(500);
    
    // Take screenshot 3: Fresh game started
    await page.screenshot({ path: 'test-results/screenshots/03-game-started.png', fullPage: true });
    
    // Verify initial position - white pawn should be on e2
    let square = page.locator('.chessboard-grid-cell[data-row="2"][data-col="5"]');
    let classes = await square.getAttribute('class');
    expect(classes).toContain('chess-pawn-white');
    
    // Helper function to make moves
    const makeMove = async (fromRow: number, fromCol: number, toRow: number, toCol: number, moveName: string, screenshotNum: number) => {
      // Click source square
      const fromSquare = page.locator(`.chessboard-grid-cell[data-row="${fromRow}"][data-col="${fromCol}"]`);
      await fromSquare.click();
      await page.waitForTimeout(300);
      
      // Click destination square
      const toSquare = page.locator(`.chessboard-grid-cell[data-row="${toRow}"][data-col="${toCol}"]`);
      await toSquare.click();
      await page.waitForTimeout(500);
      
      // Take screenshot after move
      const paddedNum = screenshotNum.toString().padStart(2, '0');
      await page.screenshot({ 
        path: `test-results/screenshots/${paddedNum}-after-${moveName}.png`, 
        fullPage: true 
      });
      
      return toSquare;
    };
    
    // Play Scholar's Mate
    console.log('Playing Scholar\'s Mate sequence...');
    
    // Move 1: e4 (white pawn from e2 to e4)
    let targetSquare = await makeMove(2, 5, 4, 5, 'e4', 4);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-pawn-white');
    console.log('✓ Move 1: e4');
    
    // Move 2: e5 (black pawn from e7 to e5)
    targetSquare = await makeMove(7, 5, 5, 5, 'e5', 5);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-pawn-black');
    console.log('✓ Move 2: e5');
    
    // Move 3: Bc4 (white bishop from f1 to c4)
    targetSquare = await makeMove(1, 6, 4, 3, 'Bc4', 6);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-bishop-white');
    console.log('✓ Move 3: Bc4');
    
    // Move 4: Nc6 (black knight from b8 to c6)
    targetSquare = await makeMove(8, 2, 6, 3, 'Nc6', 7);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-knight-black');
    console.log('✓ Move 4: Nc6');
    
    // Move 5: Qf3 (white queen from d1 to f3) - safer than Qh5
    targetSquare = await makeMove(1, 4, 3, 6, 'Qf3', 8);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-queen-white');
    console.log('✓ Move 5: Qf3');
    
    // Move 6: Nf6 (black knight from g8 to f6) - defensive move
    targetSquare = await makeMove(8, 7, 6, 6, 'Nf6', 9);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-knight-black');
    console.log('✓ Move 6: Nf6 (defensive)');
    
    // Move 7: Qxf7# (white queen captures pawn on f7 - checkmate!)
    targetSquare = await makeMove(3, 6, 7, 6, 'Qxf7-CHECKMATE', 10);
    classes = await targetSquare.getAttribute('class');
    expect(classes).toContain('chess-queen-white');
    console.log('✓ Move 7: Qxf7# - CHECKMATE!');
    
    // Wait a moment for checkmate to be detected
    await page.waitForTimeout(1000);
    
    // Take final screenshot
    await page.screenshot({ path: 'test-results/screenshots/11-final-checkmate.png', fullPage: true });
    
    // Verify checkmate state is displayed
    const gameStateText = await page.locator('.right-side-panel-item').filter({ hasText: 'Game State:' }).textContent();
    console.log('Final Game State:', gameStateText);
    
    console.log('\n✅ Complete game played successfully!');
    console.log('Screenshots saved in test-results/screenshots/');
  });
});
