#ifndef FILE_UTILS_H
#define FILE_UTILS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char **read_lines(const char *filename, int *countOut);
char *read_line(const char *filename);
void free_lines(char **lines, int count);

#endif
