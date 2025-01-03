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
      totalPrice = 0;
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

    List<int[]> boundary = new ArrayList<>();
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
      boolean isBoundary = false;

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
          isBoundary = true;
        } else if (!visited[newRow][newCol]) {
          toExplore.add(new int[] {newRow, newCol});
        }
      }

      if (isBoundary) {
        boundary.add(new int[]{r, c});
      }
    }

    if (discount) {
      int sides = countSides(boundary, plantType);
      totalPrice += area * sides;
    } else {
      totalPrice += area * perimeter;
    }
  }

  private int countSides(List<int[]> boundary, char plantType) {
    Set<String> sides = new HashSet<>();

    for (int[] point : boundary) {
      int r = point[0];
      int c = point[1];

      if (r == 0 || input.get(r-1).charAt(c) != plantType) {
        boolean foundStart = true;
        for (int[] other : boundary) {
          if (
            other[0] == r && other[1] == c-1 &&
            (other[0] == 0 || input.get(other[0]-1).charAt(other[1]) != plantType)
          ) {
            foundStart = false;
            break;
          }
        }
        if (foundStart) {
          sides.add("H_" + r + "_" + c + "_N");
        }
      }
      if (r == input.size()-1 || input.get(r+1).charAt(c) != plantType) {
        boolean foundStart = true;
        for (int[] other : boundary) {
          if (
            other[0] == r && other[1] == c-1 &&
            (other[0] == input.size()-1 || input.get(other[0]+1).charAt(other[1]) != plantType)
          ) {
            foundStart = false;
            break;
          }
        }
        if (foundStart) {
          sides.add("H_" + (r+1) + "_" + c + "_S");
        }
      }
      if (c == 0 || input.get(r).charAt(c-1) != plantType) {
        boolean foundStart = true;
        for (int[] other : boundary) {
          if (
            other[1] == c && other[0] == r-1 &&
            (other[1] == 0 || input.get(other[0]).charAt(other[1]-1) != plantType)
          ) {
            foundStart = false;
            break;
          }
        }
        if (foundStart) {
          sides.add("V_" + c + "_" + r + "_W");
        }
      }
      if (c == input.get(r).length()-1 || input.get(r).charAt(c+1) != plantType) {
        boolean foundStart = true;
        for (int[] other : boundary) {
          if (
            other[1] == c && other[0] == r-1 &&
            (other[1] == input.get(r).length()-1 || input.get(other[0]).charAt(other[1]+1) != plantType)
          ) {
            foundStart = false;
            break;
          }
        }
        if (foundStart) {
          sides.add("V_" + (c+1) + "_" + r + "_E");
        }
      }
    }

    return sides.size();
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
    loadInput("2024/Day12/input.txt");

    boolean[][] visited = new boolean[input.size()][input.get(0).length()];
    for (int i = 0; i < input.size(); i++) {
      for (int j = 0; j < input.get(i).length(); j++) {
        if (!visited[i][j]) {
          exploreRegion(i, j, visited, true);
        }
      }
    }

    return Integer.toString(totalPrice);
  }
}

