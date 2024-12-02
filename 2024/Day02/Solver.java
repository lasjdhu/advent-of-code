package Day02;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import utils.FileUtils;

public class Solver {

  private ArrayList<ArrayList<Integer>> reports = new ArrayList<>();

  private void loadInput(String filename) {
    try {
      List<String> input = FileUtils.readLines(filename);
      reports.clear();

      for (String line : input) {
        if (line.trim().isEmpty()) continue;

        String[] numbers = line.split(" ");
        ArrayList<Integer> tempList = new ArrayList<>();
        for (String num : numbers) {
          tempList.add(Integer.parseInt(num.trim()));
        }
        reports.add(tempList);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean isReportSafe(ArrayList<Integer> line) {
    int reportSize = line.size();
    int safeReportsCnt = 0;
    
    ArrayList<Integer> ascending = new ArrayList<>(line);
    ArrayList<Integer> descending = new ArrayList<>(line);
    Collections.sort(ascending);
    Collections.sort(descending, Collections.reverseOrder());
    
    if (line.equals(ascending) || line.equals(descending)) {
      for (int i = 0; i < reportSize - 1; i++) {
        int diff = Math.abs(line.get(i) - line.get(i + 1));
        if (diff >= 1 && diff <= 3) {
          safeReportsCnt += 1;
        }
      }
      return reportSize - 1 == safeReportsCnt;
    }
    return false;
  }
  
  public String partOne() {
    return partOne(false);
  }
  
  public String partOne(boolean tolerateOneDiff) {
    loadInput("2024/Day02/input.txt");
    int result = 0;
    
    for (ArrayList<Integer> line : reports) {
      if (!tolerateOneDiff) {
        if (isReportSafe(line)) {
          result += 1;
        }
      } else {
        boolean isSafeAfterRemoval = false;
        for (int j = 0; j < line.size(); j++) {
          ArrayList<Integer> modifiedLine = new ArrayList<>(line);
          modifiedLine.remove(j);
          
          if (isReportSafe(modifiedLine)) {
            isSafeAfterRemoval = true;
            break;
          }
        }
        
        if (isSafeAfterRemoval || isReportSafe(line)) {
          result += 1;
        }
      }
    }
    
    return Integer.toString(result);
  }
  
  public String partTwo() {
    loadInput("2024/Day02/input.txt");
    return partOne(true);
  }
}
