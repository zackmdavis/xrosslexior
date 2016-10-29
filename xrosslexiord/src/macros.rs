macro_rules! charvec {
    ($word:ident) => {{
        let word = stringify!($word);
        word.chars().collect::<Vec<_>>()
    }}
}


#[cfg(test)]
mod tests {

    #[test]
    fn concerning_charvec_macro() {
        assert_eq!(vec!['H', 'A', 'S', 'T', 'E'],
                   charvec![HASTE]);
    }

}
