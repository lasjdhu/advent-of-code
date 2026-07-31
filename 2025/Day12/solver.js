export function solveFirst(lines) {
  const { shapes, queries } = parseInput(lines);
  const shapeVariations = precomputeVariations(shapes);

  let validRegions = 0;
  for (const query of queries) {
    if (solveHybrid(query, shapeVariations)) {
      validRegions++;
    }
  }
  return validRegions;
}

function solveHybrid(query, variations) {
  const { w, h, counts } = query;

  let totalAreaNeeded = 0;
  let totalCount = 0;

  counts.forEach((qty, id) => {
    if (qty > 0) {
      const shapeArea = variations[id][0].size;
      totalAreaNeeded += shapeArea * qty;
      totalCount += qty;
    }
  });

  const regionArea = w * h;

  if (totalAreaNeeded > regionArea) {
    return false;
  }

  const slotsW = Math.floor(w / 3);
  const slotsH = Math.floor(h / 3);
  const capacity = slotsW * slotsH;

  if (capacity >= totalCount) {
    const allSmallEnough = counts.every((qty, id) => {
      if (qty === 0) return true;
      const v = variations[id][0];
      return v.h <= 3 && v.w <= 3;
    });

    if (allSmallEnough) {
      return true;
    }
  }

  return solveExact(query, variations, totalAreaNeeded, regionArea);
}

function solveExact(query, allShapeVariations, totalPieceArea, totalCells) {
  const { w, h, counts } = query;

  const relevantShapes = [];
  counts.forEach((qty, id) => {
    if (qty > 0) {
      const vars = allShapeVariations[id];
      if (vars && vars.length > 0) {
        relevantShapes.push({ id, qty, vars, size: vars[0].size });
      }
    }
  });

  relevantShapes.sort((a, b) => b.size - a.size);

  const maxSlack = totalCells - totalPieceArea;
  const quantities = new Int8Array(relevantShapes.length);
  relevantShapes.forEach((s, i) => (quantities[i] = s.qty));

  const movesAtCell = precomputeMoves(w, h, relevantShapes);

  const minPieceSize =
    relevantShapes.length > 0
      ? relevantShapes[relevantShapes.length - 1].size
      : 0;

  return backtrack(
    0n,
    quantities,
    movesAtCell,
    w * h,
    relevantShapes,
    w,
    maxSlack,
    minPieceSize,
  );
}

function precomputeMoves(w, h, relevantShapes) {
  const totalCells = w * h;
  const movesAtCell = Array.from({ length: totalCells }, () => []);

  for (let i = 0; i < relevantShapes.length; i++) {
    const { vars } = relevantShapes[i];
    for (const variant of vars) {
      const { coords } = variant;

      let anchorR = Infinity,
        anchorC = Infinity;
      for (const [r, c] of coords) {
        if (r < anchorR || (r === anchorR && c < anchorC)) {
          anchorR = r;
          anchorC = c;
        }
      }

      for (let tr = 0; tr < h; tr++) {
        for (let tc = 0; tc < w; tc++) {
          const dR = tr - anchorR;
          const dC = tc - anchorC;
          let valid = true;
          let mask = 0n;

          for (const [pr, pc] of coords) {
            const nr = pr + dR;
            const nc = pc + dC;
            if (nr < 0 || nr >= h || nc < 0 || nc >= w) {
              valid = false;
              break;
            }
            mask |= 1n << BigInt(nr * w + nc);
          }

          if (valid) {
            movesAtCell[tr * w + tc].push({ mask, shapeIdx: i });
          }
        }
      }
    }
  }

  movesAtCell.forEach((list) =>
    list.sort(
      (a, b) =>
        relevantShapes[b.shapeIdx].size - relevantShapes[a.shapeIdx].size,
    ),
  );

  return movesAtCell;
}

