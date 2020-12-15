package org.geogebra.keyboard.web.factory;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultSymbolsKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.IneqBoolMathKeyboardFactory;

public class KeyboardInputBox extends KeyboardFactory {

	/**
	 * inputbox keyboard constructor
	 * @param inputBoxType type of geo lined to the inputbox
	 */
	public KeyboardInputBox(InputBoxType inputBoxType) {
		super();
		setDefaultKeyboardFactory(new InputBoxDefaultMathKeyboardFactory());
		setMathKeyboardFactory(new InputBoxDefaultMathKeyboardFactory());
		setFunctionKeyboardFactory(new InputBoxDefaultFunctionKeyboardFactory());
		setSpecialSymbolsKeyboardFactory(new InputBoxDefaultSymbolsKeyboardFactory());
	}
}
