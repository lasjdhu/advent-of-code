package Day03;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String partOne() {
    return partOne(false);
  }

  public String partOne(boolean partTwoEnabled) {
    loadInput("2024/Day03/mockupInput.txt");
    List<Integer> muls = new ArrayList<>();
    boolean mulEnabled = true;

    for (String line : input) {
      for (int i = 0; i < line.length(); i++) {
        if (i > 2 &&
          line.charAt(i) == ')' &&
          line.charAt(i - 1) == '(' &&
          line.charAt(i - 2) == 'o' &&
          line.charAt(i - 3) == 'd') {
          mulEnabled = true;
          continue;
        }

        if (i > 4 &&
          line.charAt(i) == ')' &&
          line.charAt(i - 1) == '(' &&
          line.charAt(i - 2) == 't' &&
          line.charAt(i - 3) == '\'' &&
          line.charAt(i - 4) == 'n' &&
          line.charAt(i - 5) == 'o' &&
          line.charAt(i - 6) == 'd') {
          mulEnabled = false;
          continue;
        }

        if (i > 2 &&
          line.charAt(i) == '(' &&
          line.charAt(i - 1) == 'l' &&
          line.charAt(i - 2) == 'u' &&
          line.charAt(i - 3) == 'm') {

          if (mulEnabled || !partTwoEnabled) {
            StringBuilder currentMulsString = new StringBuilder();
            for (int j = i + 1; j < line.length(); j++) {
              char ch = line.charAt(j);
              if (Character.isDigit(ch) || ch == ',' || ch == ')') {
                if (ch == ')') {
                  String[] nums = currentMulsString.toString().split(",");
                  if (nums.length == 2) {
                    int num1 = Integer.parseInt(nums[0].trim());
                    int num2 = Integer.parseInt(nums[1].trim());
                    muls.add(num1 * num2);
                  }
                  break;
                } else {
                  currentMulsString.append(ch);
                }
              } else {
                break;
              }
            }
          }
        }
      }
    }

    return muls.stream().mapToInt(Integer::intValue).sum() + "";
  }

  public String partTwo() {
    return partOne(true);
  }
}
