#include "solver.h"

int main() {
  const char *filename = "2015/Day02/input.txt";

  int result_first = solve_first(filename);
  printf("%d\n", result_first);

  int result_second = solve_second(filename);
  printf("%d\n", result_second);

  return 0;
}
