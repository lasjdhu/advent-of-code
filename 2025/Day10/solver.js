export function solveFirst(lines) {
  let totalPresses = 0;

  for (const line of lines) {
    const { indicatorMask, buttons } = parseMachine(line);

    const buttonMasks = buttons.map((indices) =>
      indices.reduce((mask, bit) => mask | (1 << bit), 0),
    );

    totalPresses += bfsMinPresses(indicatorMask, buttonMasks);
  }

  return totalPresses;
}

export function solveSecond(lines) {
  let totalPresses = 0;

  for (const line of lines) {
    const { joltages, buttons } = parseMachine(line);

    const patterns = precomputePatterns(buttons);
    const cache = new Map();

    const result = solveRecursive(joltages, buttons, patterns, cache);

    if (result !== Infinity) {
      totalPresses += result;
    }
  }

  return totalPresses;
}

function parseMachine(line) {
  const indicatorStr = line.split("]")[0].slice(1);
  let indicatorMask = 0;
  for (let i = 0; i < indicatorStr.length; i++) {
    if (indicatorStr[i] === "#") {
      indicatorMask |= 1 << i;
    }
  }

  const buttons = line
    .split("]")[1]
    .split("{")[0]
    .trim()
    .split(" ")
    .map((b) =>
      b
        .slice(1, b.length - 1)
        .split(",")
        .map(Number),
    );

  const targetStr = line.split("{")[1].split("}")[0];
  const joltages = targetStr.split(",").map(Number);

  return { indicatorMask, buttons, joltages };
}

function bfsMinPresses(target, buttons) {
  const queue = [[0, 0]];
  const visited = new Set();
  visited.add(0);

  while (queue.length > 0) {
    const [current, presses] = queue.shift();

    if (current === target) {
      return presses;
    }

    for (const buttonMask of buttons) {
      const nextState = current ^ buttonMask;

      if (!visited.has(nextState)) {
        visited.add(nextState);
        queue.push([nextState, presses + 1]);
      }
    }
  }

  return Infinity;
}

function precomputePatterns(buttons) {
  const patterns = new Map();
  const numButtons = buttons.length;

  for (let k = 0; k <= numButtons; k++) {
    for (const combo of getCombinations(numButtons, k)) {
      let mask = 0;
      for (const btnIdx of combo) {
        const affected = buttons[btnIdx];
        for (const bit of affected) {
          mask ^= 1 << bit;
        }
      }

      if (!patterns.has(mask)) {
        patterns.set(mask, []);
      }
      patterns.get(mask).push(combo);
    }
  }
  return patterns;
}

function solveRecursive(target, buttons, patterns, cache) {
  if (target.every((n) => n === 0)) return 0;

  const key = target.join(",");
  if (cache.has(key)) return cache.get(key);

  let requiredPattern = 0;
  for (let i = 0; i < target.length; i++) {
    if (target[i] % 2 !== 0) {
      requiredPattern |= 1 << i;
    }
  }

  let minPresses = Infinity;
  const possibleMoves = patterns.get(requiredPattern) || [];

  for (const buttonIndices of possibleMoves) {
    const nextTarget = [...target];

    for (const btnIdx of buttonIndices) {
      const affected = buttons[btnIdx];
      for (const cIdx of affected) {
        nextTarget[cIdx]--;
      }
    }

    let valid = true;
    for (const val of nextTarget) {
      if (val < 0 || val % 2 !== 0) {
        valid = false;
        break;
      }
    }
    if (!valid) continue;

    const halfTarget = nextTarget.map((x) => x / 2);
    const res = solveRecursive(halfTarget, buttons, patterns, cache);

    if (res !== Infinity) {
      minPresses = Math.min(minPresses, buttonIndices.length + 2 * res);
    }
  }

  cache.set(key, minPresses);
  return minPresses;
}

function* getCombinations(n, k) {
  const indices = Array.from({ length: n }, (_, i) => i);
  function* backtrack(start, current) {
    if (current.length === k) {
      yield [...current];
      return;
    }
    for (let i = start; i < n; i++) {
      current.push(indices[i]);
      yield* backtrack(i + 1, current);
      current.pop();
    }
  }
  yield* backtrack(0, []);
}
