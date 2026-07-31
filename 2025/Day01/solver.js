export function solveFirst(lines) {
  let pos = 50
  let zeroCounter = 0

  for (const line of lines) {
    const direction = line.charAt(0)
    const value = Number(line.substring(1))

    if (direction === 'L') {
      pos = (pos - value) % 100
    } else {
      pos = (pos + value) % 100
    }

    if (pos === 0) zeroCounter++
  }

  return zeroCounter
}

export function solveSecond(lines) {
  let pos = 50;
  let zeroCounter = 0;

  for (const line of lines) {
    const dir = line.charAt(0);
    const value = Number(line.substring(1));

    if (dir === 'R') {
      let k0 = (100 - pos) % 100;
      if (k0 === 0) k0 = 100;

      if (value >= k0) {
        zeroCounter += 1 + Math.floor((value - k0) / 100);
      }

      pos = (pos + value) % 100;
    } else {
      let k0 = pos % 100;
      if (k0 === 0) k0 = 100;

      if (value >= k0) {
        zeroCounter += 1 + Math.floor((value - k0) / 100);
      }

      pos = ((pos - value) % 100 + 100) % 100;
    }
  }

  return zeroCounter;
}


