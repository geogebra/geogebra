package org.geogebra.keyboard.scientific.factory;

import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificDefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificFunctionKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificLettersKeyboardFactory;

public final class ScientificKeyboardFactory extends DefaultKeyboardFactory {

	/**
	 * Creates a ScientificKeyboardFactory with default implementations
	 * for keyboard model factories.
	 */
	public ScientificKeyboardFactory() {
		mathKeyboardFactory = new ScientificDefaultKeyboardFactory(true);
		defaultKeyboardModelFactory = new ScientificDefaultKeyboardFactory(false);
		functionKeyboardFactory = new ScientificFunctionKeyboardFactory();
		letterKeyboardFactory = new ScientificLettersKeyboardFactory();
	}
}
