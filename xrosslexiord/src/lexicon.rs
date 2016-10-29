use std::fs::File;
use std::io;
use std::io::prelude::*;
use std::io::BufReader;


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

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn concerning_loading_our_dictionary() {
        let dictionary = load_dictionary().expect("couldn't load dictionary");
        // Probably some systems will have a different /usr/share/dict/words.
        // Works on my machine!
        assert_eq!(vec!["A", "AOL", "Aachen", "Aaliyah", "Aaron", "Abbas",
                        "Abbasid", "Abbott", "Abby", "Abdul"],
                   dictionary[..10].to_vec());
    }

}
