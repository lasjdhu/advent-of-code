
package Day21;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import utils.FileUtils;

class Position {
  int row;
  int col;

  Position(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Position position = (Position) obj;
    return row == position.row && col == position.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }
}

class State {
  Position position;
  List<Character> sequence;
  int matchedLength;

  State(Position position, List<Character> sequence, int matchedLength) {
    this.position = position;
    this.sequence = sequence;
    this.matchedLength = matchedLength;
  }
}

public class Solver {

  private List<String> input = new ArrayList<>();
  private char[][] NUMERIC_KEYPAD = {
    {'7', '8', '9'},
    {'4', '5', '6'},
    {'1', '2', '3'},
    {' ', '0', 'A'}
  };
  private char[][] DIRECTIONAL_KEYPAD = {
    {' ', '^', 'A'},
    {'<', 'v', '>'}
  };

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Position move(Position current, char direction, char[][] keypad) {
    int newRow = current.row, newCol = current.col;

    switch (direction) {
      case '^':
        newRow = Math.max(0, current.row - 1);
        break;
      case 'v':
        newRow = Math.min(keypad.length - 1, current.row + 1);
        break;
      case '<':
        newCol = Math.max(0, current.col - 1);
        break;
      case '>':
        newCol = Math.min(keypad[0].length - 1, current.col + 1);
        break;
    }

    if (keypad[newRow][newCol] == ' ') {
      return current;
    }
    return new Position(newRow, newCol);
  }

  private List<List<Character>> getAllShortestSequences(char[][] keypad, Position start, String target) {
    Queue<State> queue = new LinkedList<>();
    Map<String, Integer> visited = new HashMap<>();
    List<List<Character>> shortestSequences = new ArrayList<>();
    int shortestLength = Integer.MAX_VALUE;

    State initialState = new State(start, new ArrayList<>(), 0);
    queue.add(initialState);
    visited.put(getStateKey(initialState), 0);

    while (!queue.isEmpty()) {
      State current = queue.poll();

      if (current.sequence.size() > shortestLength) {
        continue;
      }

      if (current.matchedLength == target.length()) {
        if (current.sequence.size() < shortestLength) {
          shortestSequences.clear();
          shortestLength = current.sequence.size();
        }
        if (current.sequence.size() == shortestLength) {
          shortestSequences.add(new ArrayList<>(current.sequence));
        }
        continue;
      }

      for (char move : new char[]{'<', '>', '^', 'v', 'A'}) {
        Position newPos = new Position(current.position.row, current.position.col);
        int newMatchedLength = current.matchedLength;

        if (move == 'A') {
          if (newMatchedLength < target.length() &&
            keypad[newPos.row][newPos.col] == target.charAt(newMatchedLength)) {
            newMatchedLength++;
          } else {
            continue;
          }
        } else {
          newPos = move(newPos, move, keypad);
        }

        List<Character> newSequence = new ArrayList<>(current.sequence);
        newSequence.add(move);
        State newState = new State(newPos, newSequence, newMatchedLength);

        String stateKey = getStateKey(newState);
        Integer previousLength = visited.get(stateKey);

        if (previousLength == null || newSequence.size() <= previousLength) {
          visited.put(stateKey, newSequence.size());
          queue.add(newState);
        }
      }
    }

    return shortestSequences;
  }

  private String toStringList(List<Character> sequence) {
    StringBuilder sb = new StringBuilder();
    for (char c : sequence) sb.append(c);
    return sb.toString();
  }

  private String getStateKey(State state) {
    return state.position.row + "," + state.position.col + "," + state.matchedLength;
  }

  public String partOne() {
    loadInput("2024/Day21/input.txt");

    int totalComplexity = 0;
    for (String code : input) {
      List<List<Character>> numericSequences = getAllShortestSequences(NUMERIC_KEYPAD, new Position(3, 2), code);

      int shortestOverallLength = Integer.MAX_VALUE;
      for (List<Character> numericSequence : numericSequences) {
        List<List<Character>> firstRobotPaths = getAllShortestSequences(DIRECTIONAL_KEYPAD, new Position(0, 2), toStringList(numericSequence));

        for (List<Character> firstRobotPath : firstRobotPaths) {
          List<List<Character>> secondRobotPaths = getAllShortestSequences(DIRECTIONAL_KEYPAD, new Position(0, 2), toStringList(firstRobotPath));

          for (List<Character> secondRobotPath : secondRobotPaths) {
            shortestOverallLength = Math.min(shortestOverallLength, secondRobotPath.size());
          }
        }
      }

      int numericValue = Integer.parseInt(code.replaceAll("[^0-9]", ""));
      totalComplexity += shortestOverallLength * numericValue;
    }

    return Integer.toString(totalComplexity);
  }

  public String partTwo() {
    loadInput("2024/Day21/input.txt");
    return Integer.toString(0);
  }
}

