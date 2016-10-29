#![allow(dead_code)]

use lexicon::Lexicon;

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

    pub fn oriented_new(start_this: usize, start_cross: usize,
                        orientation: Orientation, length: usize) -> Self {
        match orientation {
            Orientation::Across => WordspanAddress::new(
                start_this, start_cross, orientation, length),
            Orientation::Down => WordspanAddress::new(
                start_cross, start_this, orientation, length)
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

    pub fn oriented_read(&self, orientation: Orientation,
                         this: usize, cross: usize) -> char {
        match orientation {
            Orientation::Across => self.read(this, cross),
            Orientation::Down => self.read(cross, this)
        }
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

    pub fn oriented_wordspan_addresses(&self, orientation: Orientation)
                                       -> Vec<WordspanAddress> {
        let mut addresses = Vec::new();
        let (this_dimension, cross_dimension) = match orientation {
            Orientation::Across => (self.rows, self.cols),
            Orientation::Down => (self.cols, self.rows)
        };
        for beam in 0..this_dimension {
            let mut open_word_from: Option<usize> = None;
            for crossbeam in 0..cross_dimension {
                match open_word_from {
                    None => {
                        if self.oriented_read(orientation,
                                              beam, crossbeam) == BARRIER {
                            continue;
                        } else {
                            open_word_from = Some(crossbeam);
                        }
                    },
                    Some(open_from) => {
                        if self.oriented_read(orientation,
                                              beam, crossbeam) == BARRIER {
                            let address = WordspanAddress::oriented_new(
                                beam, open_from, orientation,
                                crossbeam-open_from
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
                let address = WordspanAddress::oriented_new(
                    beam, open_from, orientation,
                    cross_dimension-open_from
                );
                addresses.push(address);
            }
        }
        addresses
    }

    pub fn wordspan_addresses(&self) -> Vec<WordspanAddress> {
        let mut addresses = self.oriented_wordspan_addresses(Orientation::Across);
        addresses.extend(self.oriented_wordspan_addresses(Orientation::Down));
        addresses
    }

    pub fn is_solved(&self, lexicon: &Lexicon) -> bool {
        for address in self.wordspan_addresses() {
            let word = self.read_wordspan(address);
            if !lexicon.contains(&word) {
                return false;
            }
        }
        true
    }

}


#[cfg(test)]
mod tests {
    use super::*;
    use lexicon::Lexicon;

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
                   charvec![WRITE]);
    }

    #[test]
    fn concerning_read_wordspan_down() {
        let raise_address = WordspanAddress::new(0, 1, Orientation::Down, 5);
        assert_eq!(test_puzzle_i().read_wordspan(raise_address),
                   charvec![RAISE]);
    }

    #[test]
    fn concerning_write_wordspan_across() {
        let mut puzzle = test_puzzle_i();
        // in more ways than one
        let write_address = WordspanAddress::new(0, 0, Orientation::Across, 5);
        puzzle.write_wordspan(write_address, charvec![PLATE]);
        assert_eq!(puzzle.read_wordspan(write_address),
                   charvec![PLATE]);
    }

    #[test]
    fn concerning_write_wordspan_down() {
        let mut puzzle = test_puzzle_i();
        let entry_address = WordspanAddress::new(0, 4, Orientation::Down, 5);
        puzzle.write_wordspan(entry_address, charvec![FAITH]);
        assert_eq!(puzzle.read_wordspan(entry_address),
                   charvec![FAITH]);
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
            &puzzle.oriented_wordspan_addresses(Orientation::Across)[..6]
        );
    }

    #[test]
    fn concerning_wordspan_addresses_down() {
        let puzzle = test_puzzle_ii();
        assert_eq!(
            &[WordspanAddress::new(0, 0, Orientation::Down, 4),
              WordspanAddress::new(0, 1, Orientation::Down, 6),
              WordspanAddress::new(0, 2, Orientation::Down, 7),
              WordspanAddress::new(0, 3, Orientation::Down, 3),
              WordspanAddress::new(4, 3, Orientation::Down, 3),
              WordspanAddress::new(0, 4, Orientation::Down, 2)],
            &puzzle.oriented_wordspan_addresses(Orientation::Down)[..6]
        );
    }

    #[test]
    fn concerning_is_solved() {
        let lexicon = Lexicon::build(7);
        let puzzles = vec![test_puzzle_i(), test_puzzle_ii()];
        for mut puzzle in puzzles {
            assert!(puzzle.is_solved(&lexicon));
            puzzle.write(0, 0, 'Q');
            assert!(!puzzle.is_solved(&lexicon));
        }
    }

}
