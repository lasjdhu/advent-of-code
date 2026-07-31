package Day11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.FileUtils;

public class Solver {

  private Map<Long, Long> stones = new HashMap<>();

  private void loadInput(String filename) {
    try {
      stones.clear();
      String input = FileUtils.readLine(filename);
      for (String stoneStr : input.split(" ")) {
        stones.put(Long.parseLong(stoneStr), stones.getOrDefault(Long.parseLong(stoneStr), 0L) + 1);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void blink() {
    Map<Long, Long> newStones = new HashMap<>();

    for (Map.Entry<Long, Long> entry : stones.entrySet()) {
      long stone = entry.getKey();
      long count = entry.getValue();

      if (stone == 0) {
        newStones.put(1L, newStones.getOrDefault(1L, 0L) + count);
      } else {
        String stoneStr = Long.toString(stone);

        if (stoneStr.length() % 2 == 0) {
          long firstPart = Long.parseLong(stoneStr.substring(0, stoneStr.length() / 2));
          long secondPart = Long.parseLong(stoneStr.substring(stoneStr.length() / 2));

          newStones.put(firstPart, newStones.getOrDefault(firstPart, 0L) + count);
          newStones.put(secondPart, newStones.getOrDefault(secondPart, 0L) + count);
        } else {
          newStones.put(stone * 2024, newStones.getOrDefault(stone * 2024, 0L) + count);
        }
      }
    }

    stones = newStones;
  }

  public String partOne() {
    loadInput("2024/Day11/input.txt");
    long totalStoneCount = 0;

    for (int i = 0; i < 25; i++) {
      blink();
    }

    for (Long count : stones.values()) {
      totalStoneCount += count;
    }

    return String.valueOf(totalStoneCount);
  }

  public String partTwo() {
    loadInput("2024/Day11/input.txt");

    long totalStoneCount = 0;
    for (int i = 0; i < 75; i++) {
      blink();
    }

    for (Long count : stones.values()) {
      totalStoneCount += count;
    }

    return String.valueOf(totalStoneCount);
  }
}
