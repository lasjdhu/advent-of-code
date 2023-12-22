use std::collections::HashSet;

fn main() {
    let input = include_str!("./input.txt");
    let output = get_total(input);
    dbg!(output);
}

fn get_total(input: &str) -> i32 {
    let mut cards = vec![1; input.lines().count()];

    for (i, line) in input.lines().enumerate() {
        let (winning, matches) = split_left_right(remove_prefix(line, get_prefix_end_index(line)));
        let winning_set: HashSet<_> = winning.split_whitespace().map(|num_str| num_str.parse::<i32>().unwrap()).collect();
        let matches_set: HashSet<_> = matches.split_whitespace().map(|num_str| num_str.parse::<i32>().unwrap()).collect();
        let matched_count = winning_set.intersection(&matches_set).count();

        for j in 1..=matched_count {
            cards[i + j] += cards[i];
        }
    }

    cards.iter().sum()
}

fn get_prefix_end_index(line: &str) -> usize {
    let end = line.find(":").unwrap();
    end + 1
}

fn remove_prefix(line: &str, to_index: usize) -> &str {
    &line[to_index..].trim()
}

fn split_left_right(line: &str) -> (&str, &str) {
    let mut parts = line.split('|');
    let left = parts.next().unwrap().trim();
    let right = parts.next().unwrap().trim();
    (left, right)
}
