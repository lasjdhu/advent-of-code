mod part1;
use part1::{get_prefix_indices, get_id, remove_prefix, get_sets};

fn main() {
    let input = include_str!("./input.txt");
    let output = get_powers_sum(input);
    dbg!(output);
}

fn get_powers_sum(input: &str) -> i32 {
    let mut sum = 0;

    for line in input.split("\n").filter(|&s| !s.is_empty()) {
        let prefix = get_prefix_indices(line);
        let id = get_id(line, prefix[0], prefix[1]);

        // prefix[1] + 2 because we also want to skip ": " part
        let line_wo_prefix = remove_prefix(line, prefix[1] + 2);
        let sets = get_sets(&line_wo_prefix);

        let power = get_fewer_values(&sets);

        sum += power;
    }

    sum
}

fn get_fewer_values(sets: &Vec<[i32; 3]>) -> i32 {
    let mut values: [i32; 3] = [0, 0, 0];

    for set in sets {
        if set[0] > values[0] {
            values[0] = set[0];
        }
        if set[1] > values[1] {
            values[1] = set[1];
        }
        if set[2] > values[2] {
            values[2] = set[2];
        }
    }

    values[0] * values[1] * values[2]
}

