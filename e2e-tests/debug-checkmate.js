const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  await page.goto('http://localhost:3000');
  await page.waitForTimeout(2000);
  
  // Start game
  const startButton = page.locator('button:has-text("Start Game")');
  if (await startButton.isVisible()) {
    await startButton.click();
    await page.waitForTimeout(500);
  }
  
  // Helper function to click square by row and column
  const clickSquare = async (row, col) => {
    const squareLocator = page.locator(`.chessboard-grid-cell[data-row="${row}"][data-col="${col}"]`);
    await squareLocator.click();
    await page.waitForTimeout(300);
  };

  // Scholar's Mate sequence
  await clickSquare(2, 5); // e2
  await clickSquare(4, 5); // e4
  await clickSquare(7, 5); // e7
  await clickSquare(5, 5); // e5
  await clickSquare(1, 6); // f1
  await clickSquare(4, 3); // c4
  await clickSquare(8, 2); // b8
  await clickSquare(6, 3); // c6
  await clickSquare(1, 4); // d1
  await clickSquare(5, 8); // h5
  await clickSquare(8, 7); // g8
  await clickSquare(6, 6); // f6
  await clickSquare(5, 8); // h5
  await clickSquare(7, 6); // f7 - checkmate!
  
  await page.waitForTimeout(1000);
  
  // Get the game state text
  const gameStateDiv = await page.locator('.right-side-panel-item').filter({ hasText: 'Game State:' }).textContent();
  console.log('Game State div:', gameStateDiv);
  
  const turnDiv = await page.locator('.right-side-panel-item').filter({ hasText: 'Turn:' }).textContent();
  console.log('Turn div:', turnDiv);
  
  // Take screenshot
  await page.screenshot({ path: 'checkmate-screenshot.png' });
  
  await browser.close();
  process.exit(0);
})();
