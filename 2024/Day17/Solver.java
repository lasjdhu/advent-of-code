package Day17;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.FileUtils;

class Instruction {
  int opcode;
  int operand;

  public Instruction(int opcode, int operand) {
    this.opcode = opcode;
    this.operand = operand;
  }
}

public class Solver {
  private List<String> input = new ArrayList<>();
  private String instString = "";
  private List<Instruction> instructions = new ArrayList<>();
  private long[] registers = new long[3];
  private List<Long> output = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseInput(Long overrideRegisterA) {
    instString = "";
    instructions.clear();
    output.clear();
    registers = new long[3];
    for (String line : input) {
      line = line.trim();
      if (line.isEmpty()) continue;

      if (line.startsWith("Register A:")) {
        if (overrideRegisterA != null) {
          registers[0] = overrideRegisterA;
        } else {
          registers[0] = Integer.parseInt(line.substring(11).trim());
        }
      } else if (line.startsWith("Register B:")) {
        registers[1] = Integer.parseInt(line.substring(11).trim());
      } else if (line.startsWith("Register C:")) {
        registers[2] = Integer.parseInt(line.substring(11).trim());
      } else if (line.startsWith("Program:")) {
        String[] parts = line.substring(8).trim().split(",");
        instString = line.substring(8).trim();
        for (int i = 0; i < parts.length - 1; i += 2) {
          int opcode = Integer.parseInt(parts[i].trim());
          int operand = Integer.parseInt(parts[i + 1].trim());
          instructions.add(new Instruction(opcode, operand));
        }
      }
    }
  }

  private long evaluateComboOperand(int operand) {
    if (operand >= 0 && operand <= 3) {
      return (long)operand;
    }
    return registers[operand - 4];
  }

  private void executeInstructions() {
    int ip = 0;

    while (ip < instructions.size()) {
      Instruction curr = instructions.get(ip);

      switch (curr.opcode) {
        case 0: // adv
          registers[0] = registers[0] / (1 << evaluateComboOperand(curr.operand));
          ip++;
          break;

        case 1: // bxl
          registers[1] ^= curr.operand;
          ip++;
          break;

        case 2: // bst
          registers[1] = evaluateComboOperand(curr.operand) & 7;
          ip++;
          break;

        case 3: // jnz
          if (registers[0] != 0) {
            ip = curr.operand;
          } else {
            ip++;
          }
          break;

        case 4: // bxc
          registers[1] ^= registers[2];
          ip++;
          break;

        case 5: // out
          long outValue = evaluateComboOperand(curr.operand) & 7;
          output.add(outValue);
          ip++;
          break;

        case 6: // bdv
          registers[1] = registers[0] / (1 << evaluateComboOperand(curr.operand));
          ip++;
          break;

        case 7: // cdv
          registers[2] = registers[0] / (1 << evaluateComboOperand(curr.operand));
          ip++;
          break;
      }
    }
  }

  private List<Long> runProgram(Long initialA) {
    parseInput(initialA);
    executeInstructions();
    return new ArrayList<>(output);
  }

  private List<Long> findMatchingProgramOutput(long lowerBound, long upperBound, int digitIndex, List<Long> targetOutput) {
    if (digitIndex < 0) {
      return new ArrayList<>();
    }

    long targetDigit = targetOutput.get(digitIndex - 1);
    List<Long> matchingResults = new ArrayList<>();

    long nextLowerBound = lowerBound;
    boolean isMatchingFound = false;

    for (long candidate = lowerBound; candidate <= upperBound; candidate += Math.pow(8, digitIndex - 1)) {
      long currentDigit = runProgram(candidate).get(digitIndex - 1);

      if (currentDigit != targetDigit) {
        if (isMatchingFound) {
          matchingResults.addAll(findMatchingProgramOutput(nextLowerBound, candidate, digitIndex - 1, targetOutput));
        }
        isMatchingFound = false;
        continue;
      }

      if (digitIndex == 1) {
        matchingResults.add(candidate);
        break;
      }

      if (!isMatchingFound) {
        nextLowerBound = candidate;
        isMatchingFound = true;
      }
    }

    return matchingResults;
  }

  public String partOne() {
    loadInput("2024/Day17/input.txt");

    runProgram(null);
    StringBuilder sb = new StringBuilder();
    for (long i : output) {
      sb.append(i);
      sb.append(",");
    }
    sb.deleteCharAt(sb.length() - 1);

    String result = String.valueOf(sb);
    return result;
  }

  public String partTwo() {
    loadInput("2024/Day17/input.txt");

    parseInput(null);
    List<Long> targetOutput = new ArrayList<>();
    for (String num : instString.split(",")) {
      targetOutput.add(Long.parseLong(num));
    }

    int numDigits = targetOutput.size();
    long minA = (long) Math.pow(8, numDigits - 1);
    long maxA = (long) Math.pow(8, numDigits);

    List<Long> possibleValues = findMatchingProgramOutput(minA, maxA, numDigits, targetOutput);
    long minPossibleValue = -1L;
    if (!possibleValues.isEmpty()) {
      minPossibleValue = possibleValues.get(0);
    }
    return Long.toString(minPossibleValue);
  }
}
