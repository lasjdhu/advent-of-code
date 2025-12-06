export function solveFirst(input) {
  let total = 0;

  const lines = input.map(line => line.trim().split(/\s+/));
  const ops = lines[lines.length - 1];
  const dataRows = lines.length - 1;

  for (let i = 0; i < ops.length; i++) {
    const op = ops[i];
    let res;

    if (op === "*") {
      res = 1;
      for (let j = 0; j < dataRows; j++) {
        res *= Number(lines[j][i]);
      }
    } else if (op === "+") {
      res = 0;
      for (let j = 0; j < dataRows; j++) {
        res += Number(lines[j][i]);
      }
    }

    total += res;
  }

  return total;
}

export function solveSecond(input) {
  let total = 0;

  const lines = input.map(line => line.split(''));
  const height = lines.length;
  const width = lines[0].length;

  for (let row of lines) while (row.length < width) row.push(' ');
  const ops = lines[height - 1].map(c => c).filter(c => c.trim() !== '');

  const cols = [];
  for (let c = 0; c < width; c++) {
    let col = '';
    for (let r = 0; r < height - 1; r++) col += lines[r][c];
    cols.push({ col });
  }

  const problems = [];
  let currentCols = [];

  for (let i = cols.length - 1; i >= 0; i--) {
    const { col } = cols[i];
    if (col.trim() === '') {
      if (currentCols.length) {
        problems.push(currentCols);
        currentCols = [];
      }
      continue;
    }
    currentCols.push({ col });
  }
  if (currentCols.length) problems.push(currentCols);

  for (let i = 0; i < problems.length; i++) {
    const colsGroup = problems[i];
    const op = ops[ops.length - 1 - i];
    let res = op === '*' ? 1 : 0;

    for (const { col } of colsGroup) {
      const num = Number(col.replace(/\s/g, ''));
      if (op === '*') res *= num;
      else if (op === '+') res += num;
    }

    total += res;
  }

  return total;
}

