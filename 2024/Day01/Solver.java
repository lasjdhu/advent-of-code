package Day01;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import utils.FileUtils;

public class Solver {

  private ArrayList<Integer> left = new ArrayList<>();
  private ArrayList<Integer> right = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      List<String> input = FileUtils.readLines(filename);

      left.clear();
      right.clear();

      for (String line : input) {
        if (line.trim().isEmpty()) continue;

        String[] numbers = line.split("   ");
        if (numbers.length == 2) {
          try {
            Integer leftEl = Integer.parseInt(numbers[0]);
            Integer rightEl = Integer.parseInt(numbers[1]);
            left.add(leftEl);
            right.add(rightEl);
          } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + line);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String partOne() {
    loadInput("2024/Day01/input.txt");

    Collections.sort(left);
    Collections.sort(right);

    if (left.size() != right.size()) {
      return "Lists don't match";
    }

    int result = 0;
    for (int i = 0; i < left.size(); i++) {
      result += Math.abs(left.get(i) - right.get(i));
    }

    return Integer.toString(result);
  }

  public String partTwo() {
    loadInput("2024/Day01/input.txt");

    if (left.size() != right.size()) {
      return "Lists don't match";
    }

    int result = 0;

    ArrayList<Integer> results = new ArrayList<>(Collections.nCopies(left.size(), 0));
    for (int i = 0; i < left.size(); i++) {
      Integer scoredValue = left.get(i);

      for (int j = 0; j < right.size(); j++) {
        if (right.get(j).equals(scoredValue)) {
          results.set(i, results.get(i) + right.get(j));
        }
      }
    }

    for (Integer score : results) {
      result += score;
    }

    return Integer.toString(result);
  }
}
