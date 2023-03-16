package org.geogebra.keyboard.scientific.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificDefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificFunctionKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificLettersKeyboardFactory;

public final class ScientificKeyboardFactory extends KeyboardFactory {

	public static final KeyboardFactory INSTANCE = new ScientificKeyboardFactory();

	private ScientificKeyboardFactory() {
		super();
		setDefaultKeyboardFactory(new ScientificDefaultKeyboardFactory(false));
		setMathKeyboardFactory(new ScientificDefaultKeyboardFactory(true));
		setFunctionKeyboardFactory(new ScientificFunctionKeyboardFactory());
		setLetterKeyboardFactory(new ScientificLettersKeyboardFactory());
	}
}
