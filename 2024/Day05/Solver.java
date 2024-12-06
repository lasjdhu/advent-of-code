package Day05;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private List<List<Integer>> rules = new ArrayList<>();
  private List<List<Integer>> updates = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void divideInput() {
    boolean updateStarted = false;

    for (String line : input) {
      if (line.trim().isEmpty()) {
        updateStarted = true;
        continue;
      } else if (!updateStarted) {
        String[] parts = line.split("\\|");
        List<Integer> rule = new ArrayList<>();
        rule.add(Integer.parseInt(parts[0].trim()));
        rule.add(Integer.parseInt(parts[1].trim()));
        rules.add(rule);
      } else if (updateStarted) {
        String[] numbers = line.split(",");
        List<Integer> row = new ArrayList<>();
        for (String number : numbers) {
          try {
            row.add(Integer.parseInt(number.trim()));
          } catch (NumberFormatException e) {
            System.out.println("Invalid number: " + number);
          }
        }
        updates.add(row);
      }
    }
  }

  private boolean isValidOrder(List<Integer> update, List<List<Integer>> rules) {
    for (List<Integer> rule : rules) {
      int before = rule.get(0);
      int after = rule.get(1);

      int beforeIdx = update.indexOf(before);
      int afterIdx = update.indexOf(after);

      if (beforeIdx != -1 && afterIdx != -1 && beforeIdx > afterIdx) {
        return false;
      }
    }
    return true;
  }

  private List<Integer> fixUpdateOrder(List<Integer> update, List<List<Integer>> rules) {
    List<Integer> fixedUpdate = new ArrayList<>(update);
    boolean changed = true;

    while (changed) {
      changed = false;
      for (List<Integer> rule : rules) {
        int before = rule.get(0);
        int after = rule.get(1);

        int beforeIdx = fixedUpdate.indexOf(before);
        int afterIdx = fixedUpdate.indexOf(after);

        if (beforeIdx != -1 && afterIdx != -1 && beforeIdx > afterIdx) {
          fixedUpdate.remove(beforeIdx);
          fixedUpdate.add(afterIdx, before);
          changed = true;
          break;
        }
      }
    };

    return fixedUpdate;
  }

  public String partOne() {
    loadInput("2024/Day05/input.txt");
    divideInput();
    int middlesCntValid = 0;

    for (List<Integer> update : updates) {
      boolean valid = isValidOrder(update, rules);

      if (valid) {
        int middleIdx = update.size() / 2;
        middlesCntValid += update.get(middleIdx);
      }
    }

    return Integer.toString(middlesCntValid);
  }

  public String partTwo() {
    loadInput("2024/Day05/input.txt");
    divideInput();
    int middlesCntInvalid = 0;

    for (List<Integer> update : updates) {
      boolean valid = isValidOrder(update, rules);

      if (!valid) {
        List<Integer> fixedUpdate = fixUpdateOrder(update, rules);
        int middleIdx = fixedUpdate.size() / 2;
        middlesCntInvalid += fixedUpdate.get(middleIdx);
      }
    }

    return Integer.toString(middlesCntInvalid / 2);
  }
}
