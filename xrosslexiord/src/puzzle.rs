#![allow(dead_code)]

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub enum Orientation {
    Across,
    Down
}

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
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

#[derive(Clone, Debug)]
pub struct Puzzle {
    rows: usize,
    cols: usize,
    backing: Vec<char>
}


impl Puzzle {
    pub fn read(&self, row_index: usize, col_index: usize) -> char {
        self.backing[self.cols*row_index + col_index]
    }

    pub fn write(&mut self, row_index: usize, col_index: usize,
             letter: char) {
        self.backing[self.cols*row_index + col_index] = letter;
    }

    // XXX: returning a Vec<_> implies an allocation, but we can't return a
    // slice because the Down case isn't contiguous. Can we return a
    // Cow<[char]>? Profile and decide later
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

    pub fn write_wordspan(&mut self, address: WordspanAddress, word: Vec<char>) {
        let start_square = self.cols*address.start_row + address.start_col;
        let mut offset = 0;
        for character in word {
            self.backing[start_square + offset] = character;
            offset += match address.orientation {
                Orientation::Across => 1,
                Orientation::Down => self.cols
            }
        }
    }

    fn wordspan_addresses_across(&self) -> Vec<WordspanAddress> {
        let mut addresses = Vec::new();
        for row in 0..self.rows {
            let mut open_word_from: Option<usize> = None;
            for col in 0..self.cols {
                match open_word_from {
                    None => {
                        if self.read(row, col) == BARRIER {
                            continue;
                        } else {
                            open_word_from = Some(col);
                        }
                    },
                    Some(open_from) => {
                        if self.read(row, col) == BARRIER {
                            let address = WordspanAddress::new(
                                row, open_from, Orientation::Across,
                                col-open_from
                            );
                            addresses.push(address);
                            open_word_from = None;
                        } else {
                            continue;
                        }
                    }
                }
            }
            // close off word closed by edge of puzzle rather than barrier
            if let Some(open_from) = open_word_from {
                let address = WordspanAddress::new(
                    row, open_from, Orientation::Across,
                    self.cols-open_from
                );
                addresses.push(address);
            }
        }
        addresses
    }

}


#[cfg(test)]
mod tests {
    use super::*;

    fn test_puzzle_i() -> Puzzle {
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

    fn test_puzzle_ii() -> Puzzle {
        Puzzle {
            rows: 7,
            cols: 11,
            backing: vec![
                'S', 'W', 'I', 'F', 'T', '█', 'S', 'T', 'A', 'C', 'K',
                'L', 'A', 'M', 'A', 'S', '█', 'L', 'A', 'M', 'A', 'R',
                'A', 'L', 'A', 'N', '█', 'L', 'E', 'G', 'A', 'T', 'O',
                'B', 'E', 'G', '█', 'L', 'A', 'D', '█', 'D', 'E', 'C',
                '█', 'S', 'E', 'L', 'I', 'M', '█', 'F', 'E', 'R', '█',
                '█', 'A', 'R', 'E', 'S', '█', 'P', 'I', 'U', 'S', '█',
                '█', '█', 'Y', 'I', 'P', '█', 'L', 'E', 'S', '█', '█',
            ]
        }
    }

    #[test]
    fn concerning_read_wordspan_across() {
        let write_address = WordspanAddress::new(0, 0, Orientation::Across, 5);
        assert_eq!(test_puzzle_i().read_wordspan(write_address),
                   vec!['W', 'R', 'I', 'T', 'E']);
    }

    #[test]
    fn concerning_read_wordspan_down() {
        let raise_address = WordspanAddress::new(0, 1, Orientation::Down, 5);
        assert_eq!(test_puzzle_i().read_wordspan(raise_address),
                   vec!['R', 'A', 'I', 'S', 'E']);
    }

    #[test]
    fn concerning_write_wordspan_across() {
        let mut puzzle = test_puzzle_i();
        // in more ways than one
        let write_address = WordspanAddress::new(0, 0, Orientation::Across, 5);
        puzzle.write_wordspan(write_address, vec!['P', 'L', 'A', 'T', 'E']);
        assert_eq!(puzzle.read_wordspan(write_address),
                   vec!['P', 'L', 'A', 'T', 'E']);
    }

    #[test]
    fn concerning_write_wordspan_down() {
        let mut puzzle = test_puzzle_i();
        let entry_address = WordspanAddress::new(0, 4, Orientation::Down, 5);
        puzzle.write_wordspan(entry_address, vec!['F', 'A', 'I', 'T', 'H']);
        assert_eq!(puzzle.read_wordspan(entry_address),
                   vec!['F', 'A', 'I', 'T', 'H']);
    }

    #[test]
    fn concerning_wordspan_addresses_across() {
        let puzzle = test_puzzle_ii();
        assert_eq!(
            &[WordspanAddress::new(0, 0, Orientation::Across, 5),
              WordspanAddress::new(0, 6, Orientation::Across, 5),
              WordspanAddress::new(1, 0, Orientation::Across, 5),
              WordspanAddress::new(1, 6, Orientation::Across, 5),
              WordspanAddress::new(2, 0, Orientation::Across, 4),
              WordspanAddress::new(2, 5, Orientation::Across, 6)],
            &puzzle.wordspan_addresses_across()[..6]
        );
    }
}
