package org.geogebra.common.kernel.validator;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;

public class NumberValidator {

	private StringParser stringParser;

	public NumberValidator(AlgebraProcessor algebraProcessor) {
		stringParser = new StringParser(algebraProcessor);
	}

	/**
	 * @param numberString The string containing a number.
	 * @param minValue The value contained in the String must be greater than this.
	 * @return If the numeric value of the String is greater than the minValue then returns the
	 * 		numeric value of the String,
	 * 		otherwise throws a NumberValueOutOfBoundsException.
	 */
	public Double getDouble(String numberString, Double minValue) {
		double number = stringParser.convertToDouble(numberString);
		if ((minValue != null && number <= minValue)
				|| Double.isNaN(number)
				|| Double.isInfinite(number)) {
			throw new NumberValueOutOfBoundsException();
		}
		return number;
	}
}
