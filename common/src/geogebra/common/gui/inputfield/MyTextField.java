package geogebra.common.gui.inputfield;

/**
 * @author gabor
 * 
 * Just a collector class for static methods 
 * used both Web and Desktop
 *
 */
public abstract class MyTextField {

	/**
	 * Locates bracket positions in a given string with given caret position.
	 */
	public static int[] getBracketPositions(String text, int caret) {
	
		// position to the left of the caret if a bracket exists
		int bracketPos0 = -1;
		// position of matching bracket if it exists
		int bracketPos1 = -1;
	
		int searchDirection = 0;
		int searchEnd = 0;
	
		char bracketToMatch = ' ';
		char oppositeBracketToMatch = ' ';
	
		if (caret > 0 && caret <= text.length()) {
	
			// get the character just to the left of the caret
			char c = text.charAt(caret - 1);
			bracketPos0 = caret - 1;
	
			// check if we have a bracket next to the caret
			// and set the search parameters if we do
			switch (c) {
			case '(':
				searchDirection = +1;
				searchEnd = text.length();
				oppositeBracketToMatch = '(';
				bracketToMatch = ')';
				break;
			case '{':
				searchDirection = +1;
				searchEnd = text.length();
				oppositeBracketToMatch = '{';
				bracketToMatch = '}';
				break;
			case '[':
				searchDirection = +1;
				searchEnd = text.length();
				oppositeBracketToMatch = '[';
				bracketToMatch = ']';
				break;
			case ')':
				searchDirection = -1;
				searchEnd = -1;
				oppositeBracketToMatch = ')';
				bracketToMatch = '(';
				break;
			case '}':
				searchDirection = -1;
				searchEnd = -1;
				oppositeBracketToMatch = '}';
				bracketToMatch = '{';
				break;
			case ']':
				searchDirection = -1;
				searchEnd = -1;
				oppositeBracketToMatch = ']';
				bracketToMatch = '[';
				break;
			default:
				searchDirection = 0;
				bracketPos0 = -1;
				bracketPos1 = -1;
				break;
			}
	
		}
	
		// search the text for a matching bracket
	
		boolean textMode = false; // flag for quoted text
		if (searchDirection != 0) {
			int count = 0;
			for (int i = caret - 1; i != searchEnd; i += searchDirection) {
				if (text.charAt(i) == '\"') {
					textMode = !textMode;
				}
				if (!textMode && text.charAt(i) == bracketToMatch) {
					count++;
				} else if (!textMode
						&& text.charAt(i) == oppositeBracketToMatch) {
					count--;
				}
	
				if (count == 0) {
					bracketPos1 = i;
					break;
				}
			}
		}
	
		int[] result = { bracketPos0, bracketPos1 };
	
		return result;
	
	}

	public static boolean isCloseBracketOrWhitespace(char c) {
		//Character.isWhiteSpace not supported in GWT
	    return isWhiteSpace(c) || c == ')' || c == ']' || c == '}';
	  }
	
	private static boolean isWhiteSpace(char c) {
		return /*It is a Unicode space character (SPACE_SEPARATOR, LINE_SEPARATOR, or PARAGRAPH_SEPARATOR) but is not also a non-breaking space (*/
				c == '\u00A0' ||
				c == '\u2007' ||
				c == '\u202F' || /*).*/
				/*It is*/ c == '\u0009' || /*, HORIZONTAL TABULATION. */
				/*It is*/ c == '\n' || /* LINE FEED.*/ //invalid
				/*It is*/ c == '\u000B' || /* VERTICAL TABULATION.*/
				/*It is*/ c == '\u000C' || /* FORM FEED. */
				/*It is*/ c == '\n' || /* CARRIAGE RETURN.*/ //Invalid 
				/*It is*/ c == '\u001C' || /* FILE SEPARATOR.*/
				/*It is*/ c == '\u001D' || /* GROUP SEPARATOR.*/
				/*It is */c == '\u001E'; /* || /* RECORD SEPARATOR.*/
				//invalid /*It is */c == '\u001F' /*UNIT SEPARATOR.*/
			
	}

}
