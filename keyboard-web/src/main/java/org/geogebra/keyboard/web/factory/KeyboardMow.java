package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.web.factory.model.MowSpecialSymbolsKeyboardFactory;

public class KeyboardMow extends KeyboardFactory {

	/**
	 * Keyboard layout for MOW
	 */
	public KeyboardMow() {
		super();
		setSpecialSymbolsKeyboardFactory(new MowSpecialSymbolsKeyboardFactory());
	}
}
