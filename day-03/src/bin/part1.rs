use regex::Regex;

fn main() {
    let input = include_str!("./input.txt");
    let output = count_sum(input);
    dbg!(output);
}
 
fn count_sum(input: &str) -> i32 {
    let mut sum = 0;
    let mut numbers_list: Vec<(u32, Vec<(usize, usize, usize)>)> = Vec::new();
    let re = Regex::new(r"(\d+)").expect("Invalid regex pattern");

    for (i, line) in input.lines().enumerate() {
        for caps in re.captures_iter(line) {
            if let Some(number_str) = caps.get(1) {
                if let Ok(number) = number_str.as_str().parse::<u32>() {
                    let start_index = number_str.start();
                    let end_index = number_str.end();

                    numbers_list.push((number, vec![(i, start_index, end_index)]));
                }
            }
        }
    }

    for (number, indexes) in &numbers_list {
        if is_adjacent(&input, &indexes) {
            sum += *number as i32;
        }
    }

    sum
}

fn is_adjacent(input: &str, indexes: &Vec<(usize, usize, usize)>) -> bool {
    for &(i, start, end) in indexes {
        let possible_indexes: Vec<usize>;
        if start > 0 {
            possible_indexes = (start - 1..=end).collect();
        } else {
            possible_indexes = (start..=end).collect();
        }

        if let Some(line) = input.lines().nth(i) {
            let line_str = line.to_string();
            if (start > 0 && line_str.chars().nth(start - 1).filter(|&c| c != '.').is_some()) ||
               (end <= line_str.len() && line_str.chars().nth(end).filter(|&c| c != '.').is_some()) {
                return true;
            }
        }

        if i != 0 {
            for &line_offset in &[i - 1, i + 1] {
                if let Some(line) = input.lines().nth(line_offset) {
                    let line_str = line.to_string();

                    if possible_indexes.iter().any(|&c| {
                        line_str.chars().nth(c).filter(|&ch| ch != '.' && !ch.is_digit(10)).is_some()
                    }) {
                        return true;
                    }
                }
            }
        } else {
            if let Some(line) = input.lines().nth(i + 1) {
                let line_str = line.to_string();

                if possible_indexes.iter().any(|&c| {
                    line_str.chars().nth(c).filter(|&ch| ch != '.' && !ch.is_digit(10)).is_some()
                }) {
                    return true;
                }
            }
        }
    }

    false
}

