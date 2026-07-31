function parsePoints(lines) {
  return lines
    .filter((line) => line.trim().length > 0)
    .map((line, index) => {
      const [x, y, z] = line.split(",").map(Number);
      return { id: index, x, y, z };
    });
}

function getSortedEdges(points) {
  const N = points.length;
  const edges = [];

  for (let i = 0; i < N; i++) {
    for (let j = i + 1; j < N; j++) {
      const p1 = points[i];
      const p2 = points[j];
      const distSq =
        (p1.x - p2.x) ** 2 + (p1.y - p2.y) ** 2 + (p1.z - p2.z) ** 2;
      edges.push({ u: i, v: j, w: distSq });
    }
  }

  edges.sort((a, b) => a.w - b.w);
  return edges;
}

class UnionFind {
  constructor(size) {
    this.parent = new Int32Array(size).map((_, i) => i);
    this.size = new Int32Array(size).fill(1);
    this.numComponents = size;
  }

  find(i) {
    let root = i;
    while (root !== this.parent[root]) {
      root = this.parent[root];
    }
    let curr = i;
    while (curr !== root) {
      let next = this.parent[curr];
      this.parent[curr] = root;
      curr = next;
    }
    return root;
  }

  union(i, j) {
    const rootA = this.find(i);
    const rootB = this.find(j);

    if (rootA === rootB) return false;

    if (this.size[rootA] < this.size[rootB]) {
      this.parent[rootA] = rootB;
      this.size[rootB] += this.size[rootA];
    } else {
      this.parent[rootB] = rootA;
      this.size[rootA] += this.size[rootB];
    }

    this.numComponents--;
    return true;
  }

  getSizes() {
    const circuitSizes = [];
    for (let i = 0; i < this.parent.length; i++) {
      if (this.parent[i] === i) {
        circuitSizes.push(this.size[i]);
      }
    }
    return circuitSizes;
  }
}

export function solveFirst(lines) {
  const points = parsePoints(lines);
  const edges = getSortedEdges(points);
  const uf = new UnionFind(points.length);

  const LIMIT = 1000;
  for (let i = 0; i < LIMIT && i < edges.length; i++) {
    uf.union(edges[i].u, edges[i].v);
  }

  const sizes = uf.getSizes();
  sizes.sort((a, b) => b - a);

  const top3 = sizes.slice(0, 3);
  return top3.reduce((acc, val) => acc * val, 1);
}

export function solveSecond(lines) {
  const points = parsePoints(lines);
  const edges = getSortedEdges(points);
  const uf = new UnionFind(points.length);

  for (const edge of edges) {
    if (uf.union(edge.u, edge.v)) {
      if (uf.numComponents === 1) {
        return points[edge.u].x * points[edge.v].x;
      }
    }
  }

  return 0;
}