function backtrack(
  gridMask,
  quantities,
  movesAtCell,
  totalCells,
  relevantShapes,
  width,
  currentSlack,
  minPieceSize,
) {
  let firstEmpty = -1;
  for (let i = 0; i < totalCells; i++) {
    if ((gridMask & (1n << BigInt(i))) === 0n) {
      firstEmpty = i;
      break;
    }
  }

  if (firstEmpty === -1) {
    return quantities.every((q) => q === 0);
  }

  let hasPieces = false;
  for (let q of quantities) if (q > 0) hasPieces = true;
  if (!hasPieces) return true;

  if (
    checkGlobalFragmentation(
      gridMask,
      totalCells,
      width,
      minPieceSize,
      currentSlack,
    )
  ) {
    return false;
  }

  const possibleMoves = movesAtCell[firstEmpty];
  for (const { mask, shapeIdx } of possibleMoves) {
    if (quantities[shapeIdx] > 0) {
      if ((gridMask & mask) === 0n) {
        quantities[shapeIdx]--;
        if (
          backtrack(
            gridMask | mask,
            quantities,
            movesAtCell,
            totalCells,
            relevantShapes,
            width,
            currentSlack,
            minPieceSize,
          )
        ) {
          return true;
        }
        quantities[shapeIdx]++;
      }
    }
  }

  if (currentSlack > 0) {
    if (
      backtrack(
        gridMask | (1n << BigInt(firstEmpty)),
        quantities,
        movesAtCell,
        totalCells,
        relevantShapes,
        width,
        currentSlack - 1,
        minPieceSize,
      )
    ) {
      return true;
    }
  }

  return false;
}

function checkGlobalFragmentation(
  gridMask,
  totalCells,
  width,
  minPieceSize,
  availableSlack,
) {
  let visited = gridMask;
  let wastedSpace = 0;

  for (let i = 0; i < totalCells; i++) {
    const bit = 1n << BigInt(i);
    if ((visited & bit) === 0n) {
      let area = 0;
      let stack = [i];
      visited |= bit;

      while (stack.length > 0) {
        const p = stack.pop();
        area++;

        const r = Math.floor(p / width);
        const c = p % width;
        const neighbors = [
          [r - 1, c],
          [r + 1, c],
          [r, c - 1],
          [r, c + 1],
        ];

        for (const [nr, nc] of neighbors) {
          if (nr >= 0 && nc >= 0 && nr * width + nc < totalCells) {
            const ni = nr * width + nc;
            const nBit = 1n << BigInt(ni);
            if ((visited & nBit) === 0n) {
              visited |= nBit;
              stack.push(ni);
            }
          }
        }
      }

      if (area < minPieceSize) {
        wastedSpace += area;
        if (wastedSpace > availableSlack) return true;
      }
    }
  }
  return false;
}

function parseInput(lines) {
  const shapes = {};
  const queries = [];
  let currentShapeId = null;
  let currentShapeGrid = [];

  for (const line of lines) {
    const l = line.trim();
    if (!l) continue;

    if (l.includes("x") && l.includes(":")) {
      const [dims, countsStr] = l.split(":");
      const [w, h] = dims.split("x").map(Number);
      const counts = countsStr.trim().split(/\s+/).map(Number);
      queries.push({ w, h, counts });
    } else if (/^\d+:$/.test(l)) {
      if (currentShapeId !== null)
        shapes[currentShapeId] = parseGrid(currentShapeGrid);
      currentShapeId = parseInt(l, 10);
      currentShapeGrid = [];
    } else {
      currentShapeGrid.push(l);
    }
  }
  if (currentShapeId !== null)
    shapes[currentShapeId] = parseGrid(currentShapeGrid);
  return { shapes, queries };
}

function parseGrid(grid) {
  const coords = [];
  for (let r = 0; r < grid.length; r++) {
    for (let c = 0; c < grid[r].length; c++) {
      if (grid[r][c] === "#") coords.push([r, c]);
    }
  }
  return coords;
}

function precomputeVariations(shapes) {
  const out = {};
  for (const [id, coords] of Object.entries(shapes)) {
    const vars = [];
    const seen = new Set();
    let curr = coords;
    for (let i = 0; i < 4; i++) {
      [curr, flip(curr)].forEach((shape) => {
        const norm = normalize(shape);
        const key = JSON.stringify(norm);
        if (!seen.has(key)) {
          seen.add(key);
          let maxR = 0,
            maxC = 0;
          for (const [r, c] of norm) {
            if (r > maxR) maxR = r;
            if (c > maxC) maxC = c;
          }
          vars.push({
            coords: norm,
            size: norm.length,
            h: maxR + 1,
            w: maxC + 1,
          });
        }
      });
      curr = curr.map(([r, c]) => [c, -r]);
    }
    out[id] = vars;
  }
  return out;
}

function flip(coords) {
  return coords.map(([r, c]) => [r, -c]);
}
function normalize(coords) {
  if (coords.length === 0) return [];
  const minR = Math.min(...coords.map(([r]) => r));
  const minC = Math.min(...coords.map(([, c]) => c));
  return coords
    .map(([r, c]) => [r - minR, c - minC])
    .sort((a, b) => a[0] - b[0] || a[1] - b[1]);
}
