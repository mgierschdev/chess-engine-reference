import { test, expect } from '@playwright/test';

test.describe('Move History and Capture Display Tests', () => {
    test('should display move history and show captures correctly', async ({ page }) => {
        // Navigate to the app
        await page.goto('http://localhost:3000');
        await page.waitForTimeout(1000);

        // Take screenshot of initial state
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/01-initial.png', fullPage: true });

        // Start a new game
        await page.click('button:has-text("Start Game")');
        await page.waitForTimeout(1000);
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/02-game-started.png', fullPage: true });

        // Verify Move History component is visible
        const moveHistoryVisible = await page.isVisible('text=Move History');
        expect(moveHistoryVisible).toBe(true);

        // Verify initially shows "No moves yet"
        const initialHistoryText = await page.textContent('.move-history');
        expect(initialHistoryText).toContain('No moves yet');

        // Move 1: e2 to e4 (White pawn)
        await page.click('.chessboard-grid-cell[data-row="6"][data-col="4"]');
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="4"][data-col="4"]');
        await page.waitForTimeout(1500);
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/03-after-e4.png', fullPage: true });

        // Verify move history shows e4
        let historyText = await page.textContent('.move-history');
        console.log('After e4:', historyText);
        expect(historyText).toContain('e4');
        expect(historyText).toContain('1.');

        // Move 2: e7 to e5 (Black pawn)
        await page.click('.chessboard-grid-cell[data-row="1"][data-col="4"]');
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="3"][data-col="4"]');
        await page.waitForTimeout(1500);
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/04-after-e5.png', fullPage: true });

        // Verify move history shows both moves
        historyText = await page.textContent('.move-history');
        console.log('After e5:', historyText);
        expect(historyText).toContain('e4');
        expect(historyText).toContain('e5');

        // Move 3: d2 to d4 (White pawn)
        await page.click('.chessboard-grid-cell[data-row="6"][data-col="3"]');
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="4"][data-col="3"]');
        await page.waitForTimeout(1500);
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/05-after-d4.png', fullPage: true });

        // Verify move history shows d4
        historyText = await page.textContent('.move-history');
        console.log('After d4:', historyText);
        expect(historyText).toContain('d4');
        expect(historyText).toContain('2.');

        // Move 4: e5 takes d4 (Black pawn captures White pawn - CRITICAL TEST)
        await page.click('.chessboard-grid-cell[data-row="3"][data-col="4"]');
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="4"][data-col="3"]');
        await page.waitForTimeout(2000); // Give extra time for state update
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/06-after-exd4-CAPTURE.png', fullPage: true });

        // Verify move history shows capture
        historyText = await page.textContent('.move-history');
        console.log('After exd4 capture:', historyText);
        expect(historyText).toContain('exd4'); // Should show as exd4 (pawn from e file captures on d4)
        
        // CRITICAL: Verify the captured pawn is no longer on the board
        // The white pawn should be gone from d4, replaced by black pawn
        const d4Cell = page.locator('.chessboard-grid-cell[data-row="4"][data-col="3"]');
        const d4HasBlackPawn = await d4Cell.evaluate((el) => {
            // Check if there's a black pawn image
            const img = el.querySelector('img');
            return img && img.src.includes('black') && img.src.includes('pawn');
        });
        expect(d4HasBlackPawn).toBe(true);

        // Verify the e5 square is now empty
        const e5Cell = page.locator('.chessboard-grid-cell[data-row="3"][data-col="4"]');
        const e5IsEmpty = await e5Cell.evaluate((el) => {
            const img = el.querySelector('img');
            return !img || img.src === '';
        });
        expect(e5IsEmpty).toBe(true);

        // Continue with more moves to ensure state stays consistent
        // Move 5: Nf3 (White knight)
        await page.click('.chessboard-grid-cell[data-row="7"][data-col="6"]');
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="5"][data-col="5"]');
        await page.waitForTimeout(1500);
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/07-after-Nf3.png', fullPage: true });

        // Verify move history
        historyText = await page.textContent('.move-history');
        console.log('After Nf3:', historyText);
        expect(historyText).toContain('Nf3');
        expect(historyText).toContain('3.');

        // Move 6: Nc6 (Black knight)
        await page.click('.chessboard-grid-cell[data-row="0"][data-col="1"]');
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="2"][data-col="2"]');
        await page.waitForTimeout(1500);
        await page.screenshot({ path: 'e2e-tests/test-results/move-history/08-after-Nc6.png', fullPage: true });

        historyText = await page.textContent('.move-history');
        console.log('After Nc6:', historyText);
        expect(historyText).toContain('Nc6');

        // Final verification: Count moves in history
        const moveRows = await page.locator('.move-history tbody tr').count();
        expect(moveRows).toBe(3); // 3 move pairs (6 half-moves)

        console.log('✅ All move history and capture tests passed!');
    });

    test('should handle multiple captures correctly', async ({ page }) => {
        await page.goto('http://localhost:3000');
        await page.waitForTimeout(1000);

        await page.click('button:has-text("Start Game")');
        await page.waitForTimeout(1000);

        // Play a sequence with captures
        const moves = [
            { from: [6, 4], to: [4, 4], name: 'e4', screenshot: '01-e4.png' },
            { from: [1, 4], to: [3, 4], name: 'e5', screenshot: '02-e5.png' },
            { from: [7, 5], to: [4, 2], name: 'Bc4', screenshot: '03-Bc4.png' },
            { from: [0, 1], to: [2, 2], name: 'Nc6', screenshot: '04-Nc6.png' },
            { from: [7, 3], to: [4, 6], name: 'Qh5', screenshot: '05-Qh5.png' },
            { from: [0, 6], to: [2, 5], name: 'Nf6', screenshot: '06-Nf6.png' },
        ];

        for (const move of moves) {
            await page.click(`.chessboard-grid-cell[data-row="${move.from[0]}"][data-col="${move.from[1]}"]`);
            await page.waitForTimeout(300);
            await page.click(`.chessboard-grid-cell[data-row="${move.to[0]}"][data-col="${move.to[1]}"]`);
            await page.waitForTimeout(1500);
            await page.screenshot({ path: `e2e-tests/test-results/multi-capture/${move.screenshot}`, fullPage: true });
        }

        // Now capture: Qxf7# (checkmate with capture)
        await page.click('.chessboard-grid-cell[data-row="4"][data-col="6"]'); // Queen at h5
        await page.waitForTimeout(300);
        await page.click('.chessboard-grid-cell[data-row="1"][data-col="5"]'); // Capture pawn at f7
        await page.waitForTimeout(2000);
        await page.screenshot({ path: 'e2e-tests/test-results/multi-capture/07-Qxf7-checkmate.png', fullPage: true });

        // Verify capture in move history
        const historyText = await page.textContent('.move-history');
        console.log('After Qxf7#:', historyText);
        expect(historyText).toContain('Qxf7'); // Should show Queen captures f7

        // Verify checkmate state
        const gameStateText = await page.textContent('text=Game State:');
        expect(gameStateText).toContain('Checkmate');

        console.log('✅ Multiple capture test passed!');
    });
});
