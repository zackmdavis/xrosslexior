#![allow(dead_code)]

use std::ascii::AsciiExt;
use std::fs::File;
use std::io;
use std::io::prelude::*;
use std::io::BufReader;

use radix_trie::Trie;

pub fn load_dictionary() -> Result<Vec<String>, io::Error> {
    let wordfile = try!(File::open("/usr/share/dict/words"));
    let wordreader = BufReader::new(wordfile);
    let mut wordlist = Vec::with_capacity(99171);
    for line in wordreader.lines() {
        let word = try!(line).trim().to_owned();
        if word.contains("'") {
            continue;
        }
        wordlist.push(word);
    }
    Ok(wordlist)
}

lazy_static! {
    static ref DICTIONARY: Vec<String> = load_dictionary()
        .expect("couldn't load dictionary");
}


pub struct Lexicon {
    trees: Vec<Trie<String, ()>>
}

impl Lexicon {
    pub fn build(n: usize) -> Self {
        let mut lexicon = Lexicon { trees: Vec::new() };
        for i in 0..n+1 {
            lexicon.trees.push(compile_n_prefix_tree(i));
        }
        lexicon
    }

    pub fn contains(&self, word: &[char]) -> bool {
        let key: String = word.iter().cloned().collect();
        self.trees[key.len()].get(&key).is_some()
    }

    pub fn n_dictionary(&self, n: usize) -> Vec<String> {
        return self.trees[n].keys().cloned().collect()
    }
}

pub fn compile_n_prefix_tree(n: usize) -> Trie<String, ()> {
    let mut tree = Trie::new();
    for word in DICTIONARY.iter() {
        if word.len() != n {
            continue;
        }
        let normalized = word.to_ascii_uppercase();
        tree.insert(normalized, ());
    }
    tree
}


#[cfg(test)]
mod tests {
    use super::*;
    use super::DICTIONARY;

    #[test]
    fn concerning_loading_our_dictionary() {
        // Probably some systems will have a different /usr/share/dict/words.
        // Works on my machine!
        assert_eq!(vec!["A", "AOL", "Aachen", "Aaliyah", "Aaron", "Abbas",
                        "Abbasid", "Abbott", "Abby", "Abdul"],
                   DICTIONARY[..10].to_vec());
    }

    #[test]
    fn concerning_compiling_prefix_trees() {
        let tree = compile_n_prefix_tree(6);
        assert_eq!(vec!["AACHEN", "ABACUS", "ABASED", "ABASES", "ABATED"],
                   tree.keys().take(5).collect::<Vec<_>>());
    }

    #[test]
    fn concerning_lexicon_lookup() {
        let lexicon = Lexicon::build(3);
        assert!(lexicon.contains(&['C', 'A', 'T']));
    }

    #[test]
    fn concerning_n_dictionaries() {
        let lexicon = Lexicon::build(3);
        assert_eq!(vec!["ABE", "ACE", "ACT", "ADA", "ADD"],
                   lexicon.n_dictionary(3).iter().take(5).collect::<Vec<_>>());
    }

}
