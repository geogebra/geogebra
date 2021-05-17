package org.geogebra.keyboard.web.factory;

import java.util.List;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.IneqBoolFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard.InputBoxDefaultFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard.InputBoxDefaultGreekKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard.InputBoxDefaultLettersKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard.InputBoxDefaultMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard.InputBoxDefaultSymbolsKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.FunctionMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.IneqBoolMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.VectorMatrixMathKeyboardFactory;

public class KeyboardInputBox extends KeyboardFactory {

	/**
	 * inputbox keyboard constructor
	 * @param inputBoxType type of geo lined to the inputbox
	 * @param functionVars function vars in case of a function
	 */
	public KeyboardInputBox(InputBoxType inputBoxType, List<String> functionVars) {
		super();
		setDefaultKeyboardFactory(getMathKeyboard(inputBoxType, functionVars));
		setMathKeyboardFactory(getMathKeyboard(inputBoxType, functionVars));
		setFunctionKeyboardFactory(getFunctionKeyboard(inputBoxType));
		setSpecialSymbolsKeyboardFactory(new InputBoxDefaultSymbolsKeyboardFactory());
		setLetterKeyboardFactory(new InputBoxDefaultLettersKeyboardFactory());
		setGreekKeyboardFactory(new InputBoxDefaultGreekKeyboardFactory());
	}

	private KeyboardModelFactory getMathKeyboard(InputBoxType inputBoxType,
			List<String> functionVars) {
		switch (inputBoxType) {
		case VECTOR_MATRIX:
			return new VectorMatrixMathKeyboardFactory();
		case INEQ_BOOL:
			return new IneqBoolMathKeyboardFactory();
		case FUNCTION:
			return new FunctionMathKeyboardFactory(functionVars);
		case DEFAULT:
		default:
			return new InputBoxDefaultMathKeyboardFactory();
		}
	}

	private KeyboardModelFactory getFunctionKeyboard(InputBoxType inputBoxType) {
		switch (inputBoxType) {
		case DEFAULT:
		case VECTOR_MATRIX:
		case FUNCTION:
		default:
			return new InputBoxDefaultFunctionKeyboardFactory();
		case INEQ_BOOL:
			return new IneqBoolFunctionKeyboardFactory();
		}
	}
}
