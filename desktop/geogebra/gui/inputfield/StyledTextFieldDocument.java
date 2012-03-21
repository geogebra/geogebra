package geogebra.gui.inputfield;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Extends DefaultStyledDocument to allow coloring of brackets and quoted text in a
 * JTextField.
 * 
 * @author G. Sturr
 * 
 */
public class StyledTextFieldDocument extends DefaultStyledDocument {

	private static final long serialVersionUID = 1L;

	@Override
	protected void insertUpdate(AbstractDocument.DefaultDocumentEvent chng,
			AttributeSet attr) {
		super.insertUpdate(chng, attr);
		this.applyCharacterStyling(0, false);
	}

	@Override
	protected void postRemoveUpdate(AbstractDocument.DefaultDocumentEvent chng) {
		super.postRemoveUpdate(chng);
		this.applyCharacterStyling(0, false);
	}

	/**
	 * Sets the supported styles based on the given font.
	 * 
	 * @param font
	 */
	public void setStyles(Font font) {

		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(defaultStyle, font.getFamily());
		StyleConstants.setFontSize(defaultStyle, font.getSize());
		// StyleConstants.setBold(defaultStyle, false);

		Style s = addStyle("regularStyle", defaultStyle);
		StyleConstants.setForeground(s, Color.BLACK);

		s = addStyle("textStyle", defaultStyle);
		StyleConstants.setForeground(s, Color.GRAY);

		s = addStyle("matchedBracketStyle", defaultStyle);
		// StyleConstants.setForeground(s, Color.RED);
		StyleConstants.setBackground(s, Color.CYAN);

		s = addStyle("unMatchedBracketStyle", defaultStyle);
		// StyleConstants.setForeground(s, Color.GREEN);
		StyleConstants.setBackground(s, Color.RED);
		setLogicalStyle(0, getStyle("regularStyle"));

	}

	/**
	 * Applies styles to the current text string. NOTE: this is not an efficient
	 * method. No attempt is made to apply styles to substrings. Instead, each
	 * character is simply given its own style attribute.
	 * 
	 * @param caret
	 *            position of caret
	 * @param applyBracketColor
	 *            if true then a bracket to the left of the caret and its
	 *            matching bracket (if they exist) will be colored
	 */
	public void applyCharacterStyling(int caret, boolean applyBracketColor) {

		if (this.getLength() == 0)
			return;

		String text = null;
		try {
			text = getText(0, getLength());

			int[] bracketPos = getBracketPositions(text, caret);
			boolean textMode = false;
			Style charStyle;

			for (int i = 0; i < text.length(); i++) {

				if (text.charAt(i) == '\"')
					textMode = !textMode;

				if (textMode || text.charAt(i) == '\"') {
					charStyle = getStyle("textStyle");

				} else if (applyBracketColor && i == bracketPos[0]
						|| i == bracketPos[1]) {
					if (bracketPos[1] >= 0)
						charStyle = getStyle("matchedBracketStyle");
					else
						charStyle = getStyle("unMatchedBracketStyle");
				} else {
					charStyle = getStyle("regularStyle");
				}

				setCharacterAttributes(i, 1, charStyle, true);
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Locates bracket positions in a given string with given caret position.
	 */
	private static int[] getBracketPositions(String text, int caret) {

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

}
