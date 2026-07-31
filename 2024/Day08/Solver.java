package Day08;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import utils.FileUtils;

public class Solver {
  private List<String> input;

  private void loadInput(String filename) {
    try {
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<Character, Set<List<Integer>>> findInitialCoordinates() {
    Map<Character, Set<List<Integer>>> coordinates = new HashMap<>();
    for (int y = 0; y < input.size(); y++) {
      String line = input.get(y);
      for (int x = 0; x < line.length(); x++) {
        char symbol = line.charAt(x);
        if (symbol != '.') {
          coordinates.computeIfAbsent(symbol, k -> new HashSet<>()).add(List.of(x, y));
        }
      }
    }
    return coordinates;
  }

  private boolean isValidCoordinate(List<Integer> coord, int width, int height) {
    int x = coord.get(0);
    int y = coord.get(1);
    return x >= 0 && y >= 0 && y < height && x < width;
  }

  private Set<List<Integer>> calculateAntinodes(Map<Character, Set<List<Integer>>> initialCoords) {
    Set<List<Integer>> antinodes = new HashSet<>();
    int width = input.get(0).length();
    int height = input.size();

    for (Set<List<Integer>> symbolCoords : initialCoords.values()) {
      List<List<Integer>> coordList = new ArrayList<>(symbolCoords);

      for (int i = 0; i < coordList.size(); i++) {
        for (int j = 0; j < coordList.size(); j++) {
          if (i == j) continue;

          List<Integer> coord1 = coordList.get(i);
          List<Integer> coord2 = coordList.get(j);

          int dx = coord2.get(0) - coord1.get(0);
          int dy = coord2.get(1) - coord1.get(1);

          List<Integer> antinode1 = calculateAntinode(coord1, coord2, dx, dy, true);
          List<Integer> antinode2 = calculateAntinode(coord1, coord2, dx, dy, false);

          if (isValidCoordinate(antinode1, width, height)) {
            antinodes.add(antinode1);
          }
          if (isValidCoordinate(antinode2, width, height)) {
            antinodes.add(antinode2);
          }
        }
      }
    }
    return antinodes;
  }

  private List<Integer> calculateAntinode(List<Integer> coord1, List<Integer> coord2, 
                                          int dx, int dy, boolean isFirst) {
    int minX = Math.min(coord1.get(0), coord2.get(0));
    int maxX = Math.max(coord1.get(0), coord2.get(0));
    int minY = Math.min(coord1.get(1), coord2.get(1));
    int maxY = Math.max(coord1.get(1), coord2.get(1));

    return Integer.signum(dx) == Integer.signum(dy) 
      ? (isFirst 
        ? List.of(minX - Math.abs(dx), minY - Math.abs(dy))
        : List.of(maxX + Math.abs(dx), maxY + Math.abs(dy)))
      : (isFirst
        ? List.of(minX - Math.abs(dx), maxY + Math.abs(dy))
        : List.of(maxX + Math.abs(dx), minY - Math.abs(dy)));
  }

  private Set<List<Integer>> propagateCoverage(Map<Character, Set<List<Integer>>> initialCoords) {
    Set<List<Integer>> coveredCoords = new HashSet<>();
    int width = input.get(0).length();
    int height = input.size();

    for (Set<List<Integer>> symbolCoords : initialCoords.values()) {
      coveredCoords.addAll(symbolCoords);

      for (List<Integer> coord1 : symbolCoords) {
        for (List<Integer> coord2 : symbolCoords) {
          if (coord1.equals(coord2)) continue;

          int dx = coord2.get(0) - coord1.get(0);
          int dy = coord2.get(1) - coord1.get(1);

          List<Integer> nextCoord = List.of(
            coord2.get(0) + dx, 
            coord2.get(1) + dy
          );

          while (isValidCoordinate(nextCoord, width, height)) {
            coveredCoords.add(nextCoord);
            nextCoord = List.of(
              nextCoord.get(0) + dx, 
              nextCoord.get(1) + dy
            );
          }
        }
      }
    }
    return coveredCoords;
  }

  public String solve(boolean propagate) {
    loadInput("2024/Day08/input.txt");

    Map<Character, Set<List<Integer>>> initialCoords = findInitialCoordinates();

    Set<List<Integer>> resultCoords = propagate
      ? propagateCoverage(initialCoords)
      : calculateAntinodes(initialCoords);

    return Integer.toString(resultCoords.size());
  }

  public String partOne() {
    return solve(false);
  }

  public String partTwo() {
    return solve(true);
  }
}
