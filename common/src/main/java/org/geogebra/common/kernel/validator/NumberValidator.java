package org.geogebra.common.kernel.validator;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.geogebra.common.util.DoubleUtil;

/**
 * Checks string to be a number with given properties
 */
public class NumberValidator {

	public static final String NUMBER_FORMAT_ERROR_MESSAGE_KEY = "InputError.Enter_a_number";
	public static final String NUMBER_TOO_SMALL_ERROR_MESSAGE_KEY = "InputError."
			+ "EndValueLessThanStartValue";
	public static final String NUMBER_NEGATIVE_ERROR_MESSAGE_KEY = "InputError."
			+ "Enter_a_number_greater_than_0";

	private AlgebraProcessor stringParser;

	/**
	 * @param algebraProcessor
	 *            algebra processor
	 */
	public NumberValidator(AlgebraProcessor algebraProcessor) {
		stringParser = algebraProcessor;
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

	/**
	 * @param numberString
	 *            The string containing a number.
	 * @param minValue
	 *            The value contained in the String must be greater than this.
	 * @return If the numeric value of the String is greater or equal to the
	 *         minValue (with epsilon difference) then returns the numeric value
	 *         of the String, otherwise throws a
	 *         NumberValueOutOfBoundsException.
	 */
	public double getDoubleGreaterOrEqual(String numberString,
			Double minValue) {
		double number = stringParser.convertToDouble(numberString);
		if (!MyDouble.isFinite(number)) {
			throw new NumberFormatException("The number must be finite");
		}
		if (minValue != null) {
			if (DoubleUtil.isEqual(number, minValue)) {
				number = minValue;
			} else if (number < minValue) {
				throw new NumberValueOutOfBoundsException();
			}
		}
		return number;
	}
}
