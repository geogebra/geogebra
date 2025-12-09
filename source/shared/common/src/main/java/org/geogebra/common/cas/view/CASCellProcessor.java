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

package org.geogebra.common.cas.view;

import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.Localization;

/**
 * Helper class for processing CAS input
 */
public class CASCellProcessor {

	private Localization localization;

	/**
	 * @param loc
	 *            localization
	 */
	public CASCellProcessor(Localization loc) {
		this.localization = loc;
	}
	
	/**
	 * Fixes common input errors and returns the corrected input String.
	 * 
	 * @param input
	 *            CAS input
	 * @return fixed input
	 */
	public String fixInputErrors(String input) {
		String inputTrim = input.trim();

		// replace a := with Delete[a]
		if (inputTrim.endsWith(":=")) {
			inputTrim = localization.getCommand("Delete")
					+ "["
					+ inputTrim.substring(0, inputTrim.length() - 2).trim()
					+ "];";
		}

		// remove trailing =
		else if (inputTrim.endsWith("=")) {
			inputTrim = inputTrim.substring(0, inputTrim.length() - 1);
		}

		return inputTrim;
	}

	/**
	 * @param cellValue
	 *            cas cell
	 * @param selRowInput
	 *            input
	 * @param staticReferenceFound
	 *            whether static reference (#) was used
	 * @return fixed input
	 */
	public String fixInput(GeoCasCell cellValue, String selRowInput,
			boolean staticReferenceFound) {
		String evalText = null;
		String fixedInput = fixInputErrors(selRowInput);
		if (!fixedInput.equals(selRowInput)) {
			cellValue.setInput(fixedInput);
			evalText = fixedInput;
		}
		// fix GGB-1593
		if (cellValue.getTwinGeo() != null && !staticReferenceFound
				&& !cellValue.getLocalizedInput()
						.equals(fixedInput)) {
			cellValue.setInput(fixedInput);
		}
		return evalText;
	}
}
