package org.geogebra.common.kernel.validator;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.parser.stringparser.StringParser;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;

public class NumberValidator {

	public static final String NUMBER_FORMAT_ERROR_MESSAGE_KEY = "InputError.Enter_a_number";
	public static final String NUMBER_TOO_SMALL_ERROR_MESSAGE_KEY = "InputError."
			+ "EndValueLessThanStartValue";
	public static final String NUMBER_NEGATIVE_ERROR_MESSAGE_KEY = "InputError."
			+ "Enter_a_number_greater_than_0";

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
	public double getDouble(String numberString, Double minValue) {
		double number = stringParser.convertToDouble(numberString);
		if (!MyDouble.isFinite(number)) {
			throw new NumberFormatException("The number must be finite");
		}
		if (minValue != null && number <= minValue) {
			throw new NumberValueOutOfBoundsException();
		}
		return number;
	}
}
