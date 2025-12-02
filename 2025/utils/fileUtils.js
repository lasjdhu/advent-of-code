import { readFile } from "fs/promises";

export async function readLines(filename) {
  try {
    const data = await readFile(filename, "utf-8")
    return data.split("\n").filter(l => l !== "")
  } catch (err) {
    console.error("Error reading file:", err)
    return []
  }
}

export async function readLine(filename) {
  try {
    const data = await readFile(filename, "utf-8")
    return data
  } catch (err) {
    console.error("Error reading file:", err)
    return ""
  }
}
