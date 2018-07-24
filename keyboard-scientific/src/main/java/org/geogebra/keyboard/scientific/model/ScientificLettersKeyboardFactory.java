package org.geogebra.keyboard.scientific.model;

import org.geogebra.keyboard.base.model.impl.factory.LetterKeyboardFactory;

import java.util.Arrays;

public class ScientificLettersKeyboardFactory extends LetterKeyboardFactory {

	private static final String DEFAULT_CONTROL_ROW = "=,'";

	@Override
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow, String
			controlRow, Integer bottomActionLeft, Integer controlActionLeft) {
		String allButtons = topRow + middleRow + bottomRow;
		char[] characters = allButtons.toCharArray();
		Arrays.sort(characters);
		int length = characters.length;
		int[] lengths = { 0, 0 };
		int rowLength = (int) Math.ceil(length / 3.0f);
		if (length % 3 == 2) {
			lengths[0] = lengths[1] = rowLength;
		} else if (length % 3 == 1) {
			lengths[0] = rowLength - 1;
			lengths[1] = rowLength;
		} else {
			lengths[0] = lengths[1] = rowLength - 1;
		}
		String newTopRow = subrangeToString(characters, 0, lengths[0]);
		String newMiddleRow = subrangeToString(characters, lengths[0], lengths[1]);
		String newBottomRow = subrangeToString(characters, lengths[1], length);

		super.setKeyboardDefinition(newTopRow, newMiddleRow, newBottomRow, DEFAULT_CONTROL_ROW,
				null, ACTION_SHIFT);
	}

	private String subrangeToString(char[] chars, int from, int to) {
		return String.valueOf(Arrays.copyOfRange(chars, from, to));
	}
}
