import { solveFirst } from "./solver.js";
import { readLines } from "../utils/fileUtils.js";

const lines = await readLines("2025/Day12/input.txt");

const resultFirst = solveFirst(lines);
console.log(resultFirst);
