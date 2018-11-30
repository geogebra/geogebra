package org.geogebra.common.kernel.validator;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;

public class NumberValidator {

	private StringParser stringParser;

	public NumberValidator(AlgebraProcessor algebraProcessor) {
		stringParser = new StringParser(algebraProcessor);
	}

	public Double getDouble(String numberString, Double minValue) {
		double number = stringParser.convertToDouble(numberString);
		if (minValue != null && number <= minValue) {
			throw new NumberValueOutOfBoundsException();
		}
		return number;
	}
}
