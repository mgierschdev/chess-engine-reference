import { test, expect } from '@playwright/test';

test.describe('Chess UI Visual Verification', () => {
  test('complete game with visual screenshots', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.chessboard-grid', { timeout: 10000 });
    
    // End any existing game
    const endButton = page.locator('button:has-text("End Game")');
    if (await endButton.isVisible().catch(() => false)) {
      await endButton.click();
      await page.waitForTimeout(500);
    }
    await page.screenshot({ path: 'test-results/screenshots/01-initial.png', fullPage: true });
    
    // Start new game
    const startButton = page.locator('button:has-text("Start Game")');
    await startButton.click();
    await page.waitForTimeout(500);
    await page.screenshot({ path: 'test-results/screenshots/02-game-started.png', fullPage: true });
    
    const makeMove = async (fromRow: number, fromCol: number, toRow: number, toCol: number, name: string, num: number) => {
      await page.locator(`.chessboard-grid-cell[data-row="${fromRow}"][data-col="${fromCol}"]`).click();
      await page.waitForTimeout(300);
      await page.locator(`.chessboard-grid-cell[data-row="${toRow}"][data-col="${toCol}"]`).click();
      await page.waitForTimeout(500);
      await page.screenshot({ path: `test-results/screenshots/${num.toString().padStart(2, '0')}-${name}.png`, fullPage: true });
    };
    
    // Play moves
    await makeMove(2, 5, 4, 5, 'e4', 3);
    await makeMove(7, 5, 5, 5, 'e5', 4);
    await makeMove(1, 6, 4, 3, 'Bc4', 5);
    await makeMove(8, 2, 6, 3, 'Nc6', 6);
    await makeMove(1, 4, 3, 6, 'Qf3', 7);
    await makeMove(8, 7, 6, 6, 'Nf6', 8);
    await makeMove(3, 6, 7, 6, 'Qxf7-checkmate', 9);
    
    await page.waitForTimeout(1000);
    await page.screenshot({ path: 'test-results/screenshots/10-final.png', fullPage: true });
    
    console.log('✅ Game completed - check screenshots for visual verification');
  });
});
