#include "solver.h"

int main() {
  const char *filename = "2015/Day01/input.txt";

  int resultFirst = solveFirst(filename);
  printf("%d\n", resultFirst);

  int resultSecond = solveSecond(filename);
  printf("%d\n", resultSecond);

  return 0;
}
