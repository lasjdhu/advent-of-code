package Day10;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<int[]> trailheads = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void findZeros() {
    trailheads.clear();
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      for (int j = 0; j < line.length(); j++) {
        char ch = line.charAt(j);
        if (Character.isDigit(ch)) {
          int number = Character.getNumericValue(ch);
          if (number == 0) {
            trailheads.add(new int[]{i, j});
          }
        }
      }
    }
  }

  private void findTrailEnds(int x, int y, int currentHeight, boolean[][] visited, Collection<String> trailEnds) {
    if (currentHeight == 9) {
      trailEnds.add(x + "," + y);
      return;
    }

    visited[x][y] = true;

    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    for (int[] dir : directions) {
      int newX = x + dir[0];
      int newY = y + dir[1];

      if (newX >= 0 && newX < input.size() && newY >= 0 && newY < input.get(0).length() && !visited[newX][newY]) {
        int newHeight = Character.getNumericValue(input.get(newX).charAt(newY));

        if (newHeight == currentHeight + 1) {
          findTrailEnds(newX, newY, newHeight, visited, trailEnds);
        }
      }
    }

    visited[x][y] = false;
  }

  private int calculateResult(int[] trailhead, Collection<String> trailEnds) {
    int x = trailhead[0];
    int y = trailhead[1];
    boolean[][] visited = new boolean[input.size()][input.get(0).length()];
    findTrailEnds(x, y, 0, visited, trailEnds);
    return trailEnds.size();
  }

  public String partOne() {
    loadInput("2024/Day10/input.txt");

    findZeros();
    int scoresSum = 0;
    for (int[] trailhead : trailheads) {
      scoresSum += calculateResult(trailhead, new HashSet<>());
    }

    return Integer.toString(scoresSum);
  }

  public String partTwo() {
    loadInput("2024/Day10/input.txt");

    findZeros();
    int scoresSum = 0;
    for (int[] trailhead : trailheads) {
      scoresSum += calculateResult(trailhead, new ArrayList<>());
    }

    return Integer.toString(scoresSum);
  }
}

