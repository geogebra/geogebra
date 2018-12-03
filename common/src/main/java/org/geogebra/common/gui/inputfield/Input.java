package org.geogebra.common.gui.inputfield;

public interface Input {

	/**
	 * @return the text contained by the input.
	 */
	String getText();

	/**
	 * Shows an error message.
	 * @param errorMessage The error message to be shown.
	 */
	void showError(String errorMessage);

	/**
	 * Stops showing the error message and the input is set back to its default state.
	 */
	void setErrorResolved();
}
