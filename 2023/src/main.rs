mod day01;
mod utils;

use std::env;

fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() < 2 {
        eprintln!("Usage: {} <day>", args[0]);
        return;
    }

    let day = &args[1];

    match day.as_str() {
        "01" => day01::run(),
        _ => eprintln!("Unknown day: {}", day),
    }
}
