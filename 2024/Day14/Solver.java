package Day14;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.FileUtils;

class Robot {
  public int[] position;
  public int[] velocity;

  public Robot(int[] position, int[] velocity) {
    this.position = position;
    this.velocity = velocity;
  }

  @Override
    public String toString() {
        return "Position: (" + position[0] + "," + position[1] + "), Velocity: (" + velocity[0] + "," + velocity[1] + ")" + "\n";
    }
}

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<List<Integer>> grid = new ArrayList<>();
  private Map<Integer, Robot> robots = new HashMap<>();

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
    for (int i = 0; i < 103; i++) {
        List<Integer> row = new ArrayList<>();
        for (int j = 0; j < 101; j++) {
            row.add(0);
        }
        grid.add(row);
    }
  }

  private void initializeRobots() {
    Pattern pattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      Matcher matcher = pattern.matcher(line);
      if (matcher.matches()) {
        int[] position = { Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)) };
        int[] velocity = { Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(3)) };
        robots.put(i, new Robot(position, velocity));
      }
    }
  }

  private void placeRobotsOnGrid() {
    for (Robot robot : robots.values()) {
      int x = robot.position[0];
      int y = robot.position[1];
      if (x >= 0 && x < 103 && y >= 0 && y < 101) {
        Integer robotsNumber = grid.get(x).get(y);
        grid.get(x).set(y, robotsNumber + 1);
      }
    }
  }

  private void tick() {
    for (Robot robot : robots.values()) {
      int oldY = robot.position[0];
      int oldX = robot.position[1];

      int newY = (oldY + robot.velocity[0] + 103) % 103;
      int newX = (oldX + robot.velocity[1] + 101) % 101;

      Integer robotsNumberOldCell = grid.get(oldY).get(oldX);
      grid.get(oldY).set(oldX, robotsNumberOldCell - 1);

      Integer robotsNumberNewCell = grid.get(newY).get(newX);
      grid.get(newY).set(newX, robotsNumberNewCell + 1);

      robot.position[0] = newY;
      robot.position[1] = newX;
    }
  }

  private List<List<Integer>> cropQuadrant(int rowStart, int rowEnd, int colStart, int colEnd) {
    List<List<Integer>> quadrant = new ArrayList<>();
    for (int i = rowStart; i <= rowEnd; i++) {
      List<Integer> row = new ArrayList<>();
      for (int j = colStart; j <= colEnd; j++) {
        row.add(grid.get(i).get(j));
      }
      quadrant.add(row);
    }
    return quadrant;
  }

  private int count(List<List<Integer>> gridC) {
    int total = 0;

    for (List<Integer> row : gridC) {
      for (Integer cell : row) {
        total += cell;
      }
    }

    return total;
  }

  private boolean checkForChristmasTree() {
    int[][] christmasTree = {
      {0, 3},
      {1, 2}, {1, 3}, {1, 4},
      {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 5},
      {3, 0}, {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5}, {3, 6}
    };

    for (int x = 0; x < 103; x++) {
      for (int y = 0; y < 101; y++) {
        boolean matchesTree = true;

        for (int[] offset : christmasTree) {
          int checkX = x + offset[0];
          int checkY = y + offset[1];

          if (checkX < 0 || checkX >= 103 || checkY < 0 || checkY >= 101) {
            matchesTree = false;
            break;
          }

          if (grid.get(checkX).get(checkY) == 0) {
            matchesTree = false;
            break;
          }
        }

        if (matchesTree) {
          return true;
        }
      }
    }

    return false;
  }

  public String partOne() {
    loadInput("2024/Day14/input.txt");

    initializeGrid();
    initializeRobots();
    placeRobotsOnGrid();

    for (int second = 0; second < 100; second++) {
      tick();
    }

    List<List<Integer>> firstQ = cropQuadrant(0, 50, 0, 49);
    List<List<Integer>> secondQ = cropQuadrant(52, 102, 0, 49);
    List<List<Integer>> thirdQ = cropQuadrant(0, 50, 51, 100);
    List<List<Integer>> forthQ = cropQuadrant(52, 102, 51, 100);

    int firstN = count(firstQ);
    int secondN = count(secondQ);
    int thirdN = count(thirdQ);
    int forthN = count(forthQ);

    return Integer.toString(firstN * secondN * thirdN * forthN);
  }

  public String partTwo() {
    loadInput("2024/Day14/input.txt");

    initializeGrid();
    initializeRobots();
    placeRobotsOnGrid();

    int fewestSeconds = 0;

    while (!checkForChristmasTree()) {
      tick();
      fewestSeconds++;
    }

    return Integer.toString(fewestSeconds);
  }
}

