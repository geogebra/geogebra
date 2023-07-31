package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.model.impl.factory.DefaultKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.MowSpecialSymbolsKeyboardFactory;

public final class NotesKeyboardFactory extends DefaultKeyboardFactory {

	/**
	 * Keyboard layout for MOW
	 */
	public NotesKeyboardFactory() {
		super();
		specialSymbolsKeyboardFactory = new MowSpecialSymbolsKeyboardFactory();
	}
}
