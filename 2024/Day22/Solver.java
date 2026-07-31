package Day22;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

import utils.FileUtils;

class PriceKey {
  private int a, b, c, d;

  public PriceKey(int a, int b, int c, int d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PriceKey priceKey = (PriceKey) o;
    return a == priceKey.a && b == priceKey.b && c == priceKey.c && d == priceKey.d;
  }

  @Override
  public int hashCode() {
    return Objects.hash(a, b, c, d);
  }
}

public class Solver {

  private List<String> input = new ArrayList<>();
  private Map<Long, Long> secretNumbers = new HashMap<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      secretNumbers.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void getFirstSecretNumber() {
    for (String line : input) {
      long first = Long.parseLong(line);
      secretNumbers.put(first, first);
    }
  }

  private long mix (Long result, Long number) {
    return result ^ number;
  }

  private long prune (Long number) {
    return number % 16777216;
  }

  private void generateNextNumber(Long key) {
    long last = secretNumbers.get(key);

    long stepOne = last * 64;
    stepOne = mix(stepOne, last);
    stepOne = prune(stepOne);

    long stepTwo = (long) Math.floor(stepOne / 32);
    stepTwo = mix(stepTwo, stepOne);
    stepTwo = prune(stepTwo);

    long stepThree = stepTwo * 2048;
    stepThree = mix(stepThree, stepTwo);
    stepThree = prune(stepThree);

    secretNumbers.put(key, stepThree);
  }

  public String partOne() {
    loadInput("2024/Day22/input.txt");
    getFirstSecretNumber();

    for (Long key : secretNumbers.keySet()) {
      for (int i = 0; i < 2000; i++) {
        generateNextNumber(key);
      }
    }

    long total = secretNumbers.values().stream().mapToLong(Long::longValue).sum();

    return Long.toString(total);
  }

  public String partTwo() {
    loadInput("2024/Day22/input.txt");
    Map<PriceKey, Integer> bestPrices = new HashMap<>();
    getFirstSecretNumber();

    for (Long key : secretNumbers.keySet()) {
      Set<PriceKey> visited = new HashSet<>();
      long currentSecret = secretNumbers.get(key);
      int price = (int) (currentSecret % 10);

      Integer a = null, b = null, c = null, d;

      for (int i = 0; i < 2000; i++) {
        generateNextNumber(key);
        currentSecret = secretNumbers.get(key);
        int newPrice = (int) (currentSecret % 10);
        d = newPrice - price;

        if (a != null) {
          PriceKey priceKey = new PriceKey(a, b, c, d);
          if (!visited.contains(priceKey)) {
            visited.add(priceKey);
            bestPrices.merge(priceKey, newPrice, Integer::sum);
          }
        }

        a = b;
        b = c;
        c = d;
        price = newPrice;
      }
    }

    secretNumbers.clear();
    int result = bestPrices.values().stream().mapToInt(Integer::intValue).max().orElse(0);

    return Integer.toString(result);
  }
}
