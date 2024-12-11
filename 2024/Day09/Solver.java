package Day09;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import utils.FileUtils;

public class Solver {
  private String input = new String();
  private List<String> diskMap = new ArrayList<>();
  private String diskMapCopy = String.join("", diskMap);
  private Long checksum = 0L;

  private void loadInput(String filename) {
    try {
      input = null;
      diskMap.clear();
      input = FileUtils.readLine(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean isInteger(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
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

  private List<String> parseForBlocks() {
    String tmp = String.join("", diskMap);
    List<String> blocks = new ArrayList<>();

    StringBuilder currentBlock = new StringBuilder();
    for (int i = 0; i < tmp.length(); i++) {
      if (currentBlock.length() == 0) {
        currentBlock.append(tmp.charAt(i));
      } else if (tmp.charAt(i) == tmp.charAt(i - 1)) {
        currentBlock.append(tmp.charAt(i));
      } else {
        blocks.add(currentBlock.toString());
        currentBlock = new StringBuilder();
        currentBlock.append(tmp.charAt(i));
      }

      if (i == tmp.length() - 1) {
        blocks.add(currentBlock.toString());
      }
    }

    return blocks;
  }

  private void compactFiles() {
    List<String> blocks = parseForBlocks();

    int j = blocks.size() - 1;
    for (int i = 0; i < blocks.size(); i++) {
      if (isInteger(blocks.get(i))) {
        continue;
      } else {
        String currentBlock = blocks.get(i);
        while (j > 0 && blocks.get(j).length() > blocks.get(i).length() || !isInteger(blocks.get(j))) {
          j--;
        }

        if (isInteger(blocks.get(j)) && blocks.get(j).length() <= blocks.get(i).length() && j > i) {
          if (blocks.get(j).length() == blocks.get(i).length()) {
            blocks.set(i, blocks.get(j));
            blocks.set(j, currentBlock);
          } else if (blocks.get(j).length() < blocks.get(i).length()) {
            int diff = blocks.get(i).length() - blocks.get(j).length();
            blocks.set(i, blocks.get(j) + ".".repeat(diff));
            blocks.set(j, currentBlock.substring(0, currentBlock.length() - diff));
          }
          j--;
        }
      }
    }

    diskMap.clear();
    for (String block : blocks) {
      for (int i = 0; i < block.length(); i++) {
        diskMap.add(Character.toString(block.charAt(i)));
      }
    }

    if (diskMapCopy.equals(String.join("", diskMap))) {
      return;
    }
    diskMapCopy = String.join("", diskMap);
    compactFiles();
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

    checksum = 0L;

    generateDiskMap();
    compactSpace();
    getChecksum();

    return Long.toString(checksum);
  }

  public String partTwo() {
    loadInput("2024/Day09/input.txt");

    checksum = 0L;

    generateDiskMap();
    diskMapCopy = String.join("", diskMap);
    compactFiles();
    getChecksum();

    return Long.toString(checksum);
  }
}
