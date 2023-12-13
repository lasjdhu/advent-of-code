use std::collections::BTreeMap;

fn main() {
    let input = include_str!("./input.txt");
    let output = count_sum(input);
    dbg!(output);
}

fn get_number_from_string(s: &str) -> i32 {
    match s {
        "one" => 1,
        "two" => 2,
        "three" => 3,
        "four" => 4,
        "five" => 5,
        "six" => 6,
        "seven" => 7,
        "eight" => 8,
        "nine" => 9,
        &_ => 0,
    }
}
 
fn count_sum(input: &str) -> i32 {
    let mut sum = 0;

    for line in input.split("\n") {
        let strings: [&str; 9] =
            ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"];
        let mut digit_map: BTreeMap<i32, i32> = BTreeMap::new();

        for s in strings {
            let mut start_index = 0;

            while let Some(index) = line[start_index..].find(s) {
                let absolute_index = start_index + index;
                digit_map.insert(absolute_index as i32 + s.len() as i32 - 1, get_number_from_string(s));

                start_index = absolute_index + s.len();
            }
        }

        for (index, c) in line.char_indices() {
            if c.is_numeric() {
                digit_map.insert(index as i32, c.to_digit(10).unwrap() as i32);
            }
        }

        println!("{:?}", digit_map);

        if let Some(first_entry) = digit_map.iter().next() {
            if let Some(last_entry) = digit_map.iter().next_back() {
                let first_part = first_entry.1;
                let second_part = last_entry.1;
                println!("{}{}", first_part, second_part);
                sum += format!("{}{}", first_part, second_part)
                    .parse::<i32>()
                    .unwrap();
            }
        }
    }

    sum
}
