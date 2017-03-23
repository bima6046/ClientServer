package hw1_v2;

/**
 * Class for managing the related code of dash string
 * @author Bisrat
 *
 */
class DASH_MANAGEMENT {
	
	private int max_failed_attempts;
	
	/**
	 * Set maximum failed attempts depending on word length
	 * @param word_len
	 */
	void set_attempts(int word_len){
		max_failed_attempts = (2*word_len > 15) ? 15 : 2*word_len;
	}
	
	/**
	 * return max failed attempts
	 * @return
	 */
	int get_attempts(){
		return max_failed_attempts;
	}
	
	private int decrease_attempts(){
		return --max_failed_attempts;
	}
	
	
	/**
	 * Create a string of underlines with length len
	 * @param len length of string
	 * @return string with dashes
	 */
	StringBuilder createDashes(int len){
		StringBuilder dashed_word = new StringBuilder();
		for(int i = 0; i < len; i++) dashed_word.append("_ ");
		return dashed_word;
	}
	
	/**
	 * Count the dashes in the current string represented as a char array
	 * @param dash_array string in char array format
	 * @return total dashes
	 */
	int countDashes(char[] dash_array){
		int totalDashes = 0;
		for(char c: dash_array)
			if(c == '_') totalDashes++;
		return totalDashes;
	}
	
	/**
	 * Checks whether the guess was a letter or a word and make the corresponding function call
	 * @param guess					the guess string
	 * @param current_dash_word		the dash string as a stringbuilder variable
	 * @param selected_word			the intended word to guess
	 * @return
	 */
	StringBuilder checkGuess(String guess, StringBuilder current_dash_word, String selected_word){
		if(guess.length() == 1){
			return checkLetter(guess, current_dash_word, selected_word);
		}
		else{
			return checkWord(guess, current_dash_word, selected_word);
		}
	}
	
	private StringBuilder checkLetter(String letter, StringBuilder current_dash_word, String selected_word){
		int i = 0;
		boolean noLetter = true;
		while((i = selected_word.indexOf(letter, i)) != -1){
			noLetter = false;
			current_dash_word.setCharAt(i*2, letter.charAt(0));
			i++;
		}
		if(noLetter && decrease_attempts() == 0) 
			current_dash_word = stringToBuilderWithSpaces(selected_word);
		return current_dash_word;
	}
	
	private StringBuilder checkWord(String word, StringBuilder current_dash_word, String selected_word){
		if(word.equals(selected_word)) return stringToBuilderWithSpaces(word);
		else{
			if(decrease_attempts() == 0)
				current_dash_word = stringToBuilderWithSpaces(selected_word);
			return current_dash_word;
		}
	}
	
	private StringBuilder stringToBuilderWithSpaces(String word){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < word.length(); i++){
			sb.append(word.charAt(i));
			sb.append(' ');
		}
		return sb;
	}
	
}
