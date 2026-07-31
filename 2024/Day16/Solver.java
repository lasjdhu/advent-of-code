package Day16;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;

import utils.FileUtils;

enum LabyrinthObject { WALL, START, END, EMPTY, SEAT }

class PathState {
  int score;
  int x;
  int y;
  int dir;
  List<int[]> path;

  PathState(int score, int x, int y, int dir, List<int[]> path) {
    this.score = score;
    this.x = x;
    this.y = y;
    this.dir = dir;
    this.path = new ArrayList<>(path);
    this.path.add(new int[]{x, y, dir});
  }
}

class PathStateKey {
  int x, y, dir;

  PathStateKey(int x, int y, int dir) {
    this.x = x;
    this.y = y;
    this.dir = dir;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PathStateKey that = (PathStateKey) o;
    return x == that.x && y == that.y && dir == that.dir;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, dir);
  }
}


public class Solver {

    private List<String> input = new ArrayList<>();
    private List<List<LabyrinthObject>> grid = new ArrayList<>();

    private void loadInput(String filename) {
      try {
        grid.clear();
        input.clear();
        input = FileUtils.readLines(filename);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void fillGrid() {
      for (int i = 0; i < input.size(); i++) {
        List<LabyrinthObject> row = new ArrayList<>();
        for (int j = 0; j < input.get(0).length(); j++) {
          char c = input.get(i).charAt(j);
          if (c == '#') {
            row.add(LabyrinthObject.WALL);
          } else if (c == 'S') {
            row.add(LabyrinthObject.START);
          } else if (c == 'E') {
            row.add(LabyrinthObject.END);
          } else {
            row.add(LabyrinthObject.EMPTY);
          }
        }
        grid.add(row);
      }
    }

    private int calculateShortestPath(int startX, int startY) {
      int rows = grid.size();
      int cols = grid.get(0).size();
      int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
      PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
      queue.add(new int[]{0, startX, startY, 0});
      boolean[][][] visited = new boolean[rows][cols][4];
      visited[startX][startY][0] = true;

      while (!queue.isEmpty()) {
        int[] current = queue.poll();
        int score = current[0];
        int x = current[1];
        int y = current[2];
        int dir = current[3];

        if (grid.get(x).get(y) == LabyrinthObject.END) {
          return score;
        }

        int newX = x + directions[dir][0];
        int newY = y + directions[dir][1];

        if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && !visited[newX][newY][dir] && grid.get(newX).get(newY) != LabyrinthObject.WALL) {
          visited[newX][newY][dir] = true;
          queue.add(new int[]{score + 1, newX, newY, dir});
        }

        for (int rotation = -1; rotation <= 1; rotation += 2) {
          int newDir = (dir + rotation + 4) % 4;
          if (!visited[x][y][newDir]) {
            visited[x][y][newDir] = true;
            queue.add(new int[]{score + 1000, x, y, newDir});
          }
        }
      }
      return -1;
    }

    private List<List<int[]>> findBestPaths(int startX, int startY) {
      int rows = grid.size();
      int cols = grid.get(0).size();
      int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

      List<List<int[]>> bestPaths = new ArrayList<>();
      int minScore = Integer.MAX_VALUE;

      PriorityQueue<PathState> queue = new PriorityQueue<>((a, b) -> {
        int scoreDiff = Integer.compare(a.score, b.score);
        if (scoreDiff != 0) return scoreDiff;
        return Boolean.compare(
          grid.get(a.x).get(a.y) == LabyrinthObject.END, 
          grid.get(b.x).get(b.y) == LabyrinthObject.END
        );
      });

      queue.add(new PathState(0, startX, startY, 0, new ArrayList<>()));

      Map<PathStateKey, Set<Integer>> visitedScores = new HashMap<>();

      while (!queue.isEmpty()) {
        PathState current = queue.poll();

        if (grid.get(current.x).get(current.y) == LabyrinthObject.END) {
          if (current.score < minScore) {
            bestPaths.clear();
            minScore = current.score;
          }
          if (current.score == minScore) {
            bestPaths.add(current.path);
          }
          continue;
        }

        PathStateKey currentKey = new PathStateKey(current.x, current.y, current.dir);
        boolean shouldExplore = true;
        Set<Integer> stateScores = visitedScores.getOrDefault(currentKey, new HashSet<>());
        if (!stateScores.isEmpty()) {
          shouldExplore = stateScores.stream().anyMatch(s -> s >= current.score);
        }

        if (!shouldExplore) continue;

        stateScores.add(current.score);
        visitedScores.put(currentKey, stateScores);

        int[][] currentDirections = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int newX = current.x + currentDirections[current.dir][0];
        int newY = current.y + currentDirections[current.dir][1];

        if (newX >= 0 && newX < rows && newY >= 0 && newY < cols
          && grid.get(newX).get(newY) != LabyrinthObject.WALL) {
          queue.add(new PathState(current.score + 1, newX, newY, current.dir, current.path));
        }

        for (int rotation : new int[]{-1, 1}) {
          int newDir = (current.dir + rotation + 4) % 4;
          queue.add(new PathState(current.score + 1000, current.x, current.y, newDir, current.path));
        }
      }

      return bestPaths;
    }

    public String partOne() {
      loadInput("2024/Day16/mockupInput.txt");

      fillGrid();

      int startX = -1;
      int startY = -1;
      for (int i = 0; i < grid.size(); i++) {
        for (int j = 0; j < grid.get(0).size(); j++) {
          if (grid.get(i).get(j) == LabyrinthObject.START) {
            startX = i;
            startY = j;
            break;
          }
        }
      }

      int score = calculateShortestPath(startX, startY);

      return Integer.toString(score);
    }

  public String partTwo() {
    loadInput("2024/Day16/mockupInput.txt");

    fillGrid();

    int startX = -1;
    int startY = -1;
    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(0).size(); j++) {
        if (grid.get(i).get(j) == LabyrinthObject.START) {
          startX = i;
          startY = j;
          break;
        }
      }
    }

    List<List<int[]>> bestPaths = findBestPaths(startX, startY);

    Set<String> bestPathTiles = new HashSet<>();
    for (List<int[]> path : bestPaths) {
      for (int[] step : path) {
        bestPathTiles.add(step[0] + "," + step[1]);
      }
    }

    for (String tile : bestPathTiles) {
      String[] parts = tile.split(",");
      int x = Integer.parseInt(parts[0]);
      int y = Integer.parseInt(parts[1]);
      if (grid.get(x).get(y) == LabyrinthObject.EMPTY) {
        grid.get(x).set(y, LabyrinthObject.SEAT);
      }
    }

    return Integer.toString(bestPathTiles.size());
  }
}
