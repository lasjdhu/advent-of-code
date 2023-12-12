fn main() {
    let input = include_str!("./input.txt");
    let output = count_sum(input);
    dbg!(output);
}
 
fn count_sum(input: &str) -> i32 {
    let mut sum = 0;

    for line in input.split("\n") {
        let mut digits: Vec<i32> = Vec::new();

        for c in line.chars() {
            if c.is_numeric() {
                digits.push(c.to_digit(10).unwrap() as i32);
            }
        }

        if let Some(first_part) = digits.first() {
            if let Some(second_part) = digits.last() {
                sum += format!("{}{}", first_part, second_part)
                    .parse::<i32>()
                    .unwrap();
            }
        }
    }

    sum
}
