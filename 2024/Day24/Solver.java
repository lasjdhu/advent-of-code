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

  private List<Map.Entry<String, String>> getGateOperations() {
    return gateDefinitions.entrySet()
      .stream()
      .filter(e -> e.getValue().contains(" "))
      .collect(Collectors.toList());
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
    loadInput("2024/Day24/input.txt");
    parseInput();
    List<Map.Entry<String, String>> operations = getGateOperations();
    List<String> wrong = new ArrayList<>();

    for (Map.Entry<String, String> entry : operations) {
      String out = entry.getKey();
      String expr = entry.getValue();
      String[] tokens = expr.split(" ");
      if (tokens.length != 3) continue;
      String w1 = tokens[0];
      String op = tokens[1];
      String w2 = tokens[2];

      if (out.startsWith("z") && !op.equals("XOR") && !out.equals("z45")) {
        wrong.add(out);
      }

      if (op.equals("XOR") &&
        !(out.startsWith("x") || out.startsWith("y") || out.startsWith("z")) &&
        !(w1.startsWith("x") || w1.startsWith("y") || w1.startsWith("z")) &&
        !(w2.startsWith("x") || w2.startsWith("y") || w2.startsWith("z"))) {
        wrong.add(out);
      }

      if (op.equals("AND") && !w1.equals("x00") && !w2.equals("x00")) {
        for (Map.Entry<String, String> e2 : operations) {
          String[] tokens2 = e2.getValue().split(" ");
          if (tokens2.length == 3) {
            String w1_2 = tokens2[0];
            String w2_2 = tokens2[2];
            String op2 = tokens2[1];
            if ((out.equals(w1_2) || out.equals(w2_2)) && !op2.equals("OR")) {
              wrong.add(out);
              break;
            }
          }
        }
      }

      if (op.equals("XOR")) {
        for (Map.Entry<String, String> e2 : operations) {
          String[] tokens2 = e2.getValue().split(" ");
          if (tokens2.length == 3) {
            String w1_2 = tokens2[0];
            String w2_2 = tokens2[2];
            String op2 = tokens2[1];
            if ((out.equals(w1_2) || out.equals(w2_2)) && op2.equals("OR")) {
              wrong.add(out);
              break;
            }
          }
        }
      }
    }

    return wrong.stream().distinct().sorted().collect(Collectors.joining(","));
  }

}
