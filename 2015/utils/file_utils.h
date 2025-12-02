#ifndef FILE_UTILS_H
#define FILE_UTILS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char **readLines(const char *filename, int *countOut);
char *readLine(const char *filename);
void freeLines(char **lines, int count);

#endif
