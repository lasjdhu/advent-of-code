package Day25;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<List<Integer>> locks = new ArrayList<>();
  private List<List<Integer>> keys = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      locks.clear();
      keys.clear();
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseInput() {
    List<String> currentBlock = new ArrayList<>();

    for (String line : input) {
      if (line.trim().isEmpty()) {
        if (!currentBlock.isEmpty()) {
          processBlock(currentBlock);
          currentBlock.clear();
        }
        continue;
      }
      currentBlock.add(line);
    }

    if (!currentBlock.isEmpty()) {
      processBlock(currentBlock);
    }
  }

  private void processBlock(List<String> block) {
    if (block.isEmpty()) return;

    boolean isLock = block.get(0).charAt(0) == '#';
    List<Integer> heights = new ArrayList<>();

    int columnCount = block.get(0).length();
    for (int col = 0; col < columnCount; col++) {
      int height = 0;
      for (String row : block) {
        if (row.charAt(col) == '#') {
          height++;
        }
      }
      heights.add(height - 1);
    }

    if (isLock) {
      locks.add(heights);
    } else {
      keys.add(heights);
    }
  }

  private boolean isFit(List<Integer> lock, List<Integer> key, int maxHeight) {
    for (int i = 0; i < lock.size(); i++) {
      if (lock.get(i) + key.get(i) > maxHeight) {
        return false;
      }
    }
    return true;
  }

  public String partOne() {
    loadInput("2024/Day25/input.txt");

    parseInput();

    int maxHeight = input.get(0).length();
    int validFitCount = 0;

    for (List<Integer> lock : locks) {
      for (List<Integer> key : keys) {
        if (isFit(lock, key, maxHeight)) {
          validFitCount++;
        }
      }
    }

    return Integer.toString(validFitCount);
  }
}
