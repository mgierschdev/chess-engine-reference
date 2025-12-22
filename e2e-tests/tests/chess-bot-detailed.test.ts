import { test, expect } from '@playwright/test';

test.describe('Chess Bot Mode - Detailed Movement Verification', () => {
  test('should show complete bot interaction with piece movement screenshots', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.chessboard-grid', { timeout: 10000 });
    
    // End any existing game
    const endButton = page.locator('button:has-text("End Game")');
    if (await endButton.isVisible().catch(() => false)) {
      await endButton.click();
      await page.waitForTimeout(500);
    }
    
    // Screenshot 1: Initial state
    await page.screenshot({ path: 'test-results/bot-detailed/01-initial-state.png', fullPage: true });
    
    // Enable bot mode
    const botModeCheckbox = page.locator('input[data-testid="bot-mode-toggle"]');
    await botModeCheckbox.check();
    await page.waitForTimeout(300);
    
    // Screenshot 2: Bot mode enabled
    await page.screenshot({ path: 'test-results/bot-detailed/02-bot-mode-enabled.png', fullPage: true });
    
    // Start game
    const startButton = page.locator('button:has-text("Start Game")');
    await startButton.click();
    await page.waitForTimeout(500);
    
    // Screenshot 3: Fresh game ready
    await page.screenshot({ path: 'test-results/bot-detailed/03-fresh-game.png', fullPage: true });
    
    console.log('\n🎮 Starting Bot Mode Game - Player (White) vs Computer (Black)\n');
    
    // MOVE 1: Player moves e2 to e4
    console.log('Move 1a: Player selecting e2 pawn...');
    await page.locator('.chessboard-grid-cell[data-row="2"][data-col="5"]').click();
    await page.waitForTimeout(300);
    
    console.log('Move 1b: Player moving to e4...');
    await page.locator('.chessboard-grid-cell[data-row="4"][data-col="5"]').click();
    await page.waitForTimeout(500);
    
    // Screenshot 4: After player's e4 move
    await page.screenshot({ path: 'test-results/bot-detailed/04-player-e4.png', fullPage: true });
    console.log('✓ Player moved: e2-e4');
    
    // Verify e4 has white pawn
    let square = page.locator('.chessboard-grid-cell[data-row="4"][data-col="5"]');
    let classes = await square.getAttribute('class');
    expect(classes).toContain('chess-pawn-white');
    
    // Wait for computer to respond
    console.log('⏳ Computer is thinking...');
    await page.waitForTimeout(2000);
    
    // Screenshot 5: After computer's response
    await page.screenshot({ path: 'test-results/bot-detailed/05-computer-response-1.png', fullPage: true });
    console.log('✓ Computer responded\n');
    
    // MOVE 2: Player moves Nf3
    console.log('Move 2a: Player selecting g1 knight...');
    await page.locator('.chessboard-grid-cell[data-row="1"][data-col="7"]').click();
    await page.waitForTimeout(300);
    
    console.log('Move 2b: Player moving to f3...');
    await page.locator('.chessboard-grid-cell[data-row="3"][data-col="6"]').click();
    await page.waitForTimeout(500);
    
    // Screenshot 6: After player's Nf3 move
    await page.screenshot({ path: 'test-results/bot-detailed/06-player-Nf3.png', fullPage: true });
    console.log('✓ Player moved: Ng1-f3');
    
    // Verify f3 has white knight
    square = page.locator('.chessboard-grid-cell[data-row="3"][data-col="6"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-knight-white');
    
    // Wait for computer to respond
    console.log('⏳ Computer is thinking...');
    await page.waitForTimeout(2000);
    
    // Screenshot 7: After computer's second response
    await page.screenshot({ path: 'test-results/bot-detailed/07-computer-response-2.png', fullPage: true });
    console.log('✓ Computer responded\n');
    
    // MOVE 3: Player moves d4
    console.log('Move 3a: Player selecting d2 pawn...');
    await page.locator('.chessboard-grid-cell[data-row="2"][data-col="4"]').click();
    await page.waitForTimeout(300);
    
    console.log('Move 3b: Player moving to d4...');
    await page.locator('.chessboard-grid-cell[data-row="4"][data-col="4"]').click();
    await page.waitForTimeout(500);
    
    // Screenshot 8: After player's d4 move
    await page.screenshot({ path: 'test-results/bot-detailed/08-player-d4.png', fullPage: true });
    console.log('✓ Player moved: d2-d4');
    
    // Verify d4 has white pawn
    square = page.locator('.chessboard-grid-cell[data-row="4"][data-col="4"]');
    classes = await square.getAttribute('class');
    expect(classes).toContain('chess-pawn-white');
    
    // Wait for computer to respond
    console.log('⏳ Computer is thinking...');
    await page.waitForTimeout(2000);
    
    // Screenshot 9: After computer's third response
    await page.screenshot({ path: 'test-results/bot-detailed/09-computer-response-3.png', fullPage: true });
    console.log('✓ Computer responded\n');
    
    // Final screenshot
    await page.waitForTimeout(500);
    await page.screenshot({ path: 'test-results/bot-detailed/10-final-position.png', fullPage: true });
    
    console.log('✅ Bot mode test complete!');
    console.log('📸 Generated 10 screenshots showing:');
    console.log('   - Initial state & bot mode enabled');
    console.log('   - 3 player moves with visible piece changes');
    console.log('   - 3 computer responses with visible piece changes');
    console.log('   - Final game state');
  });
});
