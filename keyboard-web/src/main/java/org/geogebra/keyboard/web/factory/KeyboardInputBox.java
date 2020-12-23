package org.geogebra.keyboard.web.factory;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.IneqBoolFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultFunctionKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.InputBoxDefaultSymbolsKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.FunctionMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.FunctionNVarMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.IneqBoolMathKeyboardFactory;
import org.geogebra.keyboard.web.factory.model.inputbox.math.VectorMatriceMathKeyboardFactory;

public class KeyboardInputBox extends KeyboardFactory {

	/**
	 * inputbox keyboard constructor
	 * @param inputBoxType type of geo lined to the inputbox
	 */
	public KeyboardInputBox(InputBoxType inputBoxType) {
		super();
		setDefaultKeyboardFactory(getMathKeyboard(inputBoxType));
		setMathKeyboardFactory(getMathKeyboard(inputBoxType));
		setFunctionKeyboardFactory(getFunctionKeyboard(inputBoxType));
		setSpecialSymbolsKeyboardFactory(new InputBoxDefaultSymbolsKeyboardFactory());
	}

	private KeyboardModelFactory getMathKeyboard(InputBoxType inputBoxType) {
		switch (inputBoxType) {
		case VECTOR_MATRIX:
			return new VectorMatriceMathKeyboardFactory();
		case INEQ_BOOL:
			return new IneqBoolMathKeyboardFactory();
		case FUNCTION:
			return new FunctionMathKeyboardFactory();
		case FUNCTION_NVAR:
			return new FunctionNVarMathKeyboardFactory();
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
		case FUNCTION_NVAR:
		default:
			return new InputBoxDefaultFunctionKeyboardFactory();
		case INEQ_BOOL:
			return new IneqBoolFunctionKeyboardFactory();
		}
	}
}
