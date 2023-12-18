use regex::Regex;
use std::collections::HashMap;

fn main() {
    let input = include_str!("./input.txt");
    let output = count_sum(input);
    dbg!(output);
}

fn count_sum(input: &str) -> u32 {
    let (numbers_list, asterisks_list) = extract_numbers_and_asterisks(input);
    let mut adjacency_map = build_adjacency_map(&numbers_list, &asterisks_list);
    let sum = filter_and_multiply(&mut adjacency_map);

    sum
}

fn extract_numbers_and_asterisks(input: &str) -> (Vec<(u32, Vec<(usize, usize, usize)>)>, Vec<(usize, usize)>) {
    let mut numbers_list: Vec<(u32, Vec<(usize, usize, usize)>)> = Vec::new();
    let mut asterisks_list: Vec<(usize, usize)> = Vec::new();
    let re_numbers = Regex::new(r"(\d+)").expect("Invalid regex pattern");
    let re_asterisks = Regex::new(r"\*").expect("Invalid regex pattern");

    for (i, line) in input.lines().enumerate() {
        for caps in re_numbers.captures_iter(line) {
            if let Some(number_str) = caps.get(1) {
                if let Ok(number) = number_str.as_str().parse::<u32>() {
                    let start_index = number_str.start();
                    let end_index = number_str.end();
                    numbers_list.push((number, vec![(i, start_index, end_index)]));
                }
            }
        }
        for caps in re_asterisks.find_iter(line) {
            let index = caps.start();
            asterisks_list.push((i, index));
        }
    }

    (numbers_list, asterisks_list)
}

fn build_adjacency_map(numbers_list: &Vec<(u32, Vec<(usize, usize, usize)>)>, asterisks_list: &Vec<(usize, usize)>) -> HashMap<(usize, usize), Vec<u32>> {
    let mut adjacency_map: HashMap<(usize, usize), Vec<u32>> = HashMap::new();

    for (num, num_indices) in numbers_list {
        for (num_line, start, end) in num_indices {
            let num_start = *start as i32;
            let num_end = *end as i32;
            for (ast_line, index) in asterisks_list {
                let ast_index = *index as i32;

                if num_line == ast_line && (ast_index - num_end == 0 || num_start - ast_index == 1) {
                    let key = (*ast_line, *index);
                    adjacency_map.entry(key).or_default().push(*num);
                }

                if (*num_line == *ast_line + 1 || *num_line == *ast_line - 1) &&
                    (ast_index >= num_start - 1 && ast_index <= num_end)
                {
                    let key = (*ast_line, *index);
                    adjacency_map.entry(key).or_default().push(*num);
                }
            }
        }
    }

    adjacency_map
}

fn filter_and_multiply(adjacency_map: &mut HashMap<(usize, usize), Vec<u32>>) -> u32 {
    adjacency_map.retain(|_, numbers| numbers.len() > 1);

    let mut sum_of_multiplications = 0;
    for (_, numbers) in adjacency_map {
        let multiplication_result: u32 = numbers.iter().product();
        sum_of_multiplications += multiplication_result;
    }

    sum_of_multiplications
}
