export function solveFirst(lines) {
  let area = 0;

  for (let i = 0; i < lines.length; i++) {
    let [x1, y1] = lines[i].split(",").map(Number);

    for (let j = 0; j < lines.length; j++) {
      if (i === j) continue;
      else {
        let [x2, y2] = lines[j].split(",").map(Number);

        let cur = (Math.abs(x1 - x2) + 1) * (Math.abs(y1 - y2) + 1);
        if (cur > area) area = cur;
      }
    }
  }

  return area;
}

export function solveSecond(lines) {
  const points = lines.map((l) => l.split(",").map(Number));
  const n = points.length;

  const edges = [];
  for (let i = 0; i < n; i++) {
    edges.push({ p1: points[i], p2: points[(i + 1) % n] });
  }

  let maxArea = 0;

  for (let i = 0; i < n; i++) {
    for (let j = i + 1; j < n; j++) {
      const p1 = points[i];
      const p2 = points[j];

      const xMin = Math.min(p1[0], p2[0]);
      const xMax = Math.max(p1[0], p2[0]);
      const yMin = Math.min(p1[1], p2[1]);
      const yMax = Math.max(p1[1], p2[1]);

      const area = (xMax - xMin + 1) * (yMax - yMin + 1);

      if (area <= maxArea) continue;

      let crossing = false;
      for (const edge of edges) {
        if (edgeIntersectsRect(edge, xMin, xMax, yMin, yMax)) {
          crossing = true;
          break;
        }
      }
      if (crossing) continue;

      const mx = (xMin + xMax) / 2;
      const my = (yMin + yMax) / 2;

      if (isPointInOrOnPolygon(mx, my, edges)) {
        maxArea = area;
      }
    }
  }

  return maxArea;
}

function edgeIntersectsRect(edge, xMin, xMax, yMin, yMax) {
  const [ex1, ey1] = edge.p1;
  const [ex2, ey2] = edge.p2;

  if (ex1 === ex2) {
    if (ex1 > xMin && ex1 < xMax) {
      const eYMin = Math.min(ey1, ey2);
      const eYMax = Math.max(ey1, ey2);
      if (eYMin < yMax && eYMax > yMin) return true;
    }
  } else {
    if (ey1 > yMin && ey1 < yMax) {
      const eXMin = Math.min(ex1, ex2);
      const eXMax = Math.max(ex1, ex2);
      if (eXMin < xMax && eXMax > xMin) return true;
    }
  }
  return false;
}

function isPointInOrOnPolygon(x, y, edges) {
  for (const edge of edges) {
    const [x1, y1] = edge.p1;
    const [x2, y2] = edge.p2;
    if (x1 === x2) {
      if (x1 === x && y >= Math.min(y1, y2) && y <= Math.max(y1, y2))
        return true;
    } else {
      if (y1 === y && x >= Math.min(x1, x2) && x <= Math.max(x1, x2))
        return true;
    }
  }

  const testY = y + 0.0001;
  let intersections = 0;

  for (const edge of edges) {
    const [x1, y1] = edge.p1;
    const [x2, y2] = edge.p2;

    if (x1 === x2) {
      if (y1 > testY !== y2 > testY) {
        if (x1 > x) intersections++;
      }
    }
  }

  return intersections % 2 === 1;
}
