#include "file_utils.h"

char **readLines(const char *filename, int *countOut) {
  *countOut = 0;

  FILE *f = fopen(filename, "r");
  if (!f) {
    fprintf(stderr, "Unable to open file: %s\n", filename);
    return NULL;
  }

  size_t capacity = 32;
  char **lines = malloc(capacity * sizeof(char *));
  if (!lines) {
    fclose(f);
    return NULL;
  }

  char *line = NULL;
  size_t len = 0;
  ssize_t read;

  while ((read = getline(&line, &len, f)) != -1) {
    if (*countOut >= (int)capacity) {
      capacity *= 2;
      char **tmp = realloc(lines, capacity * sizeof(char *));
      if (!tmp) {
        free(line);
        fclose(f);
        return lines;
      }
      lines = tmp;
    }

    // Strip newline
    if (read > 0 && (line[read - 1] == '\n' || line[read - 1] == '\r')) {
      line[read - 1] = '\0';
      read--;
    }

    char *stored = malloc(read + 1);
    memcpy(stored, line, read + 1);

    lines[*countOut] = stored;
    (*countOut)++;
  }

  free(line);
  fclose(f);

  if (*countOut >= (int)capacity) {
    lines = realloc(lines, (capacity + 1) * sizeof(char *));
  }
  lines[*countOut] = NULL;

  return lines;
}

char *readLine(const char *filename) {
  FILE *f = fopen(filename, "rb");
  if (!f) {
    fprintf(stderr, "Unable to open file: %s\n", filename);
    return NULL;
  }

  fseek(f, 0, SEEK_END);
  long size = ftell(f);
  fseek(f, 0, SEEK_SET);

  if (size < 0) {
    fclose(f);
    return NULL;
  }

  char *buffer = malloc(size + 1);
  if (!buffer) {
    fclose(f);
    return NULL;
  }

  fread(buffer, 1, size, f);
  buffer[size] = '\0';

  fclose(f);
  return buffer;
}

void freeLines(char **lines, int count) {
  if (!lines)
    return;

  for (int i = 0; i < count; i++) {
    free(lines[i]);
  }
  free(lines);
}
