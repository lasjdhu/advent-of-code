function hasExactRepeat(id) {
  const even = id.length % 2 === 0
  if (!even) return false

  return id.substr(0, id.length / 2) === id.substr(id.length / 2)
}

function isRepeatedPattern(id) {
  if (id.length < 2) return false

  const idRepeated = (id + id).slice(1, -1)
  return idRepeated.includes(id)
}

export function solveFirst(line) {
  let sum = 0

  const rangesStr = line.split(",").map((r) => r.trim())

  for (const rangeStr of rangesStr) {
    const [startStr, finishStr] = rangeStr.split("-")
    const start = Number(startStr)
    const finish = Number(finishStr)

    if (startStr.charAt(0) === "0") sum += start
    if (finishStr.charAt(0) === "0") sum += finish

    for (let id = start; id <= finish; id++) {
      if (hasExactRepeat(id.toString())) sum += id
    }
  }

  return sum
}

export function solveSecond(line) {
  let sum = 0

  const rangesStr = line.split(",").map((r) => r.trim())

  for (const rangeStr of rangesStr) {
    const [startStr, finishStr] = rangeStr.split("-")
    const start = Number(startStr)
    const finish = Number(finishStr)

    if (startStr.charAt(0) === "0") sum += start
    if (finishStr.charAt(0) === "0") sum += finish

    for (let id = start; id <= finish; id++) {
      if (isRepeatedPattern(id.toString())) sum += id
    }
  }

  return sum
}

