import { solveFirst, solveSecond } from "./solver.js";
import { readLine } from "../utils/fileUtils.js";

const lines = await readLine("2025/Day02/input.txt")

const resultFirst = solveFirst(lines)
console.log(resultFirst)

const resultSecond = solveSecond(lines);
console.log(resultSecond);
