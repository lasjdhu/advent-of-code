fn main() {
    let input = include_str!("./input.txt");
    let output = get_possible_count(input);
    dbg!(output);
}

fn get_possible_count(input: &str) -> i32 {
    let mut sum = 0;

    for line in input.split("\n").filter(|&s| !s.is_empty()) {
        let prefix = get_prefix_indices(line);
        let id = get_id(line, prefix[0], prefix[1]);

        // prefix[1] + 2 because we also want to skip ": " part
        let line_wo_prefix = remove_prefix(line, prefix[1] + 2);
        let sets = get_sets(&line_wo_prefix);

        let mut is_string_possible = true;
        for set in sets {
            if !is_possible(&set) {
                is_string_possible = false;
            }
        }

        if is_string_possible {
            sum += id;
        }
    }

    sum
}

fn get_prefix_indices(line: &str) -> [usize; 2] {
    let start = 5; // index of the next char after "Game "
    let end = line.find(":").unwrap();

    [start, end]
}

fn get_id(line: &str, start: usize, end: usize) -> i32 {
    line[start..end]
        .parse::<i32>()
        .unwrap()
}

fn remove_prefix(line: &str, to_index: usize) -> String {
    let mut line_wo_prefix = String::from(line);
    line_wo_prefix.replace_range(0..to_index, "");

    line_wo_prefix
}

fn get_sets(line: &str) -> Vec<[i32; 3]> {
    let mut result: Vec<[i32; 3]> = Vec::new();

    let parts = line.split("; ");

    for part in parts {
        let mut rgb: [i32; 3] = [0, 0, 0];

        for map in part.split(", ") {
            if map.contains("red") {
                if let Some(digit_str) = map.split_whitespace().next() {
                    if let Ok(digit) = digit_str.parse::<i32>() {
                        rgb[0] = digit;
                    }
                }
            } else if map.contains("green") {
                if let Some(digit_str) = map.split_whitespace().next() {
                    if let Ok(digit) = digit_str.parse::<i32>() {
                        rgb[1] = digit;
                    }
                }
            } else if map.contains("blue") {
                if let Some(digit_str) = map.split_whitespace().next() {
                    if let Ok(digit) = digit_str.parse::<i32>() {
                        rgb[2] = digit;
                    }
                }
            }
        }

        result.push(rgb);
    }

    result
}

fn is_possible(array: &[i32; 3]) -> bool {
    if array[0] <= 12 && array[1] <= 13 && array[2] <= 14 {
        return true
    }

    return false
}
