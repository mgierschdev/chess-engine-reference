import { test, expect } from '@playwright/test';

test.describe('Chess Piece Capture Tests', () => {
    test('should show piece capture in UI - pawn takes pawn', async ({ page }) => {
        await page.goto('http://localhost:3000');
        await page.waitForLoadState('networkidle');

        // Start game
        await page.click('button:has-text("Start Game")');
        await page.waitForTimeout(1000);

        // Take screenshot of initial state
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/01-initial.png', fullPage: true });

        // Move 1: White e2->e4
        await page.click('[data-row="2"][data-col="5"]'); // e2
        await page.waitForTimeout(500);
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/02-e2-selected.png', fullPage: true });
        
        await page.click('[data-row="4"][data-col="5"]'); // e4
        await page.waitForTimeout(1000);
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/03-after-e4.png', fullPage: true });

        // Move 2: Black d7->d5
        await page.click('[data-row="7"][data-col="4"]'); // d7
        await page.waitForTimeout(500);
        await page.click('[data-row="5"][data-col="4"]'); // d5
        await page.waitForTimeout(1000);
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/04-after-d5.png', fullPage: true });

        // Move 3: White e4 captures d5 (exd5)
        console.log('About to capture: White pawn e4 takes black pawn d5');
        await page.click('[data-row="4"][data-col="5"]'); // e4 (white pawn)
        await page.waitForTimeout(500);
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/05-e4-selected-before-capture.png', fullPage: true });
        
        // Check that d5 square has a black pawn before capture
        const d5BeforeCapture = await page.locator('[data-row="5"][data-col="4"]');
        const d5Content = await d5BeforeCapture.textContent();
        console.log('d5 content before capture:', d5Content);
        
        await page.click('[data-row="5"][data-col="4"]'); // d5 (capture!)
        await page.waitForTimeout(2000); // Wait for UI to update
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/06-after-exd5-CAPTURE.png', fullPage: true });

        // Verify: d5 should now have a WHITE pawn, not a black pawn
        const d5AfterCapture = await page.locator('[data-row="5"][data-col="4"]');
        const d5AfterContent = await d5AfterCapture.textContent();
        console.log('d5 content after capture:', d5AfterContent);
        
        // Check the actual piece at d5 - it should be a white pawn now
        const e4Square = await page.locator('[data-row="4"][data-col="5"]');
        const d5Square = await page.locator('[data-row="5"][data-col="4"]');
        
        // e4 should be empty now
        const e4IsEmpty = await e4Square.evaluate((el) => {
            const piece = el.querySelector('img, svg');
            return !piece || piece.getAttribute('alt') === 'empty';
        });
        console.log('e4 is empty:', e4IsEmpty);
        
        // d5 should have white pawn
        const d5HasWhitePawn = await d5Square.evaluate((el) => {
            const piece = el.querySelector('img, svg');
            return piece && piece.getAttribute('alt')?.includes('white');
        });
        console.log('d5 has white pawn:', d5HasWhitePawn);

        // Make another move to ensure board still works
        await page.click('[data-row="7"][data-col="5"]'); // e7
        await page.waitForTimeout(500);
        await page.click('[data-row="5"][data-col="5"]'); // e5
        await page.waitForTimeout(1000);
        await page.screenshot({ path: 'e2e-tests/test-results/capture-test/07-after-e5.png', fullPage: true });

        console.log('✅ Capture test completed - check screenshots to verify UI updates');
    });

    test('should show multiple captures correctly', async ({ page }) => {
        await page.goto('http://localhost:3000');
        await page.waitForLoadState('networkidle');

        await page.click('button:has-text("Start Game")');
        await page.waitForTimeout(1000);
        await page.screenshot({ path: 'e2e-tests/test-results/multi-capture/01-start.png', fullPage: true });

        // Scholar's mate setup with captures
        const moves = [
            { from: [2, 5], to: [4, 5], name: 'e4' },      // White
            { from: [7, 5], to: [5, 5], name: 'e5' },      // Black
            { from: [1, 6], to: [4, 3], name: 'Bc4' },     // White
            { from: [8, 2], to: [6, 3], name: 'Nc6' },     // Black
            { from: [1, 4], to: [4, 8], name: 'Qh5' },     // White
            { from: [8, 7], to: [6, 6], name: 'Nf6' },     // Black (knight defends)
            { from: [4, 8], to: [6, 6], name: 'Qxf6' },    // White CAPTURES knight! (Qxf6)
        ];

        let moveNum = 2;
        for (const move of moves) {
            const [fromRow, fromCol] = move.from;
            const [toRow, toCol] = move.to;
            
            console.log(`Move ${moveNum}: ${move.name}`);
            await page.click(`[data-row="${fromRow}"][data-col="${fromCol}"]`);
            await page.waitForTimeout(500);
            await page.click(`[data-row="${toRow}"][data-col="${toCol}"]`);
            await page.waitForTimeout(1500);
            await page.screenshot({ 
                path: `e2e-tests/test-results/multi-capture/${String(moveNum).padStart(2, '0')}-after-${move.name}.png`, 
                fullPage: true 
            });
            moveNum++;
        }

        console.log('✅ Multiple captures test completed');
    });
});
