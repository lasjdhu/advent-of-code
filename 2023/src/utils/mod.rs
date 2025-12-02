use std::fs;
use std::io::{self, BufRead};

pub fn read_lines(filename: &str) -> io::Result<Vec<String>> {
    let file = fs::File::open(filename)?;
    let reader = io::BufReader::new(file);
    Ok(reader
        .lines()
        .filter_map(Result::ok)
        .filter(|line| !line.is_empty())
        .collect())
}

// pub fn read_line(filename: &str) -> io::Result<String> {
//     fs::read_to_string(filename)
// }
