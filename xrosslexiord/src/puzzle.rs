#![allow(dead_code)]

pub enum Orientation {
    Across,
    Down
}

pub struct WordspanAddress {
    start_row: usize,
    start_col: usize,
    orientation: Orientation,
    length: usize
}

impl WordspanAddress {
    pub fn new(start_row: usize, start_col: usize,
               orientation: Orientation, length: usize) -> Self {
        WordspanAddress {
            start_row: start_row,
            start_col: start_col,
            orientation: orientation,
            length: length
        }
    }
}

pub const BARRIER: char = '█';

pub struct Puzzle {
    rows: usize,
    cols: usize,
    backing: Vec<char>
}


impl Puzzle {
    fn read(&self, row_index: usize, col_index: usize) -> char {
        self.backing[self.cols*row_index + col_index]
    }

    fn write(&mut self, row_index: usize, col_index: usize,
             letter: char) {
        self.backing[self.cols*row_index + col_index] = letter;
    }

    pub fn read_wordspan(&self, address: WordspanAddress) -> Vec<char> {
        let start_square = self.cols*address.start_row + address.start_col;
        match address.orientation {
            Orientation::Across => {
                self.backing[start_square..start_square+address.length].to_vec()
            },
            Orientation::Down => {
                (0..address.length).map(|r| {
                    self.backing[start_square + self.cols*r]
                }).collect()
            }
        }
    }

    #[allow(unused_variables)]
    pub fn write_wordspan(&mut self, address: WordspanAddress, word: Vec<char>) {
        // TODO
    }

}


#[cfg(test)]
mod tests {
    use super::*;

    fn test_puzzle() -> Puzzle {
        Puzzle {
            rows: 5,
            cols: 5,
            backing: vec![
                'W', 'R', 'I', 'T', 'E',
                'A', 'A', 'R', 'O', 'N',
                'G', 'I', 'A', 'N', 'T',
                'E', 'S', 'T', 'E', 'R',
                'R', 'E', 'E', 'D', 'Y',
            ]
        }
    }

    #[test]
    fn test_read_wordspan_across() {
        let write_address = WordspanAddress::new(0, 0, Orientation::Across, 5);
        assert_eq!(test_puzzle().read_wordspan(write_address),
                   vec!['W', 'R', 'I', 'T', 'E']);
    }

    #[test]
    fn test_read_wordspan_down() {
        let raise_address = WordspanAddress::new(0, 1, Orientation::Down, 5);
        assert_eq!(test_puzzle().read_wordspan(raise_address),
                   vec!['R', 'A', 'I', 'S', 'E']);
    }

}
