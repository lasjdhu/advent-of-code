export function solveFirst(grid) {
  let splitCount = 0;

  let startCol = grid[0].indexOf('S');
  if (startCol === -1) return 0;

  let currentBeams = new Set([startCol]);

  for (let r = 0; r < grid.length; r++) {
    let nextBeams = new Set();
    const rowStr = grid[r];

    for (const col of currentBeams) {
      if (col < 0 || col >= rowStr.length) continue;

      const cell = rowStr[col];

      if (cell === '^') {
        splitCount++;

        nextBeams.add(col - 1);
        nextBeams.add(col + 1);
      } else {
        nextBeams.add(col);
      }
    }

    currentBeams = nextBeams;
    if (currentBeams.size === 0) break;
  }

  return splitCount;
}

export function solveSecond(grid) {
  const width = grid[0].length;
  let counts = new Array(width).fill(0);

  const startCol = grid[0].indexOf('S');
  if (startCol === -1) return 0;

  counts[startCol] = 1;

  for (let r = 0; r < grid.length; r++) {
    const nextCounts = new Array(width).fill(0);
    const rowStr = grid[r];

    for (let c = 0; c < width; c++) {
      const activeTimelines = counts[c];
      if (activeTimelines === 0) continue;
      const cell = rowStr[c] || '.';

      if (cell === '^') {
        if (c - 1 >= 0) {
          nextCounts[c - 1] += activeTimelines;
        }

        if (c + 1 < width) {
          nextCounts[c + 1] += activeTimelines;
        }
      } else {
        nextCounts[c] += activeTimelines;
      }
    }

    counts = nextCounts;
  }

  return counts.reduce((sum, val) => sum + val, 0);
}

