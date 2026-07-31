package Day23;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Collections;

import utils.FileUtils;

public class Solver {

  private List<String> input = new ArrayList<>();
  private HashMap<String, List<String>> connections = new HashMap<>();

  private void loadInput(String filename) {
    try {
      input.clear();
      connections.clear();
      input = FileUtils.readLines(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseInput() {
    for (String line : input) {
      String[] parts = line.split("-");
      String con1 = parts[0];
      String con2 = parts[1];

      connections.computeIfAbsent(con1, k -> new ArrayList<>()).add(con2);
      connections.computeIfAbsent(con2, k -> new ArrayList<>()).add(con1);
    }
  }

  private int countTriangles() {
    int count = 0;

    for (String node : connections.keySet()) {
      List<String> neighbors = connections.get(node);
      if (neighbors == null || neighbors.size() < 2) continue;

      for (int i = 0; i < neighbors.size(); i++) {
        for (int j = i + 1; j < neighbors.size(); j++) {
          String neighbor1 = neighbors.get(i);
          String neighbor2 = neighbors.get(j);

          if (connections.containsKey(neighbor1) && connections.get(neighbor1).contains(neighbor2)) {
            if (node.startsWith("t") || neighbor1.startsWith("t") || neighbor2.startsWith("t")) {
              count++;
            }
          }
        }
      }
    }

    return count / 3;
  }

  private void bronKerboschWithPivot(Set<String> clique, Set<String> potential, Set<String> excluded, Set<String> maxClique) {
    if (potential.isEmpty() && excluded.isEmpty()) {
      if (clique.size() > maxClique.size()) {
        maxClique.clear();
        maxClique.addAll(clique);
      }
      return;
    }

    String pivot = choosePivot(potential, excluded);
    Set<String> candidates = new HashSet<>(potential);
    if (pivot != null) {
      candidates.removeAll(new HashSet<>(connections.get(pivot)));
    }

    for (String vertex : candidates) {
      Set<String> newPotential = new HashSet<>(potential);
      newPotential.retainAll(connections.get(vertex));

      Set<String> newExcluded = new HashSet<>(excluded);
      newExcluded.retainAll(connections.get(vertex));

      clique.add(vertex);
      bronKerboschWithPivot(clique, newPotential, newExcluded, maxClique);
      clique.remove(vertex);

      potential.remove(vertex);
      excluded.add(vertex);
    }
  }

  private String choosePivot(Set<String> potential, Set<String> excluded) {
    Set<String> union = new HashSet<>(potential);
    union.addAll(excluded);
    if (union.isEmpty()) {
      return null;
    }

    String maxDegreeVertex = null;
    int maxDegree = -1;

    for (String vertex : union) {
      int degree = 0;
      for (String neighbor : new HashSet<>(connections.get(vertex))) {
        if (potential.contains(neighbor)) degree++;
      }
      if (degree > maxDegree) {
        maxDegree = degree;
        maxDegreeVertex = vertex;
      }
    }
    return maxDegreeVertex;
  }

  public String partOne() {
    loadInput("2024/Day23/input.txt");
    parseInput();
    return Integer.toString(countTriangles());
  }

  public String partTwo() {
    loadInput("2024/Day23/input.txt");
    parseInput();
    Set<String> maxClique = new HashSet<>();
    Set<String> potentialVertices = new HashSet<>(connections.keySet());
    Set<String> currentClique = new HashSet<>();
    bronKerboschWithPivot(currentClique, potentialVertices, new HashSet<>(), maxClique);
    List<String> sortedComputers = new ArrayList<>(maxClique);
    Collections.sort(sortedComputers);
    return String.join(",", sortedComputers);
  }
}
