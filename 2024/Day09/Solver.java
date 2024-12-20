package Day09;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import utils.FileUtils;

public class Solver {
  private String input = new String();
  private List<String> diskMap = new ArrayList<>();
  private Long checksum = 0L;

  private void loadInput(String filename) {
    try {
      input = null;
      diskMap.clear();
      checksum = 0L;
      input = FileUtils.readLine(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void generateDiskMap() {
    for (int index = 0; index < input.length(); index++) {
      int currNum = Character.getNumericValue(input.charAt(index));
      boolean isEven = index % 2 == 0;

      if (isEven) {
        for (int i = 0; i < currNum; i++) {
          int id = index / 2;
          diskMap.add(Integer.toString(id));
        }
      } else {
        for (int freeIdx = 0; freeIdx < currNum; freeIdx++) {
          diskMap.add(".");
        }
      }
    }
  }

  private void compactSpace() {
    int j = diskMap.size() - 1;

    for (int i = 0; i < diskMap.size(); i++) {
      if (diskMap.get(i).equals(".")) {
        while (j > i && diskMap.get(j).equals(".")) {
          j--;
        }

        if (j > i) {
          diskMap.set(i, diskMap.get(j));
          diskMap.set(j, ".");
          j--;
        }
      }
    }
  }

  private void compactFiles() {
    Map<Integer, List<Integer>> blocks = new HashMap<>();
    for (int i = 0; i < diskMap.size(); i++) {
      if (!diskMap.get(i).equals(".")) {
        int file = Integer.parseInt(diskMap.get(i));
        if (!blocks.containsKey(file)) {
          blocks.put(file, new ArrayList<>());
        }
        blocks.get(file).add(i);
      }
    }

    for (int file = blocks.size() - 1; file >= 0; file--) {
      List<Integer> positions = blocks.get(file);
      if (positions == null || positions.isEmpty()) continue;

      int fileSize = positions.size();
      int currentStart = positions.get(0);

      int bestStart = -1;
      int currentFreeStart = -1;
      int consecutiveFreeSpace = 0;

      for (int i = 0; i < currentStart; i++) {
        if (diskMap.get(i).equals(".")) {
          if (currentFreeStart == -1) {
            currentFreeStart = i;
          }
          consecutiveFreeSpace++;
          if (consecutiveFreeSpace >= fileSize) {
            bestStart = currentFreeStart;
            break;
          }
        } else {
          currentFreeStart = -1;
          consecutiveFreeSpace = 0;
        }
      }

      if (bestStart != -1) {
        for (int pos : positions) {
          diskMap.set(pos, ".");
        }

        for (int i = 0; i < fileSize; i++) {
          diskMap.set(bestStart + i, Integer.toString(file));
        }
      }
    }
  }

  private void getChecksum() {
    for (int i = 0; i < diskMap.size(); i++) {
      if (!diskMap.get(i).equals(".")) {
        int currNum = Integer.parseInt(diskMap.get(i));
        checksum += currNum * i;
      }
    }
  }

  public String partOne() {
    loadInput("2024/Day09/input.txt");

    generateDiskMap();
    compactSpace();
    getChecksum();

    return Long.toString(checksum);
  }

  public String partTwo() {
    loadInput("2024/Day09/input.txt");

    generateDiskMap();
    compactFiles();
    getChecksum();

    return Long.toString(checksum);
  }
}
