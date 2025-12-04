const getAdjacents = (matrix, x, y) => {
  const adj = [];
  const height = matrix.length;
  const width = matrix[0].length;

  for (let dy = -1; dy <= 1; dy++) {
    for (let dx = -1; dx <= 1; dx++) {
      if (dx === 0 && dy === 0) continue;

      const ny = y + dy;
      const nx = x + dx;

      if (ny >= 0 && ny < height && nx >= 0 && nx < width) {
        adj.push(matrix[ny][nx]);
      }
    }
  }
  return adj;
};

export function solveFirst(lines) {
  let result = 0;
  const matrix = lines.map(line => line.split(""));

  for (let y = 0; y < matrix.length; y++) {
    for (let x = 0; x < matrix[0].length; x++) {
      const symbol = matrix[y][x];

      if (symbol !== ".") {
        const adj = getAdjacents(matrix, x, y)
        if (adj.filter(c => c === "@").length < 4) result++;
      }
    }
  }

  return result;
}

export function solveSecond(lines) {
  let result = 0;
  const matrix = lines.map(line => line.split(""));


  while (true) {
    const toRemove = [];

    for (let y = 0; y < matrix.length; y++) {
      for (let x = 0; x < matrix[0].length; x++) {
        const symbol = matrix[y][x];
        if (symbol === ".") continue;

        const adj = getAdjacents(matrix, x, y);
        if (adj.filter(c => c === "@").length < 4) {
          toRemove.push([x, y]);
        }
      }
    }

    if (toRemove.length === 0) break;

    for (const [x, y] of toRemove) {
      matrix[y][x] = ".";
      result++;
    }
  }

  return result;
}

