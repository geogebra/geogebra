package org.geogebra.keyboard.base;

public enum KeyboardType {
	NUMBERS, NUMBERS_DEFAULT, OPERATORS, ABC, GREEK, SPECIAL, LATIN;

	/**
	 * @return index on the switcher
	 */
	public int getIndex() {
		return ordinal();
	}
}
