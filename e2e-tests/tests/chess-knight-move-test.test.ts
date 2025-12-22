import { test, expect } from '@playwright/test';

test.describe('Chess Knight Movement Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
    await page.waitForSelector('.chessboard-grid-cell');
  });

  test('should allow knight to move to valid empty square', async ({ page }) => {
    // Start a new game
    await page.click('text=Start Game');
    await page.waitForTimeout(500);

    // Take screenshot of initial position
    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/01-initial.png', fullPage: true });

    // Click white knight at b1 (position 2)
    const knight = page.locator('.chessboard-grid-cell[data-row="1"][data-col="2"]');
    await knight.click();
    await page.waitForTimeout(300);
    
    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/02-knight-selected.png', fullPage: true });

    // Click on c3 (position 19) - valid knight move
    const targetSquare = page.locator('.chessboard-grid-cell[data-row="3"][data-col="3"]');
    await targetSquare.click();
    await page.waitForTimeout(500);

    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/03-knight-moved.png', fullPage: true });

    // Verify knight is now at c3
    const movedKnight = page.locator('.chessboard-grid-cell[data-row="3"][data-col="3"] img[alt*="knight"]');
    await expect(movedKnight).toBeVisible();

    // Verify old square is empty
    const oldSquare = page.locator('.chessboard-grid-cell[data-row="1"][data-col="2"] img');
    await expect(oldSquare).not.toBeVisible();

    console.log('✅ Knight moved successfully from b1 to c3');
  });

  test('should allow multiple knight moves in a game', async ({ page }) => {
    // Start a new game
    await page.click('text=Start Game');
    await page.waitForTimeout(500);

    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/04-game-start.png', fullPage: true });

    // Move 1: White knight b1 to c3
    await page.locator('.chessboard-grid-cell[data-row="1"][data-col="2"]').click();
    await page.waitForTimeout(300);
    await page.locator('.chessboard-grid-cell[data-row="3"][data-col="3"]').click();
    await page.waitForTimeout(500);
    
    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/05-after-Nc3.png', fullPage: true });

    // Move 2: Black knight b8 to c6
    await page.locator('.chessboard-grid-cell[data-row="8"][data-col="2"]').click();
    await page.waitForTimeout(300);
    await page.locator('.chessboard-grid-cell[data-row="6"][data-col="3"]').click();
    await page.waitForTimeout(500);

    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/06-after-Nc6.png', fullPage: true });

    // Move 3: White knight g1 to f3
    await page.locator('.chessboard-grid-cell[data-row="1"][data-col="7"]').click();
    await page.waitForTimeout(300);
    await page.locator('.chessboard-grid-cell[data-row="3"][data-col="6"]').click();
    await page.waitForTimeout(500);

    await page.screenshot({ path: 'e2e-tests/test-results/knight-move/07-after-Nf3.png', fullPage: true });

    // Verify all three knights are in their new positions
    const whiteKnightC3 = page.locator('.chessboard-grid-cell[data-row="3"][data-col="3"] img[alt*="knight"]');
    const blackKnightC6 = page.locator('.chessboard-grid-cell[data-row="6"][data-col="3"] img[alt*="knight"]');
    const whiteKnightF3 = page.locator('.chessboard-grid-cell[data-row="3"][data-col="6"] img[alt*="knight"]');

    await expect(whiteKnightC3).toBeVisible();
    await expect(blackKnightC6).toBeVisible();
    await expect(whiteKnightF3).toBeVisible();

    console.log('✅ Multiple knight moves executed successfully');
  });
});
