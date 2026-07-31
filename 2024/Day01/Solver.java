package Day01;

import java.io.IOException;
import java.util.HashMap;
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

    HashMap<Integer, Integer> rightFrequencyMap = new HashMap<>();
    for (Integer value : right) {
      rightFrequencyMap.put(value, rightFrequencyMap.getOrDefault(value, 0) + 1);
    }

    for (Integer value : left) {
      int frequency = rightFrequencyMap.getOrDefault(value, 0);
      result += value * frequency;
    }

    return Integer.toString(result);
  }
}
