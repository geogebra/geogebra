package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.model.impl.factory.CommonKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.MowSpecialSymbolsKeyboardFactory;

public final class NotesKeyboardFactory extends CommonKeyboardFactory {

	public static final NotesKeyboardFactory INSTANCE = new NotesKeyboardFactory();

	/**
	 * Keyboard layout for MOW
	 */
	private NotesKeyboardFactory() {
		super();
		specialSymbolsKeyboardFactory = new MowSpecialSymbolsKeyboardFactory();
	}
}
