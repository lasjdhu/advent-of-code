#include "solver.h"

int solve_first(const char *filename) {
  int result = 0;

  int count = 0;
  char **lines = read_lines(filename, &count);
  if (!lines) {
    fprintf(stderr, "Failed to read lines.\n");
    return 0;
  }

  for (int i = 0; i < count; i++) {
    int l, w, h;
    if (sscanf(lines[i], "%dx%dx%d", &l, &w, &h) != 3) {
      fprintf(stderr, "Invalid line format: %s\n", lines[i]);
      continue;
    }
    result += calc_area(l, w, h);
  }

  free_lines(lines, count);
  return result;
}

int solve_second(const char *filename) {
  int result = 0;

  int count = 0;
  char **lines = read_lines(filename, &count);
  if (!lines) {
    fprintf(stderr, "Failed to read lines.\n");
    return 0;
  }

  for (int i = 0; i < count; i++) {
    int l, w, h;
    if (sscanf(lines[i], "%dx%dx%d", &l, &w, &h) != 3) {
      fprintf(stderr, "Invalid line format: %s\n", lines[i]);
      continue;
    }
    result += calc_ribbon(l, w, h);
  }

  free_lines(lines, count);
  return result;
}

int calc_area(int l, int w, int h) {
  int first_side = l * w;
  int second_side = w * h;
  int third_side = h * l;

  int smallest_side = first_side;
  if (second_side < smallest_side)
    smallest_side = second_side;
  if (third_side < smallest_side)
    smallest_side = third_side;

  return 2 * first_side + 2 * second_side + 2 * third_side + smallest_side;
}

int calc_ribbon(int l, int w, int h) {
  int x = l, y = w, z = h;
  if (x > y) {
    int t = x;
    x = y;
    y = t;
  }
  if (x > z) {
    int t = x;
    x = z;
    z = t;
  }
  if (y > z) {
    int t = y;
    y = z;
    z = t;
  }

  int perimeter = 2 * (x + y);
  int bow = l * w * h;

  return perimeter + bow;
}
