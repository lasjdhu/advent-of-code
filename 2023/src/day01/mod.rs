mod solver;

use crate::day01::solver::{solve_first, solve_second};
use crate::utils;

pub fn run() {
    let filename = "src/day01/input.txt";
    let input = utils::read_lines(filename).expect("Failed to read input");

    println!("{}", solve_first(&input));
    println!("{}", solve_second(&input));
}
