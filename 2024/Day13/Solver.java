package Day13;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.FileUtils;

class Machine {
  public long[] buttonA;
  public long[] buttonB;
  public long[] prizeCoords;

  public Machine(long[] buttonA, long[] buttonB, long[] prizeCoords) {
    this.buttonA = buttonA;
    this.buttonB = buttonB;
    this.prizeCoords = prizeCoords;
  }
}

public class Solver {

  private String input;
  private List<Machine> machines = new ArrayList<>();
  private long totalTokens = 0;

  private void loadInput(String filename) {
    try {
      input = null;
      totalTokens = 0;
      machines.clear();
      List<String> lines = FileUtils.readLines(filename);
      input = String.join("\n", lines);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private long[] parseInfoA(String line) {
    Pattern pattern = Pattern.compile("Button A: X([+-]?\\d+), Y([+-]?\\d+)");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      long x = Long.parseLong(matcher.group(1));
      long y = Long.parseLong(matcher.group(2));
      return new long[]{x, y};
    }
    return null;
  }

  private long[] parseInfoB(String line) {
    Pattern pattern = Pattern.compile("Button B: X([+-]?\\d+), Y([+-]?\\d+)");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      long x = Long.parseLong(matcher.group(1));
      long y = Long.parseLong(matcher.group(2));
      return new long[]{x, y};
    }
    return null;
  }

  private long[] parseInfoPrize(String line) {
    Pattern pattern = Pattern.compile("Prize: X=([+-]?\\d+), Y=([+-]?\\d+)");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      long x = Long.parseLong(matcher.group(1));
      long y = Long.parseLong(matcher.group(2));
      return new long[]{x, y};
    }
    return null;
  }

  private void extractData() {
    String[] parts = input.split("\\n\\s*\\n");
    for (String block : parts) {
      String[] lines = block.split("\n");
      for (long i = 0; i < lines.length; i += 3) {
        long[] buttonA = parseInfoA(lines[0]);
        long[] buttonB = parseInfoB(lines[1]);
        long[] prizeCoords = parseInfoPrize(lines[2]);

        if (buttonA != null && buttonB != null && prizeCoords != null) {
          Machine machine = new Machine(buttonA, buttonB, prizeCoords);
          machines.add(machine);
        }
      }
    }
  }

  private void findByBruteForce(Machine machine) {
    long ax = machine.buttonA[0];
    long ay = machine.buttonA[1];
    long bx = machine.buttonB[0];
    long by = machine.buttonB[1];
    long prizeX = machine.prizeCoords[0];
    long prizeY = machine.prizeCoords[1];

    long bestTokens = Integer.MAX_VALUE;
    long bestA = 0;
    long bestB = 0;

    for (long a = 0; a <= 100; a++) {
      for (long b = 0; b <= 100; b++) {
        long finalX = ax * a + bx * b;
        long finalY = ay * a + by * b;

        if (finalX == prizeX && finalY == prizeY) {
          long tokens = a * 3 + b;

          if (tokens < bestTokens) {
            bestTokens = tokens;
            bestA = a;
            bestB = b;
          }
        }
      }
    }

    if (bestTokens < Integer.MAX_VALUE) {
      totalTokens += bestTokens;
    }
  }

  private void findByRule(Machine machine) {
    long ax = machine.buttonA[0];
    long ay = machine.buttonA[1];
    long bx = machine.buttonB[0];
    long by = machine.buttonB[1];
    long prizeX = machine.prizeCoords[0];
    long prizeY = machine.prizeCoords[1];

    prizeX += 10_000_000_000_000L;
    prizeY += 10_000_000_000_000L;

    long det = ax * by - bx * ay;
    if (det == 0) return;

    long a = prizeX * by - bx * prizeY;
    long b = ax * prizeY - prizeX * ay;
    if (a % det != 0 || b % det != 0) return;

    totalTokens += a / det * 3 + b / det;
  }

  public String partOne() {
    loadInput("2024/Day13/input.txt");

    extractData();

    for (Machine machine : machines) {
      findByBruteForce(machine);
    }

    return Long.toString(totalTokens);
  }

  public String partTwo() {
    loadInput("2024/Day13/input.txt");

    extractData();

    for (Machine machine : machines) {
      findByRule(machine);
    }

    return Long.toString(totalTokens);
  }
}

