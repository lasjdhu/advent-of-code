package Day07;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import utils.FileUtils;

public class Solver {
  private List<String> input = new ArrayList<>();
  private List<Entry> inputMap = new ArrayList<>();

  private class Entry {
    Long result;
    List<Long> operands;

    Entry(Long result, List<Long> operands) {
      this.result = result;
      this.operands = operands;
    }
  }

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createInputMap() {
    inputMap.clear();
    for (String line : input) {
      String[] dividedInput = line.split(":");
      Long result = Long.parseLong(dividedInput[0]);
      List<Long> operandsArr = new ArrayList<>();
      for (String operand : dividedInput[1].trim().split(" ")) {
        operandsArr.add(Long.parseLong(operand));
      }
      Entry entry = new Entry(result, operandsArr);
      inputMap.add(entry);
    }
  }

  private void permuteOperators(List<Long> operands, List<String> currentOps, List<String> result, boolean supportsConcat) {
    if (currentOps.size() == operands.size() - 1) {
      StringBuilder equation = new StringBuilder();
      for (int i = 0; i < operands.size(); i++) {
        equation.append(operands.get(i));
        if (i < currentOps.size()) {
          equation.append(" ").append(currentOps.get(i)).append(" ");
        }
      }
      result.add(equation.toString());
      return;
    }
    currentOps.add("+");
    permuteOperators(operands, currentOps, result, supportsConcat);
    currentOps.remove(currentOps.size() - 1);
    currentOps.add("*");
    permuteOperators(operands, currentOps, result, supportsConcat);
    currentOps.remove(currentOps.size() - 1);

    if (supportsConcat) {
      currentOps.add("||");
      permuteOperators(operands, currentOps, result, supportsConcat);
      currentOps.remove(currentOps.size() - 1);
    }
  }

  private long solveEquation(String equation) {
    String[] tokens = equation.split(" ");
    long result = Long.parseLong(tokens[0]);

    for (int i = 1; i < tokens.length; i += 2) {
      String operator = tokens[i];
      long nextOperand = Long.parseLong(tokens[i + 1]);

      switch (operator) {
        case "+":
          result += nextOperand;
          break;
        case "*":
          result *= nextOperand;
          break;
        case "||":
          result = Long.parseLong(result + "" + nextOperand);
          break;
      }
    }
    return result;
  }

  public String partOne() {
    return partOne(false);
  }

  public String partOne(boolean supportsConcat) {
    loadInput("2024/Day07/mockupInput.txt");
    createInputMap();
    long resultsCount = 0;

    for (Entry entry : inputMap) {
      List<String> equationPerms = new ArrayList<>();
      permuteOperators(entry.operands, new ArrayList<>(), equationPerms, supportsConcat);

      for (String equation : equationPerms) {
        long solvedResult = solveEquation(equation);
        if (entry.result == solvedResult) {
          resultsCount += entry.result;
          break;
        }
      }
    }
    return Long.toString(resultsCount);
  }

  public String partTwo() {
    return partOne(true);
  }
}
