// Understanding position mapping based on ChessUtil.tsx

function getCords(position) {
    if (position > 64) {
        return [-1, -1];
    }

    let col = position == 8 ? 8 : position % 8;
    let row = Math.ceil(position / 8);
    let numberElementsTillRow = (row - 1) * 8;

    if (row > 1) {
        col = position - numberElementsTillRow;
    }

    // we invert the col position
    col = Math.abs(8 - col) + 1;

    return [row, col];
}

function getArrayCord(position) {
    //col , row
    let col = Math.abs(8 - position[1]) + 1;
    let row = position[0];
    return ((row - 1) * 8 ) + col;
}

// Chess notation mapping: a-h = columns 1-8, 1-8 = rows
const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];

console.log('Chess square to array position:');
console.log('================================');

// Test key squares
const testSquares = [
  ['a1', 1, 1],
  ['e2', 2, 5],
  ['e4', 4, 5],
  ['e7', 7, 5],
  ['b1', 1, 2],
  ['d3', 3, 4],
  ['h8', 8, 8],
  ['a8', 8, 1]
];

testSquares.forEach(([square, row, col]) => {
  const pos = getArrayCord([row, col]);
  const reversePos = 64 - pos + 1; // Since board is rendered in reverse
  console.log(`${square} (row=${row}, col=${col}) -> position=${pos}, reverse=${reversePos}`);
});

console.log('\nAll positions:');
for (let row = 8; row >= 1; row--) {
  let line = `Row ${row}: `;
  for (let col = 1; col <= 8; col++) {
    const pos = getArrayCord([row, col]);
    const file = files[col - 1];
    line += `${file}${row}=${pos} `;
  }
  console.log(line);
}
