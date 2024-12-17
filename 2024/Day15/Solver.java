package Day15;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import utils.FileUtils;

enum WarehouseObject { EMPTY, WALL, BOX, ROBOT }

class Robot {
  public int x;
  public int y;

  public Robot(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<List<WarehouseObject>> grid = new ArrayList<>();
  private Queue<Character> moveQueue = new LinkedList<>();
  private Robot robot;

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseMapAndMoves() {
    boolean mapEnded = false;

    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      if (line.trim().isEmpty()) {
        mapEnded = true;
        continue;
      }

      if (!mapEnded) {
        List<WarehouseObject> row = new ArrayList<>();
        for (int j = 0; j < line.length(); j++) {
          char c = line.charAt(j);
          if (c == '#') {
            row.add(WarehouseObject.WALL);
          } else if (c == 'O') {
            row.add(WarehouseObject.BOX);
          } else if (c == '@') {
            row.add(WarehouseObject.ROBOT);
            robot = new Robot(j, i);
          } else {
            row.add(WarehouseObject.EMPTY);
          }
        }
        grid.add(row);
      } else {
        for (char move : line.replaceAll("\\s", "").toCharArray()) {
          moveQueue.offer(move);
        }
      }
    }
  }

  private boolean pushChainOfBoxes(int boxX, int boxY, int moveX, int moveY) {
    int nextBoxX = boxX + moveX;
    int nextBoxY = boxY + moveY;

    if (grid.get(nextBoxY).get(nextBoxX) == WarehouseObject.WALL) {
      return false;
    }

    if (grid.get(nextBoxY).get(nextBoxX) == WarehouseObject.EMPTY) {
      grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
      grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX);
      return true;
    }

    if (grid.get(nextBoxY).get(nextBoxX) == WarehouseObject.BOX) {
      if (pushChainOfBoxes(nextBoxX, nextBoxY, moveX, moveY)) {
        grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
        grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX);
        return true;
      }
    }

    return false;
  }

  private void tick(char move) {
    int moveX = 0;
    int moveY = 0;

    switch (move) {
      case '^':
        moveY = -1;
        break;
      case '>':
        moveX = 1;
        break;
      case 'v':
        moveY = 1;
        break;
      case '<':
        moveX = -1;
        break;
    }

    int nextX = robot.x + moveX;
    int nextY = robot.y + moveY;

    if (grid.get(nextY).get(nextX) == WarehouseObject.WALL) {
      return;
    }

    if (grid.get(nextY).get(nextX) == WarehouseObject.EMPTY) {
      grid.get(robot.y).set(robot.x, WarehouseObject.EMPTY);
      grid.get(nextY).set(nextX, WarehouseObject.ROBOT);
      robot.x = nextX;
      robot.y = nextY;
      return;
    }

    if (grid.get(nextY).get(nextX) == WarehouseObject.BOX) {
      if (pushChainOfBoxes(nextX, nextY, moveX, moveY)) {
        grid.get(robot.y).set(robot.x, WarehouseObject.EMPTY);
        grid.get(nextY).set(nextX, WarehouseObject.ROBOT);
        robot.x = nextX;
        robot.y = nextY;
      }
    }
  }

  private int findGPSSum() {
    int sum = 0;

    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(0).size(); j++) {
        WarehouseObject cell = grid.get(i).get(j);

        switch (cell) {
          case BOX:
            sum += 100 * i + j;
            break;
        }
      }
    }

    return sum;
  }

  public String partOne() {
    loadInput("2024/Day15/input.txt");

    parseMapAndMoves();
    while (!moveQueue.isEmpty()) {
      char move = moveQueue.poll();
      tick(move);
    }

    return Integer.toString(findGPSSum());
  }

  public String partTwo() {
    loadInput("2024/Day15/mockupInput.txt");

    return Integer.toString(0);
  }
}

