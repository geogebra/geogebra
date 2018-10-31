package org.geogebra.common.gui.view.table;

/**
 * Thrown when the values set in table view are invalid.
 */
public class InvalidValuesException extends Exception {

	/**
	 * Construct an InvalidValuesException.
	 *
	 * @param description description
	 */
	InvalidValuesException(String description) {
		super(description);
	}
}
