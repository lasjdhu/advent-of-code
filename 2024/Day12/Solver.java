package Day12;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private int totalPrice = 0;

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void exploreRegion(int row, int col, boolean[][] visited, boolean discount) {
    char plantType = input.get(row).charAt(col);
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    int area = 0;
    int perimeter = 0;

    List<int[]> toExplore = new ArrayList<>();
    toExplore.add(new int[] {row, col});

    while (!toExplore.isEmpty()) {
      int[] curr = toExplore.remove(toExplore.size() - 1);
      int r = curr[0];
      int c = curr[1];

      if (visited[r][c]) {
        continue;
      }
      visited[r][c] = true;
      area++;

      for (int[] direction : directions) {
        int newRow = r + direction[0];
        int newCol = c + direction[1];

        if (
          newRow < 0 ||
          newRow >= input.size() ||
          newCol < 0 ||
          newCol >= input.get(newRow).length() ||
          input.get(newRow).charAt(newCol) != plantType
        ) {
          perimeter++;
        } else if (!visited[newRow][newCol]) {
          toExplore.add(new int[] {newRow, newCol});
        }
      }
    }

    // TODO: find sides
    int sides = 0;
    totalPrice += area * (discount ? sides : perimeter);
  }

  public String partOne() {
    loadInput("2024/Day12/input.txt");

    boolean[][] visited = new boolean[input.size()][input.get(0).length()];
    for (int i = 0; i < input.size(); i++) {
      for (int j = 0; j < input.get(i).length(); j++) {
        if (!visited[i][j]) {
          exploreRegion(i, j, visited, false);
        }
      }
    }

    return Integer.toString(totalPrice);
  }

  public String partTwo() {
    loadInput("2024/Day12/mockupInput.txt");

    boolean[][] visited = new boolean[input.size()][input.get(0).length()];
    for (int i = 0; i < input.size(); i++) {
      for (int j = 0; j < input.get(i).length(); j++) {
        if (!visited[i][j]) {
          exploreRegion(i, j, visited, true);
        }
      }
    }

    return Integer.toString(0);
  }
}

