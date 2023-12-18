fn main() {
    let input = include_str!("./input.txt");
    let output = get_points(input);
    dbg!(output);
}

fn get_points(input: &str) -> i32 {
    let mut points = 0;

    for line in input.lines() {
        let mut points_in_pile = 0;

        let prefix = get_prefix_indices(line);
        let line_wo_prefix = remove_prefix(line, prefix[1] + 2);

        let (left, right) = split_left_right(&line_wo_prefix);

        let winning: Vec<i32> = get_cards(&left);
        let matches: Vec<i32> = get_cards(&right);

        for card in matches {
            if winning.contains(&card) {
                if points_in_pile == 0 {
                    points_in_pile += 1;
                } else {
                    points_in_pile *= 2;
                }
            }
        }
        points += points_in_pile;
    }

    points
}

fn get_prefix_indices(line: &str) -> [usize; 2] {
    let start = 5; // index of the next char after "Card "
    let end = line.find(":").unwrap();

    [start, end]
}

fn remove_prefix(line: &str, to_index: usize) -> String {
    let line_wo_prefix: String = line.chars().skip(to_index).collect();
    line_wo_prefix
}

fn split_left_right(line: &str) -> (&str, &str) {
    let mut parts = line.split('|');
    let left = parts.next().unwrap().trim();
    let right = parts.next().unwrap().trim();
    (left, right)
}

fn get_cards(line: &str) -> Vec<i32> {
    line.split_whitespace()
        .map(|num_str| num_str.parse::<i32>().unwrap())
        .collect()
}
