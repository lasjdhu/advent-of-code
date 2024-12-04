package Day04;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  private char[][] createMatrixFromInput() {
    int numRows = input.size();
    int numCols = input.get(0).length();

    char[][] matrix = new char[numRows][numCols];

    for (int i = 0; i < numRows; i++) {
      matrix[i] = input.get(i).toCharArray();
    }

    return matrix;
  }

  private String[] getSearchableStrings() {
    char[][] matrix = createMatrixFromInput();

    StringBuilder inputAsString = new StringBuilder();
    StringBuilder inputAsStringBackwards = new StringBuilder();
    StringBuilder inputAsStringVertical = new StringBuilder();
    StringBuilder inputAsStringVerticalBackwards = new StringBuilder();
    StringBuilder inputAsStringDiagonalLeft = new StringBuilder();
    StringBuilder inputAsStringDiagonalLeftBackwards = new StringBuilder();
    StringBuilder inputAsStringDiagonalRight = new StringBuilder();
    StringBuilder inputAsStringDiagonalRightBackwards = new StringBuilder();

    int rows = matrix.length;
    int cols = matrix[0].length;

    for (int i = 0; i < rows; i++) {
      StringBuilder rowString = new StringBuilder();
      for (int j = 0; j < cols; j++) {
        rowString.append(matrix[i][j]);
      }
      inputAsString.append(rowString).append("\n");

      StringBuilder rowStringBackwards = new StringBuilder(rowString).reverse();
      inputAsStringBackwards.append(rowStringBackwards).append("\n");
    }

    for (int j = 0; j < cols; j++) {
      StringBuilder colString = new StringBuilder();
      for (int i = 0; i < rows; i++) {
        colString.append(matrix[i][j]);
      }
      inputAsStringVertical.append(colString).append("\n");

      StringBuilder colStringBackwards = new StringBuilder(colString).reverse();
      inputAsStringVerticalBackwards.append(colStringBackwards).append("\n");
    }

    for (int d = 0; d < rows + cols - 1; d++) {
      StringBuilder diagonalLeft = new StringBuilder();
      for (int i = 0; i < rows; i++) {
        int j = d - i;
        if (j >= 0 && j < cols) {
          diagonalLeft.append(matrix[i][j]);
        }
      }
      inputAsStringDiagonalLeft.append(diagonalLeft).append("\n");

      StringBuilder diagonalLeftBackwards = new StringBuilder(diagonalLeft).reverse();
      inputAsStringDiagonalLeftBackwards.append(diagonalLeftBackwards).append("\n");
    }

    for (int d = 0; d < rows + cols - 1; d++) {
      StringBuilder diagonalRight = new StringBuilder();
      for (int i = 0; i < rows; i++) {
        int j = (cols - 1) - (d - i);
        if (j >= 0 && j < cols) {
          diagonalRight.append(matrix[i][j]);
        }
      }
      inputAsStringDiagonalRight.append(diagonalRight).append("\n");

      StringBuilder diagonalRightBackwards = new StringBuilder(diagonalRight).reverse();
      inputAsStringDiagonalRightBackwards.append(diagonalRightBackwards).append("\n");
    }

    String[] searchableStrings = {
      inputAsString.toString(),
      inputAsStringBackwards.toString(),
      inputAsStringVertical.toString(),
      inputAsStringVerticalBackwards.toString(),
      inputAsStringDiagonalLeft.toString(),
      inputAsStringDiagonalLeftBackwards.toString(),
      inputAsStringDiagonalRight.toString(),
      inputAsStringDiagonalRightBackwards.toString()
    };

    return searchableStrings;
  }

  private boolean isXMASPattern(char[][] matrix, int centerRow, int centerCol) {
    int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    for (int[] dir : directions) {
      int r1 = centerRow + dir[0];
      int c1 = centerCol + dir[1];
      int r2 = centerRow - dir[0];
      int c2 = centerCol - dir[1];

      if (r1 < 0 || r1 >= matrix.length || c1 < 0 || c1 >= matrix[0].length ||
          r2 < 0 || r2 >= matrix.length || c2 < 0 || c2 >= matrix[0].length) {
        return false;
      }

      String diag = "" + matrix[r1][c1] + matrix[centerRow][centerCol] + matrix[r2][c2];
      if (!diag.equals("MAS") && !diag.equals("SAM")) {
        return false;
      }
    }

    return true;
  }

  public String partOne() {
    loadInput("2024/Day04/input.txt");

    String[] searchableStrings = getSearchableStrings();

    Pattern pattern = Pattern.compile("XMAS");
    int totalOccurrences = 0;

    for (String str : searchableStrings) {
      Matcher matcher = pattern.matcher(str);
      while (matcher.find()) {
        totalOccurrences++;
      }
    }

    return Integer.toString(totalOccurrences);
  }

  public String partTwo() {
    loadInput("2024/Day04/input.txt");

    char[][] matrix = createMatrixFromInput();
    int xmasCount = 0;

    for (int row = 1; row < matrix.length - 1; row++) {
      for (int col = 1; col < matrix[0].length - 1; col++) {
        if (matrix[row][col] == 'A' && isXMASPattern(matrix, row, col)) {
          xmasCount++;
        }
      }
    }

    return Integer.toString(xmasCount);
  }
}
