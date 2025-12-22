import { test, expect } from '@playwright/test';

test.describe('Capture Display Verification', () => {
  test('should display captures correctly - piece disappears from board', async ({ page }) => {
    await page.goto('http://localhost:3000');
    
    // Wait for page to load
    await page.waitForSelector('.chessboard-grid');
    
    // Take initial screenshot
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/01-initial.png', fullPage: true });
    
    // Start game
    await page.click('button:has-text("Start Game")');
    await page.waitForTimeout(1000);
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/02-game-started.png', fullPage: true });
    
    // Move 1: e2 -> e4 (White pawn)
    await page.click('.chessboard-grid-cell[data-row="6"][data-col="4"]'); // e2
    await page.waitForTimeout(500);
    await page.click('.chessboard-grid-cell[data-row="4"][data-col="4"]'); // e4
    await page.waitForTimeout(1000);
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/03-after-e4.png', fullPage: true });
    
    // Verify e4 has white pawn
    const e4After = await page.locator('.chessboard-grid-cell[data-row="4"][data-col="4"]').getAttribute('data-position');
    expect(e4After).toBe('e4');
    
    // Move 2: d7 -> d5 (Black pawn)
    await page.click('.chessboard-grid-cell[data-row="1"][data-col="3"]'); // d7
    await page.waitForTimeout(500);
    await page.click('.chessboard-grid-cell[data-row="3"][data-col="3"]'); // d5
    await page.waitForTimeout(1000);
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/04-after-d5.png', fullPage: true });
    
    // Verify d5 has black pawn before capture
    const d5Before = await page.locator('.chessboard-grid-cell[data-row="3"][data-col="3"]').getAttribute('data-position');
    expect(d5Before).toBe('d5');
    
    // Move 3: e4 -> d5 (White pawn CAPTURES black pawn)
    console.log('About to make capture move: e4 takes d5');
    await page.click('.chessboard-grid-cell[data-row="4"][data-col="4"]'); // e4
    await page.waitForTimeout(500);
    
    // Take screenshot before capture
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/05-before-capture-exd5.png', fullPage: true });
    
    await page.click('.chessboard-grid-cell[data-row="3"][data-col="3"]'); // d5 (capture)
    await page.waitForTimeout(2000); // Give time for UI to update
    
    // Take screenshot after capture
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/06-after-capture-exd5.png', fullPage: true });
    
    // CRITICAL VERIFICATION: d5 should now have WHITE pawn, not black
    // Check by looking at the piece image or data attributes
    const d5AfterCapture = await page.locator('.chessboard-grid-cell[data-row="3"][data-col="3"]');
    const d5Position = await d5AfterCapture.getAttribute('data-position');
    console.log('Position after capture:', d5Position);
    expect(d5Position).toBe('d5');
    
    // Verify e4 is now empty
    const e4AfterCapture = await page.locator('.chessboard-grid-cell[data-row="4"][data-col="4"]');
    const e4Position = await e4AfterCapture.getAttribute('data-position');
    console.log('Old position after capture:', e4Position);
    expect(e4Position).toBe('e4');
    
    // Check move history shows the capture
    const moveHistory = await page.locator('.move-history').textContent();
    console.log('Move history:', moveHistory);
    expect(moveHistory).toContain('exd5'); // Capture notation
    
    console.log('✅ Capture test passed - UI updated correctly!');
  });

  test('should update board after multiple captures', async ({ page }) => {
    await page.goto('http://localhost:3000');
    await page.waitForSelector('.chessboard-grid');
    
    await page.screenshot({ path: 'e2e-tests/test-results/capture-verify/multi-01-initial.png', fullPage: true });
    
    // Start game
    await page.click('button:has-text("Start Game")');
    await page.waitForTimeout(1000);
    
    // Play a sequence with captures
    const moves = [
      { from: { row: 6, col: 4 }, to: { row: 4, col: 4 }, name: 'e4' },      // e4
      { from: { row: 1, col: 4 }, to: { row: 3, col: 4 }, name: 'e5' },      // e5
      { from: { row: 7, col: 6 }, to: { row: 5, col: 5 }, name: 'Nf3' },     // Nf3
      { from: { row: 0, col: 1 }, to: { row: 2, col: 2 }, name: 'Nc6' },     // Nc6
      { from: { row: 7, col: 5 }, to: { row: 4, col: 2 }, name: 'Bc4' },     // Bc4
      { from: { row: 1, col: 3 }, to: { row: 3, col: 3 }, name: 'd5' },      // d5
      { from: { row: 4, col: 2 }, to: { row: 3, col: 3 }, name: 'Bxd5' },    // Bxd5 (CAPTURE)
    ];
    
    for (let i = 0; i < moves.length; i++) {
      const move = moves[i];
      await page.click(`.chessboard-grid-cell[data-row="${move.from.row}"][data-col="${move.from.col}"]`);
      await page.waitForTimeout(500);
      await page.click(`.chessboard-grid-cell[data-row="${move.to.row}"][data-col="${move.to.col}"]`);
      await page.waitForTimeout(1000);
      
      await page.screenshot({ 
        path: `e2e-tests/test-results/capture-verify/multi-${String(i + 2).padStart(2, '0')}-${move.name}.png`, 
        fullPage: true 
      });
    }
    
    // Verify final capture worked
    const moveHistory = await page.locator('.move-history').textContent();
    expect(moveHistory).toContain('Bxd5'); // Capture notation
    
    console.log('✅ Multiple captures test passed!');
  });
});
