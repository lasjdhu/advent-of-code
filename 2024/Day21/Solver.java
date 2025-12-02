package Day21;

import java.io.IOException;
import java.util.*;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();

  private static final char[][] NUMERIC_LAYOUT = {
    {'7','8','9'},
    {'4','5','6'},
    {'1','2','3'},
    {' ','0','A'}
  };

  private static final char[][] DIR_LAYOUT = {
    {' ','^','A'},
    {'<','v','>'}
  };

  private Map<Point, Character> numericPad = new HashMap<>();
  private Map<Point, Character> directionalPad = new HashMap<>();

  private Map<Pair<Character, Character>, List<String>> numericPaths;
  private Map<Pair<Character, Character>, List<String>> directionalPaths;

  private Map<Pair<String, Integer>, Long> cache;

  private record Point(int r,int c) {
    List<Point> cardinals() {
      return List.of(
        new Point(r-1,c),
        new Point(r+1,c),
        new Point(r,c-1),
        new Point(r,c+1)
      );
    }

    char diffToChar(Point other) {
      if (other.r == r-1) return '^';
      if (other.r == r+1) return 'v';
      if (other.c == c-1) return '<';
      if (other.c == c+1) return '>';
      throw new IllegalArgumentException("Invalid move from " + this + " to " + other);
    }
  }

  private record Pair<A,B>(A first, B second){}

  public Solver() {
    buildPads();
    precomputePaths();
  }

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void buildPads() {
    for (int r = 0; r < NUMERIC_LAYOUT.length; r++)
      for (int c = 0; c < NUMERIC_LAYOUT[0].length; c++)
        if (NUMERIC_LAYOUT[r][c] != ' ')
          numericPad.put(new Point(r,c), NUMERIC_LAYOUT[r][c]);

    for (int r = 0; r < DIR_LAYOUT.length; r++)
      for (int c = 0; c < DIR_LAYOUT[0].length; c++)
        if (DIR_LAYOUT[r][c] != ' ')
          directionalPad.put(new Point(r,c), DIR_LAYOUT[r][c]);
  }

  private void precomputePaths() {
    numericPaths = computeMinimalPaths(numericPad);
    directionalPaths = computeMinimalPaths(directionalPad);
  }

  private Map<Pair<Character, Character>, List<String>> computeMinimalPaths(Map<Point, Character> pad) {
    Map<Pair<Character, Character>, List<String>> paths = new HashMap<>();
    List<Point> points = new ArrayList<>(pad.keySet());
    for (Point start : points) {
      for (Point end : points) {
        List<String> pathList = bfsAllPaths(pad, start, end);
        paths.put(new Pair<>(pad.get(start), pad.get(end)), pathList);
      }
    }
    return paths;
  }

  private List<String> bfsAllPaths(Map<Point, Character> pad, Point start, Point end) {
    Queue<List<Point>> queue = new ArrayDeque<>();
    queue.add(List.of(start));
    Map<Point, Integer> minDist = new HashMap<>();
    minDist.put(start, 0);
    List<String> result = new ArrayList<>();
    int costAtGoal = -1;

    while (!queue.isEmpty()) {
      List<Point> path = queue.poll();
      Point current = path.get(path.size() - 1);
      int cost = path.size() - 1;

      if (costAtGoal != -1 && cost > costAtGoal) break;

      if (current.equals(end)) {
        costAtGoal = cost;
        result.add(pathToString(path) + "A");
        continue;
      }

      for (Point n : current.cardinals()) {
        if (pad.containsKey(n)) {
          int newDist = cost + 1;
          if (minDist.getOrDefault(n, Integer.MAX_VALUE) >= newDist) {
            minDist.put(n, newDist);
            List<Point> newPath = new ArrayList<>(path);
            newPath.add(n);
            queue.add(newPath);
          }
        }
      }
    }

    return result;
  }

  private String pathToString(List<Point> path) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < path.size()-1; i++)
      sb.append(path.get(i).diffToChar(path.get(i+1)));
    return sb.toString();
  }

  private long findCost(String code, int depth, Map<Pair<Character, Character>, List<String>> transitions) {
    if (cache == null) cache = new HashMap<>();
    Pair<String, Integer> key = new Pair<>(code, depth);
    if (cache.containsKey(key)) return cache.get(key);

    long total = 0;
    String seq = "A" + code;

    for (int i = 0; i < seq.length() - 1; i++) {
      char from = seq.charAt(i);
      char to = seq.charAt(i+1);
      List<String> paths = transitions.get(new Pair<>(from, to));
      if (depth == 0) {
        total += paths.stream().mapToInt(String::length).min().orElseThrow();
      } else {
        long min = Long.MAX_VALUE;
        for (String path : paths) {
          long val = findCost(path, depth - 1, directionalPaths);
          if (val < min) min = val;
        }
        total += min;
      }
    }

    cache.put(key, total);
    return total;
  }

  private long solve(int depth) {
    long total = 0;
    for (String code : input) {
      long numeric = Long.parseLong(code.substring(0, code.length()-1));
      total += findCost(code, depth, numericPaths) * numeric;
    }
    return total;
  }

  public String partOne() {
    loadInput("2024/Day21/input.txt");
    return Long.toString(solve(2));
  }

  public String partTwo() {
    loadInput("2024/Day21/input.txt");
    return Long.toString(solve(25));
  }
}

