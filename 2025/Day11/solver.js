export function solveFirst(lines) {
  let paths = 0;
  const graph = {};

  for (const line of lines) {
    const [node, rest] = line.split(":");
    graph[node] = rest.trim().split(" ");
  }

  function dfs(node, path) {
    if (node === "out") {
      paths++;
      return;
    }

    if (path.has(node)) return;

    path.add(node);

    const neighbors = graph[node] || [];
    for (const next of neighbors) {
      dfs(next, path);
    }

    path.delete(node);
  }

  dfs("you", new Set());

  return paths;
}

export function solveSecond(lines) {
  const graph = {};
  const memo = new Map();

  for (const line of lines) {
    const [node, rest] = line.split(":");
    graph[node] = rest.trim().split(" ");
  }

  function countPaths(node, hasDac, hasFft) {
    if (node === "dac") hasDac = true;
    if (node === "fft") hasFft = true;

    if (node === "out") {
      return hasDac && hasFft ? 1 : 0;
    }

    const stateKey = `${node}-${hasDac}-${hasFft}`;
    if (memo.has(stateKey)) {
      return memo.get(stateKey);
    }

    let totalPaths = 0;
    const neighbors = graph[node] || [];

    for (const next of neighbors) {
      totalPaths += countPaths(next, hasDac, hasFft);
    }

    memo.set(stateKey, totalPaths);
    return totalPaths;
  }

  return countPaths("svr", false, false);
}
