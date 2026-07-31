import { solveFirst, solveSecond } from "./solver.js";
import { readLines } from "../utils/fileUtils.js";

const lines = await readLines("2025/Day10/input.txt");

const resultFirst = solveFirst(lines);
console.log(resultFirst);

const resultSecond = solveSecond(lines);
console.log(resultSecond);
