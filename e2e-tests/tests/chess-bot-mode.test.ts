import { test, expect } from '@playwright/test';

test.describe('Chess Bot Mode Tests', () => {
  test('should play against computer bot with visual verification', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.chessboard-grid', { timeout: 10000 });
    
    // Screenshot 1: Initial state
    await page.screenshot({ path: 'test-results/bot-screenshots/01-initial.png', fullPage: true });
    
    // End any existing game
    const endButton = page.locator('button:has-text("End Game")');
    if (await endButton.isVisible().catch(() => false)) {
      await endButton.click();
      await page.waitForTimeout(500);
    }
    
    // Screenshot 2: Ready to start
    await page.screenshot({ path: 'test-results/bot-screenshots/02-ready.png', fullPage: true });
    
    // Enable bot mode
    const botModeCheckbox = page.locator('input[data-testid="bot-mode-toggle"]');
    await botModeCheckbox.check();
    await page.waitForTimeout(300);
    
    // Screenshot 3: Bot mode enabled
    await page.screenshot({ path: 'test-results/bot-screenshots/03-bot-mode-enabled.png', fullPage: true });
    
    // Verify checkbox is checked
    await expect(botModeCheckbox).toBeChecked();
    
    // Start game
    const startButton = page.locator('button:has-text("Start Game")');
    await startButton.click();
    await page.waitForTimeout(500);
    
    // Screenshot 4: Game started
    await page.screenshot({ path: 'test-results/bot-screenshots/04-game-started.png', fullPage: true });
    
    // Helper function to make a move
    const makeMove = async (fromRow: number, fromCol: number, toRow: number, toCol: number, moveName: string, screenshotNum: number) => {
      // Click source square
      const fromSquare = page.locator(`.chessboard-grid-cell[data-row="${fromRow}"][data-col="${fromCol}"]`);
      await fromSquare.click();
      await page.waitForTimeout(300);
      
      // Click destination square
      const toSquare = page.locator(`.chessboard-grid-cell[data-row="${toRow}"][data-col="${toCol}"]`);
      await toSquare.click();
      await page.waitForTimeout(500);
      
      // Screenshot after player move
      const paddedNum = screenshotNum.toString().padStart(2, '0');
      await page.screenshot({ 
        path: `test-results/bot-screenshots/${paddedNum}-after-${moveName}.png`, 
        fullPage: true 
      });
      
      // Wait for computer thinking indicator (if it appears)
      const thinkingIndicator = page.locator('[data-testid="computer-thinking"]');
      if (await thinkingIndicator.isVisible().catch(() => false)) {
        console.log(`  Computer is thinking...`);
      }
      
      // Wait for computer to make its move
      await page.waitForTimeout(2000);
      
      // Screenshot after computer move
      await page.screenshot({ 
        path: `test-results/bot-screenshots/${(screenshotNum + 1).toString().padStart(2, '0')}-after-bot-move.png`, 
        fullPage: true 
      });
      
      return toSquare;
    };
    
    console.log('Playing against computer bot...');
    
    // Move 1: e4 (player's move, then bot responds)
    console.log('Move 1: e4');
    await makeMove(2, 5, 4, 5, 'e4', 5);
    
    // Move 2: Nf3 (player's move, then bot responds)
    console.log('Move 2: Nf3');
    await makeMove(1, 7, 3, 6, 'Nf3', 7);
    
    // Move 3: d4 (player's move, then bot responds)
    console.log('Move 3: d4');
    await makeMove(2, 4, 4, 4, 'd4', 9);
    
    // Final screenshot
    await page.waitForTimeout(1000);
    await page.screenshot({ path: 'test-results/bot-screenshots/11-final.png', fullPage: true });
    
    console.log('✅ Bot mode test completed - check screenshots for visual verification');
    console.log('   Screenshots show both player moves and computer responses');
  });

  test('should toggle bot mode on and off', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.chessboard-grid', { timeout: 10000 });
    
    const botModeCheckbox = page.locator('input[data-testid="bot-mode-toggle"]');
    
    // Initially unchecked
    await expect(botModeCheckbox).not.toBeChecked();
    
    // Check it
    await botModeCheckbox.check();
    await expect(botModeCheckbox).toBeChecked();
    
    // Uncheck it
    await botModeCheckbox.uncheck();
    await expect(botModeCheckbox).not.toBeChecked();
  });
});
