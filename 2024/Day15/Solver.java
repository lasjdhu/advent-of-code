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
    List<int[]> chain = new ArrayList<>();
    java.util.HashSet<String> seen = new java.util.HashSet<>();

    if (wide) {
      int leftX = (boxType == WarehouseObject.BOX_L) ? boxX : (boxX - 1);
      chain.add(new int[]{leftX, boxY, 1});
      seen.add(leftX + "," + boxY + ",1");
    } else {
      chain.add(new int[]{boxX, boxY, 0});
      seen.add(boxX + "," + boxY + ",0");
    }

    for (int idx = 0; idx < chain.size(); idx++) {
      int[] unit = chain.get(idx);
      int ux = unit[0], uy = unit[1], uWide = unit[2];
      int[][] srcCells;
      if (uWide == 1) {
        srcCells = new int[][]{{ux, uy}, {ux + 1, uy}};
      } else {
        srcCells = new int[][]{{ux, uy}};
      }

      for (int[] sc : srcCells) {
        int destX = sc[0] + moveX;
        int destY = sc[1] + moveY;

        if (destY < 0 || destY >= grid.size()) return false;
        if (destX < 0 || destX >= grid.get(destY).size()) return false;

        WarehouseObject dest = grid.get(destY).get(destX);
        if (dest == WarehouseObject.WALL) return false;
        if (dest == WarehouseObject.ROBOT) return false;

        if (dest == WarehouseObject.BOX) {
          String key = destX + "," + destY + ",0";
          if (!seen.contains(key)) {
            chain.add(new int[]{destX, destY, 0});
            seen.add(key);
          }
        } else if (dest == WarehouseObject.BOX_L) {
          String key = destX + "," + destY + ",1";
          if (!seen.contains(key)) {
            chain.add(new int[]{destX, destY, 1});
            seen.add(key);
          }
        } else if (dest == WarehouseObject.BOX_R) {
          int leftAnchor = destX - 1;
          String key = leftAnchor + "," + destY + ",1";
          if (!seen.contains(key)) {
            chain.add(new int[]{leftAnchor, destY, 1});
            seen.add(key);
          }
        }
      }
    }

    for (int i = chain.size() - 1; i >= 0; i--) {
      int[] u = chain.get(i);
      int ux = u[0], uy = u[1], uWide = u[2];

      if (uWide == 1) {
        int srcL = ux, srcR = ux + 1;
        int dstL = srcL + moveX, dstR = srcR + moveX;
        int dstY = uy + moveY;

        if (dstY < 0 || dstY >= grid.size()) return false;
        if (dstL < 0 || dstR >= grid.get(dstY).size()) return false;

        grid.get(uy).set(srcL, WarehouseObject.EMPTY);
        grid.get(uy).set(srcR, WarehouseObject.EMPTY);
        grid.get(dstY).set(dstL, WarehouseObject.BOX_L);
        grid.get(dstY).set(dstR, WarehouseObject.BOX_R);
      } else {
        int srcX = ux;
        int dstX = srcX + moveX;
        int dstY = uy + moveY;

        if (dstY < 0 || dstY >= grid.size()) return false;
        if (dstX < 0 || dstX >= grid.get(dstY).size()) return false;

        grid.get(uy).set(srcX, WarehouseObject.EMPTY);
        grid.get(dstY).set(dstX, WarehouseObject.BOX);
      }
    }

    return true;
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
    loadInput("2024/Day15/input.txt");

    parseMapAndMoves(true);
    while (!moveQueue.isEmpty()) {
      char move = moveQueue.poll();
      tick(move);
    }

    return Integer.toString(findGPSSum());
  }
}

