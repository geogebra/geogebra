package org.geogebra.common.properties;

/**
 * A property that is represented as a String.
 */
public interface StringProperty extends ValuedProperty<String> {

	/**
	 * Validates the value and returns a boolean whether it was valid or not.
	 * @return a boolean whether the value is valid or not
	 */
	boolean isValidValue(String value);

	/**
	 * Returns the error message for invalid input.
	 * @return the error message for invalid input
	 */
	String getInvalidValueErrorMessage();
}
