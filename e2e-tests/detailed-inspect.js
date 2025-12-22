const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  await page.goto('http://localhost:3000');
  await page.waitForTimeout(2000);
  
  // Get all cells
  const cells = await page.locator('.chessboard-grid-cell').all();
  console.log(`Total cells: ${cells.length}\n`);
  
  // Check first few cells and last few cells
  for (let i of [0, 1, 2, 7, 8, 16, 56, 57, 63]) {
    if (i < cells.length) {
      const cell = cells[i];
      const classes = await cell.getAttribute('class');
      const draggable = await cell.getAttribute('draggable');
      console.log(`Cell ${i}: classes="${classes}", draggable="${draggable}"`);
    }
  }
  
  // Check if there are any data attributes
  const firstCellAttrs = await page.locator('.chessboard-grid-cell').first().evaluate(el => {
    const attrs = {};
    for (let attr of el.attributes) {
      attrs[attr.name] = attr.value;
    }
    return attrs;
  });
  console.log('\nFirst cell attributes:', firstCellAttrs);
  
  await browser.close();
  process.exit(0);
})();
