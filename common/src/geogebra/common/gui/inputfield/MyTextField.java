package geogebra.common.gui.inputfield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

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
	public static int[] getBracketPositions(String inputText, int caret) {
		String text = inputText;
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
				text = ignoreIndices(text);
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
				text = ignoreIndices(text);
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
	
	/**
	 * Since a_{{{{{{{5}=2 is correct expression, we 
	 * replace the index by Xs to obtain a_{XXXXXXX}=2 
	 * @param text text
	 * @return text with replaced {s
	 */
	private static String ignoreIndices(String text) {
		StringBuilder sb = new StringBuilder(80);
		boolean ignore = false;
		boolean underscore = false;
		for(int i=0;i<text.length();i++){
			if(ignore && text.charAt(i)=='}'){
				ignore = false;
			}
			
			if(!ignore)
				sb.append(text.charAt(i));
			else
				sb.append('X');
			
			if(underscore && text.charAt(i)=='{'){
				ignore = true;
			}
			else if(!ignore){
				underscore = text.charAt(i)=='_';
			}
			
		}
		return sb.toString();
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

	public static ValidateAutocompletionResult commonValidateAutocompletion(int index, List<String> completions, String text, int curWordStart) {
		    
		ValidateAutocompletionResult ret = new ValidateAutocompletionResult();
	    if (completions == null || index < 0 || index >= completions.size()) {
	      ret.returnval =  false;
	    }
	    String command = completions.get(index);
	    StringBuilder sb = new StringBuilder();
	
	    int carPos = 0;
	
	    // Generate Input Tokens
	    char[] tArray = text.toCharArray();
	    ArrayList<String> textTokens = new ArrayList<String>();
	    int textBLPos = -1; // position of left most [
	    int textBRPos = -1; // position of right most ]
	    int textFirstComma = -1; // position of first comma
	    int textBLCount = 0; // count of [
	    int textParams = 0;
	    sb = new StringBuilder();
	    String lastToken = "";
	    for (int i = 0; i < tArray.length; i++) {
	      switch (tArray[i]) {
	        case '[':
	          textTokens.add(sb.toString());
	          sb = new StringBuilder();
	          textTokens.add("[");
	          lastToken = "[";
	          if (textBLPos == -1) {
	            textBLPos = textTokens.size() - 1;
	          }
	          textBLCount++;
	          break;
	        case ']':
	          if (!lastToken.equals("[")
	              || (lastToken.equals("[") && sb.toString().trim().length() != 0)) {
	            textParams++;
	          }
	          if (sb.length() > 0) {
	            textTokens.add(sb.toString());
	            sb = new StringBuilder();
	          }
	          textTokens.add("]");
	          lastToken = "]";
	          if (textBRPos == -1) {
	            textBRPos = textTokens.size() - 1;
	          }
	          break;
	        case ',':
	          textTokens.add(sb.toString());
	          sb = new StringBuilder();
	          textTokens.add(",");
	          lastToken = ",";
	          if (textFirstComma == -1) {
	            textFirstComma = textTokens.size() - 1;
	          }
	          textParams++;
	          break;
	        case '=':
	        	textTokens.add(sb.toString());
	            sb = new StringBuilder();
	            textTokens.add("=");
	            lastToken = "=";
	        	break;
	        default:
	          sb.append(tArray[i]);
	      }
	      if (i == tArray.length - 1 && sb.length() != 0) {
	        textTokens.add(sb.toString());
	        sb = new StringBuilder();
	      }
	    }
	    sb = new StringBuilder();
	
	    // Generate Command Tokens
	    char[] cArray = command.toCharArray();
	    ArrayList<String> commandTokens = new ArrayList<String>();
	    sb = new StringBuilder();
	    int commandBLPos = -1;
	    int commandParams = 0; // parameter count
	    for (int i = 0; i < cArray.length; i++) {
	      switch (cArray[i]) {
	        case '[':
	          commandTokens.add(sb.toString());
	          sb = new StringBuilder();
	          commandTokens.add("[");
	          lastToken = "[";
	          commandBLPos = commandTokens.size() - 1;
	          break;
	        case ']':
	          if (!lastToken.equals("[")
	              || (lastToken.equals("[") && sb.toString().trim().length() != 0)) {
	            commandParams++;
	          }
	          if (sb.length() > 0) {
	            commandTokens.add(sb.toString());
	            sb = new StringBuilder();
	          }
	          commandTokens.add("]");
	          lastToken = "]";
	          break;
	        case ',':
	          commandTokens.add(sb.toString());
	          sb = new StringBuilder();
	          commandParams++;
	          commandTokens.add(",");
	          lastToken = ",";
	          break;
	        default:
	          if (!/*AGCharacter.*/isWhiteSpace(cArray[i])) {
	            sb.append(cArray[i]);
	          }
	      }
	      if (i == cArray.length - 1 && sb.length() != 0) {
	        commandTokens.add(sb.toString());
	        sb = new StringBuilder();
	      }
	    }
	    sb = new StringBuilder();
	
	    // determine start token
	    int startPos = 0;
	    int length = 0;
	    Iterator<String> iterator = textTokens.iterator();
	    while (iterator.hasNext()) {
	      length += iterator.next().length();
	      if (length > curWordStart) {
	        break;
	      }
	      startPos++;
	    }
	    iterator = null;
	
	    // Build new String
	
	    // Append everything before start token
	    String current;
	    for (int i = 0; i < startPos; i++) {
	      current = textTokens.get(i).trim();
	      if (current.equals("[")) {
	        sb.append('[');
	        if (i + 1 < textTokens.size() && !textTokens.get(i + 1).equals("]")) {
	          sb.append(' ');
	        }
	      } else if (current.equals("]")) {
	        if (i - 1 >= 0 && !textTokens.get(i - 1).equals("[")) {
	          sb.append(' ');
	        }
	        sb.append(']');
	      } else if (current.equals(",")) {
	        sb.append(", ");
	      } else {
	        sb.append(current);
	      }
	    }
	    
	    // Append the command
	    sb.append(commandTokens.get(0));
	
	    if (textBLCount == 0) {
	      // original input does not contain any parameters
	      if (commandParams == 0) {
	        sb.append("[]");
	        carPos = sb.length();
	      } else {
	        carPos = sb.length();
	        sb.append("[ ");
	        for (int i = commandBLPos + 1; i < commandTokens.size(); i++) {
	          current = commandTokens.get(i);
	          if (current.equals("[")) {
	            sb.append("[ ");
	          } else if (current.equals("]")) {
	            sb.append(" ]");
	          } else if (current.equals(",")) {
	            sb.append(", ");
	          } else {
	            sb.append(current);
	          }
	        }
	      }
	    } else {
	      // original input does contain parameters
	      if (startPos + 1 < textTokens.size()) {
	        if (textTokens.get(startPos + 1).trim().equals(",")
	            || textTokens.get(startPos + 1).trim().equals("]")) {
	          // new command is a parameter itself
	          // append rest of command
	          if (commandParams == 0) {
	            sb.append("[]");
	          } else {
	            for (int i = 1; i < commandTokens.size(); i++) {
	              current = commandTokens.get(i).trim();
	              if (current.equals("[")) {
	                carPos = sb.length();
	                sb.append("[ ");
	              } else if (current.equals("]")) {
	                sb.append(" ]");
	              } else if (current.equals(",")) {
	                sb.append(", ");
	              } else {
	                sb.append(current);
	              }
	            }
	          }
	          // append the rest of original input
	          for (int i = startPos + 1; i < textTokens.size(); i++) {
	            current = textTokens.get(i).trim();
	            if (current.equals("[")) {
	              sb.append('[');
	              if (i + 1 < textTokens.size()
	                  && !textTokens.get(i + 1).equals("]")) {
	                sb.append(' ');
	              }
	            } else if (current.equals("]")) {
	              if (i - 1 >= 0 && !textTokens.get(i - 1).equals("[")) {
	                sb.append(' ');
	              }
	              sb.append(']');
	            } else if (current.equals(",")) {
	              sb.append(", ");
	            } else {
	              sb.append(current);
	            }
	          }
	        } else if (textTokens.get(startPos + 1).trim().equals("[")) {
	          // parameter comparison is needed
	          int openBrackets = 0;
	          int params = 0;
	          // appends all preexisting params
	          int lastIndex = 0;
	          carPos = sb.length();
	          for (int i = startPos + 1; i < textTokens.size(); i++) {
	            current = textTokens.get(i).trim();
	            if (current.equals("[")) {
	              openBrackets++;
	              sb.append('[');
	              if (i + 1 < textTokens.size()
	                  && !textTokens.get(i + 1).equals("]")) {
	                sb.append(' ');
	              }
	            } else if (current.equals("]")) {
	              if (openBrackets == 1) {
	                params++;
	              } else {
	                if (i - 1 >= 0 && !textTokens.get(i - 1).equals("[")) {
	                  sb.append(' ');
	                }
	                sb.append(']');
	              }
	              openBrackets--;
	              if (openBrackets == 0) {
	                lastIndex = i + 1;
	                break;
	              }
	            } else if (current.equals(",")) {
	              sb.append(", ");
	              if (openBrackets == 1) {
	                params++;
	              }
	            } else {
	              sb.append(current);
	            }
	          }
	          if (params < commandParams) {
	            // append missing
	            sb.append(", ");
	            carPos = sb.length();
	            for (int i = 2 + 2 * params; i < commandTokens.size(); i++) {
	              current = commandTokens.get(i);
	              if (current.equals("[")) {
	                carPos = sb.length();
	                sb.append("[ ");
	              } else if (current.equals("]")) {
	                sb.append(" ]");
	              } else if (current.equals(",")) {
	                sb.append(", ");
	              } else {
	                sb.append(current);
	              }
	            }
	          } else {
	            if (commandParams == 0) {
	              sb.append(']');
	            } else {
	              sb.append(" ]");
	            }
	          }
	          for (int i = lastIndex; i < textTokens.size(); i++) {
	            current = textTokens.get(i).trim();
	            if (current.equals("[")) {
	              carPos = sb.length();
	              sb.append('[');
	              if (i + 1 < textTokens.size()
	                  && !textTokens.get(i + 1).equals("]")) {
	                sb.append(' ');
	              }
	            } else if (current.equals("]")) {
	              if (i - 1 >= 0 && !textTokens.get(i - 1).equals("[")) {
	                sb.append(' ');
	              }
	              sb.append(']');
	            } else if (current.equals(",")) {
	              sb.append(", ");
	            } else {
	              sb.append(current);
	            }
	          }
	        }
	      } else {
	        // should never happen
	        // because [ cannot be the start token and startPos cannot be the last
	        // token if textBLCount > 0
	      }
	    }
	
	    if (textParams + commandParams == 0) {
	      carPos = sb.length();
	    }
	    
	    
	    ret.carPos = carPos;
	    ret.sb = sb.toString();
		return ret;
	}

	private boolean showSymbolTableIcon;

	/**
	 * Sets a flag to show the symbol table icon when the field is focused
	 * 
	 * @param showSymbolTableIcon
	 */
	public void setShowSymbolTableIcon(boolean showSymbolTableIcon) {
		this.showSymbolTableIcon = showSymbolTableIcon;
	}
}
