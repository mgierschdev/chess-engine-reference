const { chromium } = require('playwright');

(async () => {
  // Start services
  const { spawn } = require('child_process');
  
  const backend = spawn('sh', ['-c', 'cd ../backend && ./gradlew bootRun'], {
    detached: true,
    stdio: 'ignore'
  });
  backend.unref();
  
  const frontend = spawn('sh', ['-c', 'cd ../frontend && npm run dev'], {
    detached: true,
    stdio: 'ignore'
  });
  frontend.unref();
  
  // Wait for services to start
  await new Promise(resolve => setTimeout(resolve, 30000));
  
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  await page.goto('http://localhost:3000');
  await page.waitForTimeout(2000);
  
  // Take screenshot
  await page.screenshot({ path: 'ui-screenshot.png', fullPage: true });
  
  // Get HTML of chessboard
  const chessboardHTML = await page.locator('.chessboard-grid, [class*="chessboard"]').first().innerHTML();
  console.log('Chessboard HTML (first 2000 chars):');
  console.log(chessboardHTML.substring(0, 2000));
  
  // Get all cells
  const cells = await page.locator('.chessboard-grid-cell').all();
  console.log(`\nNumber of cells found: ${cells.length}`);
  
  if (cells.length > 0) {
    const firstCell = cells[0];
    const classes = await firstCell.getAttribute('class');
    console.log(`\nFirst cell classes: ${classes}`);
  }
  
  await browser.close();
  process.exit(0);
})();
