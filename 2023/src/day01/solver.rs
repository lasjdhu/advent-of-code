use std::collections::HashMap;

fn word_to_digit(word: &str) -> Option<u32> {
    let map: HashMap<&str, u32> = [
        ("one", 1),
        ("two", 2),
        ("three", 3),
        ("four", 4),
        ("five", 5),
        ("six", 6),
        ("seven", 7),
        ("eight", 8),
        ("nine", 9),
    ]
    .into_iter()
    .collect();

    map.get(word).copied()
}

pub fn first_and_last_digits(line: &str) -> Option<u32> {
    let first = line.chars().find(|c| c.is_ascii_digit())?;
    let last = line.chars().rev().find(|c| c.is_ascii_digit())?;

    let concat = format!("{}{}", first, last);
    concat.parse().ok()
}

fn find_first_digit(line: &str) -> Option<u32> {
    let chars: Vec<char> = line.chars().collect();
    let mut i = 0;

    while i < chars.len() {
        if chars[i].is_ascii_digit() {
            return chars[i].to_digit(10);
        }

        for len in 3..=5 {
            if i + len <= chars.len() {
                let slice: String = chars[i..i + len].iter().collect();
                if let Some(d) = word_to_digit(&slice) {
                    return Some(d);
                }
            }
        }

        i += 1;
    }

    None
}

fn find_last_digit(line: &str) -> Option<u32> {
    let chars: Vec<char> = line.chars().collect();

    for i in (0..chars.len()).rev() {
        if chars[i].is_ascii_digit() {
            return chars[i].to_digit(10);
        }

        for len in 3..=5 {
            if i + 1 >= len {
                let start = i + 1 - len;
                let slice: String = chars[start..=i].iter().collect();
                if let Some(d) = word_to_digit(&slice) {
                    return Some(d);
                }
            }
        }
    }

    None
}

pub fn solve_first(input: &[String]) -> u32 {
    input
        .iter()
        .filter_map(|line| first_and_last_digits(line))
        .sum()
}

pub fn solve_second(input: &[String]) -> u32 {
    input
        .iter()
        .filter_map(|line| {
            let first = find_first_digit(line)?;
            let last = find_last_digit(line)?;
            Some(first * 10 + last)
        })
        .sum()
}
