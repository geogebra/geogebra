package org.geogebra.keyboard.web;

import org.geogebra.keyboard.base.KeyboardFactory;

public class KeyboardMow extends KeyboardFactory {

	/**
	 * Keyboard layout for MOW
	 */
	public KeyboardMow() {
		super();
		setSpecialSymbolsKeyboardFactory(new MowSpecialSymbolsKeyboardFactory());
	}
}
