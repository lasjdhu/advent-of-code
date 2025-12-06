export function solveFirst(lines) {
  let result = 0;

  const blankIndex = lines.indexOf('');
  const rangeLines = lines.slice(0, blankIndex);
  const idLines = lines.slice(blankIndex + 1);

  const ranges = rangeLines.map(r => {
    const [s, e] = r.split('-').map(Number);
    return [s, e];
  });

  idLines.forEach(idStr => {
    const id = Number(idStr);
    for (const [start, end] of ranges) {
      if (id >= start && id <= end) {
        result++;
        break;
      }
    }
  });

  return result;
}

export function solveSecond(lines) {
  let result = 0;

  const blankIndex = lines.indexOf('');
  const rangeLines = lines.slice(0, blankIndex);

  const ranges = rangeLines
    .map(r => r.split('-').map(Number))
    .sort((a, b) => a[0] - b[0]);

  let [curStart, curEnd] = ranges[0];

  for (let i = 1; i < ranges.length; i++) {
    const [s, e] = ranges[i];

    if (s <= curEnd + 1) {
      curEnd = Math.max(curEnd, e);
    } else {
      result += curEnd - curStart + 1;
      [curStart, curEnd] = [s, e];
    }
  }

  result += curEnd - curStart + 1;

  return result;
}

