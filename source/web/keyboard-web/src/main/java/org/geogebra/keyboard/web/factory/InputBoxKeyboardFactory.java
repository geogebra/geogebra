package org.geogebra.keyboard.web.factory;

import java.util.List;
import java.util.Objects;

import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
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

public class InputBoxKeyboardFactory extends DefaultKeyboardFactory {

	private final InputBoxType inputBoxType;
	private final List<String> functionVars;

	/**
	 * inputbox keyboard constructor
	 * @param inputBoxType type of geo lined to the inputbox
	 * @param functionVars function vars in case of a function
	 */
	public InputBoxKeyboardFactory(InputBoxType inputBoxType, List<String> functionVars) {
		this.inputBoxType = inputBoxType;
		this.functionVars = functionVars;
		init();
	}

	private void init() {
		defaultKeyboardModelFactory = getMathKeyboard(inputBoxType, functionVars);
		mathKeyboardFactory = getMathKeyboard(inputBoxType, functionVars);
		functionKeyboardFactory = getFunctionKeyboard(inputBoxType);
		specialSymbolsKeyboardFactory = new InputBoxDefaultSymbolsKeyboardFactory();
		letterKeyboardFactory = new InputBoxDefaultLettersKeyboardFactory();
		greekKeyboardFactory = new InputBoxDefaultGreekKeyboardFactory();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		InputBoxKeyboardFactory that = (InputBoxKeyboardFactory) o;
		return inputBoxType == that.inputBoxType && Objects
				.equals(functionVars, that.functionVars);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inputBoxType, functionVars);
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
