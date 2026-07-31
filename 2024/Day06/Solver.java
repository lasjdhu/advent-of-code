package Day06;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import utils.FileUtils;

enum Direction { UP, RIGHT, DOWN, LEFT }

class Guard {
  public int x;
  public int y;
  public Direction direction;

  public Guard(int x, int y, Direction direction) {
    this.x = x;
    this.y = y;
    this.direction = direction;
  }
}

public class Solver {
  private List<String> input = new ArrayList<>();
  private List<List<Integer>> finalMatrix;

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initializeFinalMatrix() {
    finalMatrix = new ArrayList<>();

    for (String line : input) {
      List<Integer> row = new ArrayList<>();
      for (int i = 0; i < line.length(); i++) {
        row.add(0);
      }
      finalMatrix.add(row);
    }
  }

  private Guard findGuard() {
    for (int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        switch (line.charAt(x)) {
          case '^': return new Guard(x, y, Direction.UP);
          case '>': return new Guard(x, y, Direction.RIGHT);
          case 'v': return new Guard(x, y, Direction.DOWN);
          case '<': return new Guard(x, y, Direction.LEFT);
        }
      }
    }
    throw new IllegalStateException("No guard found");
  }

  private Guard getNextPosition(Guard guard) {
    int nextX = guard.x;
    int nextY = guard.y;
    Direction direction = guard.direction;
    switch (direction) {
      case UP:    nextY--; break;
      case RIGHT: nextX++; break;
      case DOWN:  nextY++; break;
      case LEFT:  nextX--; break;
    }

    if (nextX < 0 || nextX >= input.get(0).length() || 
      nextY < 0 || nextY >= input.size()) {
      return null;
    }

    if (input.get(nextY).charAt(nextX) == '#') {
      direction = getNextDirection(direction);
      return new Guard(guard.x, guard.y, direction);
    }

    return new Guard(nextX, nextY, direction);
  }

  private Direction getNextDirection(Direction current) {
    switch (current) {
        case UP: return Direction.RIGHT;
        case RIGHT: return Direction.DOWN;
        case DOWN: return Direction.LEFT;
        case LEFT: return Direction.UP;
        default: throw new IllegalStateException("Invalid direction");
    }
  }

  public String partOne() {
    loadInput("2024/Day06/input.txt");
    initializeFinalMatrix();

    Guard guard = findGuard();

    finalMatrix.get(guard.y).set(guard.x, 1);

    while (true) {
      Guard nextGuard = getNextPosition(guard);

      if (nextGuard == null) {
        break;
      }

      guard = nextGuard;
      finalMatrix.get(guard.y).set(guard.x, 1);
    }

    int result = 0;
    for (List<Integer> row : finalMatrix) {
      for (Integer element : row) {
        if (element == 1) result++;
      }
    }

    return Integer.toString(result);
  }

  public String partTwo() {
    int loopCount = 0;

    for (int y = 0; y < input.size(); y++) {
      for (int x = 0; x < input.get(0).length(); x++) {
        loadInput("2024/Day06/input.txt");
        char cell = input.get(y).charAt(x);
        if (cell == '^' || cell == '>' || cell == 'v' || cell == '<' || cell == '#') {
          continue;
        }

        List<String> modifiedInput = new ArrayList<>(input);
        StringBuilder line = new StringBuilder(modifiedInput.get(y));
        line.setCharAt(x, '#');
        modifiedInput.set(y, line.toString());

        input = modifiedInput;
        initializeFinalMatrix();
        Guard guard = findGuard();

        Set<String> visitedStates = new HashSet<>();
        boolean isCycle = false;

        while (true) {
          String state = guard.x + "," + guard.y + "," + guard.direction;
          if (visitedStates.contains(state)) {
            isCycle = true;
            break;
          }

          visitedStates.add(state);
          Guard nextGuard = getNextPosition(guard);
          if (nextGuard == null) {
            break;
          }

          guard = nextGuard;
        }

        if (isCycle) {
          loopCount++;
        }
      }
    }

    return Integer.toString(loopCount);
  }
}
