/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.inputfield;

/**
 * Text input field with error display.
 */
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
