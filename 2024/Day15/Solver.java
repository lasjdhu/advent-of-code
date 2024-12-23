package Day15;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import utils.FileUtils;

enum WarehouseObject { EMPTY, WALL, BOX, BOX_L, BOX_R, ROBOT }

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
      grid.clear();
      moveQueue.clear();
      robot = null;
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseMapAndMoves(boolean wide) {
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
            if (wide) {
              row.add(WarehouseObject.WALL);
              row.add(WarehouseObject.WALL);
            } else {
              row.add(WarehouseObject.WALL);
            }
          } else if (c == 'O') {
            if (wide) {
              row.add(WarehouseObject.BOX_L);
              row.add(WarehouseObject.BOX_R);
            } else {
              row.add(WarehouseObject.BOX);
            }
          } else if (c == '@') {
            if (wide) {
              row.add(WarehouseObject.ROBOT);
              row.add(WarehouseObject.EMPTY);
              robot = new Robot(j * 2, i);
            } else {
              row.add(WarehouseObject.ROBOT);
              robot = new Robot(j, i);
            }
          } else {
            if (wide) {
              row.add(WarehouseObject.EMPTY);
              row.add(WarehouseObject.EMPTY);
            } else {
              row.add(WarehouseObject.EMPTY);
            }
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

  private boolean pushChainOfBoxes(int boxX, int boxY, int moveX, int moveY, boolean wide, WarehouseObject boxType) {
    int nextBoxX = boxX + moveX;
    int nextBoxY = boxY + moveY;

    // Wall check
    if (grid.get(nextBoxY).get(nextBoxX) == WarehouseObject.WALL) {
        return false;
    }

    // For vertical movement in wide mode, we need to check adjacent boxes
    if (wide && moveY != 0) {
        // Get all connected boxes at the same Y level
        List<Integer> connectedBoxesX = new ArrayList<>();
        if (boxType == WarehouseObject.BOX_L) {
            connectedBoxesX.add(boxX);
            connectedBoxesX.add(boxX + 1);
            // Check for boxes to the right
            int checkX = boxX + 2;
            while (checkX < grid.get(boxY).size() && 
                   (grid.get(boxY).get(checkX) == WarehouseObject.BOX_L || 
                    grid.get(boxY).get(checkX) == WarehouseObject.BOX_R)) {
                connectedBoxesX.add(checkX);
                checkX++;
            }
        } else if (boxType == WarehouseObject.BOX_R) {
            connectedBoxesX.add(boxX - 1);
            connectedBoxesX.add(boxX);
            // Check for boxes to the left
            int checkX = boxX - 2;
            while (checkX >= 0 && 
                   (grid.get(boxY).get(checkX) == WarehouseObject.BOX_L || 
                    grid.get(boxY).get(checkX) == WarehouseObject.BOX_R)) {
                connectedBoxesX.add(checkX);
                checkX--;
            }
        }

        // First check if ALL boxes can move
        for (int x : connectedBoxesX) {
            if (grid.get(nextBoxY).get(x) == WarehouseObject.WALL) {
                return false;
            }
        }

        // Then check for other boxes in the way and verify they can all move
        boolean canPushAll = true;
        for (int x : connectedBoxesX) {
            if (grid.get(nextBoxY).get(x) == WarehouseObject.BOX_L ||
                grid.get(nextBoxY).get(x) == WarehouseObject.BOX_R) {
                if (!pushChainOfBoxes(x, nextBoxY, moveX, moveY, wide, grid.get(nextBoxY).get(x))) {
                    canPushAll = false;
                    break;
                }
            }
        }

        if (!canPushAll) {
            return false;
        }

        // Only move boxes if all checks passed
        for (int x : connectedBoxesX) {
            grid.get(nextBoxY).set(x, grid.get(boxY).get(x));
            grid.get(boxY).set(x, WarehouseObject.EMPTY);
        }
        return true;
    }

    // Empty space - we can move the box
    if (grid.get(nextBoxY).get(nextBoxX) == WarehouseObject.EMPTY) {
        if (wide) {
            if (boxType == WarehouseObject.BOX_L) {
                grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
                grid.get(boxY).set(boxX + 1, WarehouseObject.EMPTY);
                grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX_L);
                grid.get(nextBoxY).set(nextBoxX + 1, WarehouseObject.BOX_R);
            } else if (boxType == WarehouseObject.BOX_R) {
                grid.get(boxY).set(boxX - 1, WarehouseObject.EMPTY);
                grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
                grid.get(nextBoxY).set(nextBoxX - 1, WarehouseObject.BOX_L);
                grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX_R);
            }
        } else {
            grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
            grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX);
        }
        return true;
    }

    // Hit another box - try to push the chain
    WarehouseObject nextCell = grid.get(nextBoxY).get(nextBoxX);
    if (nextCell == WarehouseObject.BOX || nextCell == WarehouseObject.BOX_L || nextCell == WarehouseObject.BOX_R) {
        // Try to push the next box in the chain
        if (pushChainOfBoxes(nextBoxX, nextBoxY, moveX, moveY, wide, nextCell)) {
            // If chain was pushed successfully, move our current box
            if (wide) {
                if (boxType == WarehouseObject.BOX_L) {
                    grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
                    grid.get(boxY).set(boxX + 1, WarehouseObject.EMPTY);
                    grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX_L);
                    grid.get(nextBoxY).set(nextBoxX + 1, WarehouseObject.BOX_R);
                } else if (boxType == WarehouseObject.BOX_R) {
                    grid.get(boxY).set(boxX - 1, WarehouseObject.EMPTY);
                    grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
                    grid.get(nextBoxY).set(nextBoxX - 1, WarehouseObject.BOX_L);
                    grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX_R);
                }
            } else {
                grid.get(boxY).set(boxX, WarehouseObject.EMPTY);
                grid.get(nextBoxY).set(nextBoxX, WarehouseObject.BOX);
            }
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
      if (pushChainOfBoxes(nextX, nextY, moveX, moveY, false, WarehouseObject.BOX)) {
        grid.get(robot.y).set(robot.x, WarehouseObject.EMPTY);
        grid.get(nextY).set(nextX, WarehouseObject.ROBOT);
        robot.x = nextX;
        robot.y = nextY;
      }
    }

    if (grid.get(nextY).get(nextX) == WarehouseObject.BOX_L) {
      if (pushChainOfBoxes(nextX, nextY, moveX, moveY, true, WarehouseObject.BOX_L)) {
      grid.get(robot.y).set(robot.x, WarehouseObject.EMPTY);
      grid.get(nextY).set(nextX, WarehouseObject.ROBOT);
      robot.x = nextX;
      robot.y = nextY;
      }
    }

    if (grid.get(nextY).get(nextX) == WarehouseObject.BOX_R) {
      if (pushChainOfBoxes(nextX, nextY, moveX, moveY, true, WarehouseObject.BOX_R)) {
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
          case BOX_L:
            sum += 100 * i + j;
            break;
        }
      }
    }

    return sum;
  }

  private void printGid() {
    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(0).size(); j++) {
      WarehouseObject cell = grid.get(i).get(j);
      switch (cell) {
        case EMPTY:
          System.out.print(".");
          break;
        case WALL:
          System.out.print("#");
          break;
        case BOX:
          System.out.print("O");
          break;
        case BOX_L:
          System.out.print("[");
          break;
        case BOX_R:
          System.out.print("]");
          break;
        case ROBOT:
          System.out.print("@");
          break;
      }
      }
      System.out.println();
    }
    System.out.println();
  }

  public String partOne() {
    loadInput("2024/Day15/input.txt");

    parseMapAndMoves(false);
    while (!moveQueue.isEmpty()) {
      char move = moveQueue.poll();
      tick(move);
    }

    return Integer.toString(findGPSSum());
  }

  public String partTwo() {
    loadInput("2024/Day15/mockupInput.txt");

    parseMapAndMoves(true);
    printGid();
    while (!moveQueue.isEmpty()) {
      char move = moveQueue.poll();
      tick(move);
      printGid();
    }

    return Integer.toString(findGPSSum());
  }
}

