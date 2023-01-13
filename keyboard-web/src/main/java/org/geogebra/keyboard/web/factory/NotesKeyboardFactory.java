package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.web.factory.model.MowSpecialSymbolsKeyboardFactory;

public final class NotesKeyboardFactory extends KeyboardFactory {

	public static final NotesKeyboardFactory INSTANCE = new NotesKeyboardFactory();

	/**
	 * Keyboard layout for MOW
	 */
	private NotesKeyboardFactory() {
		super();
		setSpecialSymbolsKeyboardFactory(new MowSpecialSymbolsKeyboardFactory());
	}
}
