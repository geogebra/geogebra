package org.geogebra.common.gui.view.table;

import org.geogebra.common.main.Localization;

/**
 * Thrown when the values set in table view are invalid.
 */
public class InvalidValuesException extends Exception {

	private String key;
	/**
	 * Construct an InvalidValuesException.
	 *
	 * @param description description
	 */
	InvalidValuesException(String key, String description) {
		super(description);
		this.key = key;
	}

	/**
	 * 
	 * @param loc
	 *            {@link Localization}
	 * @return the localized error message.
	 */
	public String getLocalizedMessage(Localization loc) {
		String msg = loc.getError(key);
		return key.equals(msg) ? getLocalizedMessage() : msg;
	}
}
