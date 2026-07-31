package Day18;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import utils.FileUtils;

enum Memory { OK, CORRUPTED, VISITED }

class Me {
  public int x;
  public int y;

  public Me(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<List<Memory>> grid = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initializeGrid() {
    grid.clear();
    for (int i = 0; i <= 70; i++) {
      List<Memory> row = new ArrayList<>();
      for (int j = 0; j <= 70; j++) {
        row.add(Memory.OK);
      }
      grid.add(row);
    }
  }

  private void fillWithFallingBytes() {
    int i = 0;
    for (String line : input) {
      if (i >= 1024) {
        break;
      }
      String[] coords = line.split(",");
      int x = Integer.parseInt(coords[0]);
      int y = Integer.parseInt(coords[1]);
      grid.get(y).set(x, Memory.CORRUPTED);
      i++;
    }
  }

  private int findPath(int startX, int startY, boolean findShortestPath) {
    List<Me> queue = new ArrayList<>();
    queue.add(new Me(startX, startY));

    int[][] steps = new int[71][71];
    for (int[] row : steps) {
      Arrays.fill(row, -1);
    }
    steps[startY][startX] = 0;

    Me[][] parent = findShortestPath ? new Me[71][71] : null;

    while (!queue.isEmpty()) {
      Me me = queue.remove(0);

      if (me.x == 70 && me.y == 70) {
        if (findShortestPath) {
          Me current = me;
          while (current != null) {
            grid.get(current.y).set(current.x, Memory.VISITED);
            current = parent[current.y][current.x];
          }
          return steps[me.y][me.x];
        }
        return 0;
      }

      if (me.x > 0 && grid.get(me.y).get(me.x - 1) == Memory.OK && steps[me.y][me.x - 1] == -1) {
        queue.add(new Me(me.x - 1, me.y));
        steps[me.y][me.x - 1] = steps[me.y][me.x] + 1;
        if (findShortestPath) {
          parent[me.y][me.x - 1] = me;
        }
      }
      if (me.x < 70 && grid.get(me.y).get(me.x + 1) == Memory.OK && steps[me.y][me.x + 1] == -1) {
        queue.add(new Me(me.x + 1, me.y));
        steps[me.y][me.x + 1] = steps[me.y][me.x] + 1;
        if (findShortestPath) {
          parent[me.y][me.x + 1] = me;
        }
      }
      if (me.y > 0 && grid.get(me.y - 1).get(me.x) == Memory.OK && steps[me.y - 1][me.x] == -1) {
        queue.add(new Me(me.x, me.y - 1));
        steps[me.y - 1][me.x] = steps[me.y][me.x] + 1;
        if (findShortestPath) {
          parent[me.y - 1][me.x] = me;
        }
      }
      if (me.y < 70 && grid.get(me.y + 1).get(me.x) == Memory.OK && steps[me.y + 1][me.x] == -1) {
        queue.add(new Me(me.x, me.y + 1));
        steps[me.y + 1][me.x] = steps[me.y][me.x] + 1;
        if (findShortestPath) {
          parent[me.y + 1][me.x] = me;
        }
      }
    }

    return -1;
  }

  public String partOne() {
    loadInput("2024/Day18/input.txt");

    initializeGrid();
    fillWithFallingBytes();
    int steps = findPath(0, 0, true);

    return Integer.toString(steps);
  }

  public String partTwo() {
    loadInput("2024/Day18/input.txt");

    initializeGrid();
    fillWithFallingBytes();

    for (String line : input) {
      String[] coords = line.split(",");
      int x = Integer.parseInt(coords[0]);
      int y = Integer.parseInt(coords[1]);

      grid.get(y).set(x, Memory.CORRUPTED);

      if (findPath(0, 0, false) == -1) {
        return x + "," + y;
      }
    }

    return "???";
  }
}

