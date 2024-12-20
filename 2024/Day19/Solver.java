package Day19;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<String> patterns = new ArrayList<>();
  private List<String> designs = new ArrayList<>();
  private HashMap<String, Long> memo = new HashMap<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      patterns.clear();
      designs.clear();
      memo.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseInput() {
    for (int i = 0; i < input.size(); i++) {
      if (i == 0) {
        String[] parts = input.get(i).split(", ");
        for (String part : parts) {
          patterns.add(part);
        }
      } else if (i == 1) {
        continue;
      } else {
        designs.add(input.get(i));
      }
    }
  }

  private long processDesign(String design, boolean countingMode) {
    if (design.isEmpty()) {
      return 1L;
    }

    if (memo.containsKey(design)) {
      return countingMode ? memo.get(design) : (memo.get(design) > 0 ? 1L : 0L);
    }

    long result = 0L;
    for (String pattern : patterns) {
      if (design.startsWith(pattern)) {
        long subResult = processDesign(design.substring(pattern.length()), countingMode);
        if (!countingMode && subResult > 0) {
          result = 1L;
          break;
        }
        result += subResult;
      }
    }

    memo.put(design, result);
    return result;
  }

  public String partOne() {
    loadInput("2024/Day19/input.txt");

    parseInput();
    int validDesigns = 0;
    for (String design : designs) {
      if (processDesign(design, false) > 0) {
        validDesigns++;
      }
    }

    return Integer.toString(validDesigns);
  }

  public String partTwo() {
    loadInput("2024/Day19/input.txt");

    parseInput();
    long totalWays = 0;
    for (String design : designs) {
      totalWays += processDesign(design, true);
    }

    return Long.toString(totalWays);
  }
}

