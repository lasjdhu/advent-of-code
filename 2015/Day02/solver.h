#ifndef SOLVER_H
#define SOLVER_H

#include "../utils/file_utils.h"
#include <stdio.h>

int solve_first(const char *filename);
int solve_second(const char *filename);
int calc_area(int l, int w, int h);
int calc_ribbon(int l, int w, int h);

#endif
