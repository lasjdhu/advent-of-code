function pickLargestSubsequence(nums, count) {
  const stack = [];
  let toRemove = nums.length - count;

  for (let i = 0; i < nums.length; i++) {
    const d = nums[i];

    while (
      toRemove > 0 &&
      stack.length > 0 &&
      stack[stack.length - 1] < d
    ) {
      stack.pop();
      toRemove--;
    }

    stack.push(d);
  }

  while (toRemove > 0) {
    stack.pop();
    toRemove--;
  }

  return stack.slice(0, count);
}

export function solveFirst(lines) {
  let sum = 0;

  for (const line of lines) {
    const nums = line.split("").map(Number);
    const picks = pickLargestSubsequence(nums, 2);
    sum += Number(picks.join(""));
  }

  return sum;
}

export function solveSecond(lines) {
  let sum = 0;

  for (const line of lines) {
    const nums = line.split("").map(Number);
    const picks = pickLargestSubsequence(nums, 12);
    sum += Number(picks.join(""));
  }

  return sum;
}

