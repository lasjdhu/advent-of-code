#include "solver.h"
#include "../utils/file_utils.h"

int solveFirst(const char *filename) {
  int floor = 0;

  char *content = readLine(filename);
  if (!content) {
    fprintf(stderr, "Failed to read full content.\n");
    return 0;
  }

  int len = strlen(content);
  for (int i = 0; i < len; i++) {
    char c = content[i];

    if (c == '(')
      floor++;
    else if (c == ')')
      floor--;
  }

  free(content);
  return floor;
}

int solveSecond(const char *filename) {
  int floor = 0;
  int position = 0;

  char *content = readLine(filename);
  if (!content) {
    fprintf(stderr, "Failed to read full content.\n");
    return 0;
  }

  int len = strlen(content);
  for (int i = 0; i < len; i++) {
    char c = content[i];
    position++;

    if (c == '(')
      floor++;
    else if (c == ')')
      floor--;

    if (floor == -1)
      return position;
  }

  free(content);
  return position;
}
