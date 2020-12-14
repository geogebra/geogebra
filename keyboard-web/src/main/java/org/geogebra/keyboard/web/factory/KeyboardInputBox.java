package org.geogebra.keyboard.web.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultSymbolsKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.IneqBoolMathKeyboardFactory;

public class KeyboardInputBox extends KeyboardFactory {

	public KeyboardInputBox() {
		super();
		setDefaultKeyboardFactory(new InputBoxDefaultMathKeyboardFactory());
		setMathKeyboardFactory(new InputBoxDefaultMathKeyboardFactory());
		setFunctionKeyboardFactory(new InputBoxDefaultFunctionKeyboardFactory());
		setSpecialSymbolsKeyboardFactory(new InputBoxDefaultSymbolsKeyboardFactory());
	}
}
