package Day20;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

import utils.FileUtils;

enum LabyrinthObject { WALL, START, END, EMPTY }

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
    Queue<int[]> queue = new LinkedList<>();
    queue.add(new int[]{startX, startY, 0});
    boolean[][] visited = new boolean[rows][cols];
    visited[startX][startY] = true;

    while (!queue.isEmpty()) {
      int[] current = queue.poll();
      int x = current[0];
      int y = current[1];
      int dist = current[2];

      if (grid.get(x).get(y) == LabyrinthObject.END) {
        return dist;
      }

      for (int[] dir : directions) {
        int newX = x + dir[0];
        int newY = y + dir[1];

        if (newX >= 0 && newX < rows && newY >= 0 && newY < cols &&
          !visited[newX][newY] && grid.get(newX).get(newY) != LabyrinthObject.WALL) {
          visited[newX][newY] = true;
          queue.add(new int[]{newX, newY, dist + 1});
        }
      }
    }
    return -1;
  }

  private void findStartEnd(int[] start, int[] end) {
    int rows = grid.size();
    int cols = grid.get(0).size();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (grid.get(i).get(j) == LabyrinthObject.START) {
          start[0] = i;
          start[1] = j;
        } else if (grid.get(i).get(j) == LabyrinthObject.END) {
          end[0] = i;
          end[1] = j;
        }
      }
    }
  }

  private Map<Integer, Integer> calculateSavings(int startX, int startY, int originalScore) {
    int rows = grid.size();
    int cols = grid.get(0).size();
    List<int[]> walls = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (grid.get(i).get(j) == LabyrinthObject.WALL) {
          walls.add(new int[]{i, j});
        }
      }
    }

    Map<Integer, Integer> savingsCount = new HashMap<>();
    for (int i = 0; i < walls.size(); i++) {
      int[] cheat1 = walls.get(i);
      grid.get(cheat1[0]).set(cheat1[1], LabyrinthObject.EMPTY);
      int cheatedScore1 = calculateShortestPath(startX, startY);
      grid.get(cheat1[0]).set(cheat1[1], LabyrinthObject.WALL);

      if (cheatedScore1 != -1 && cheatedScore1 < originalScore) {
        int savings = originalScore - cheatedScore1;
        savingsCount.put(savings, savingsCount.getOrDefault(savings, 0) + 1);
      }
    }
    return savingsCount;
  }

  public String partOne() {
    loadInput("2024/Day20/input.txt");
    fillGrid();

    int[] start = new int[2];
    int[] end = new int[2];
    findStartEnd(start, end);

    int originalScore = calculateShortestPath(start[0], start[1]);
    Map<Integer, Integer> savingsCount = calculateSavings(start[0], start[1], originalScore);

    int cheatsOver100 = savingsCount.entrySet()
      .stream()
      .filter(entry -> entry.getKey() >= 100)
      .mapToInt(Map.Entry::getValue)
      .sum();

    return Integer.toString(cheatsOver100);
  }

  public String partTwo() {
    loadInput("2024/Day20/input.txt");

    return Integer.toString(0);
  }
}
