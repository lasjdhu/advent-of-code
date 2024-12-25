package Day24;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private Map<String, Integer> wires = new HashMap<>();
  private Map<String, String> gateDefinitions = new HashMap<>();

  private void loadInput(String filename) {
    try {
      wires.clear();
      gateDefinitions.clear();
      input.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseInput() {
    for (String line : input) {
      line = line.trim();
      if (line.isEmpty()){
        continue;
      }
      if (line.contains("->")) {
        String[] parts = line.split("->");
        String left = parts[0].trim();
        String right = parts[1].trim();
        gateDefinitions.put(right, left);
      } else if (line.contains(":")) {
        String[] parts = line.split(":");
        String left = parts[0].trim();
        String right = parts[1].trim();
        wires.put(left, Integer.parseInt(right));
      }
    }
  }

  private void simulateLogic() {
    while (!gateDefinitions.isEmpty()) {
      Map<String, String> unresolvedGates = new HashMap<>();
      for (Map.Entry<String, String> entry : gateDefinitions.entrySet()) {
        String outputWire = entry.getKey();
        String expression = entry.getValue();
        String[] tokens = expression.split(" ");
        if (tokens.length == 3) {
          String in1 = tokens[0].trim();
          String operator = tokens[1].trim();
          String in2 = tokens[2].trim();
          Integer val1 = wires.getOrDefault(in1, null);
          Integer val2 = wires.getOrDefault(in2, null);
          if (val1 != null && val2 != null) {
            switch (operator) {
              case "AND":
                wires.put(outputWire, val1 & val2);
                break;
              case "OR":
                wires.put(outputWire, val1 | val2);
                break;
              case "XOR":
                wires.put(outputWire, val1 ^ val2);
                break;
            }
          } else {
            unresolvedGates.put(outputWire, expression);
          }
        }
      }
      gateDefinitions = unresolvedGates;
    }
  }

  private String extractBinary(String prefix) {
    return wires.entrySet()
      .stream()
      .filter(entry -> entry.getKey().startsWith(prefix))
      .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
      .map(entry -> Integer.toString(entry.getValue()))
      .collect(Collectors.joining());
  }

  public String partOne() {
    loadInput("2024/Day24/input.txt");
    parseInput();
    simulateLogic();
    String bin = extractBinary("z");
    long decimal = Long.parseLong(bin, 2);
    return Long.toString(decimal);
  }

  public String partTwo() {
    loadInput("2024/Day24/mockupInput.txt");
    return "0";
  }
}
