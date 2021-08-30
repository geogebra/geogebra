package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.web.factory.model.MowSpecialSymbolsKeyboardFactory;

public final class KeyboardMow extends KeyboardFactory {

	public static final KeyboardMow INSTANCE = new KeyboardMow();

	/**
	 * Keyboard layout for MOW
	 */
	private KeyboardMow() {
		super();
		setSpecialSymbolsKeyboardFactory(new MowSpecialSymbolsKeyboardFactory());
	}
}
