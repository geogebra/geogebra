package org.geogebra.common.cas.view;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.Localization;

public class CASCellProcessor {

	private Localization localization;

	public CASCellProcessor(Localization loc) {
		this.localization = loc;
	}
	/**
	 * Fixes common input errors and returns the corrected input String.
	 * 
	 * @param input
	 * @return
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
				&& !cellValue.getInput(StringTemplate.defaultTemplate)
						.equals(fixedInput)) {
			cellValue.setInput(fixedInput);
		}
		return evalText;
	}
}
