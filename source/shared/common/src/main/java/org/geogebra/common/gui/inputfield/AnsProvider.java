package org.geogebra.common.gui.inputfield;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Provides the text expected after pressing the ANS button.
 */
public class AnsProvider {

	private HasLastItem lastItemProvider;

	public AnsProvider(HasLastItem lastItemProvider) {
		this.lastItemProvider = lastItemProvider;
	}

	/**
	 * @param currentElement The GeoElement of the current AV input.
	 * @param currentInput The text in the current AV input.
	 * @return The ANS text with parentheses or quotes if required.
	 */
	public String getAns(GeoElement currentElement, String currentInput) {
		String lastItem = getLastItemWithOptionalBrackets(currentElement, currentInput);
		if (doesLastItemNeedQuotes(lastItem)) {
			return "\"" + lastItem + "\"";
		}
		return lastItem;
	}

	private String getLastItemWithOptionalBrackets(GeoElement currentElement, String currentInput) {
		String lastItem = lastItemProvider.getPreviousItemFrom(currentElement);
		if (!currentInput.isEmpty() && doesLastItemNeedParentheses(lastItem)) {
			return "(" + lastItem + ")";
		}
		return lastItem;
	}

	private boolean doesLastItemNeedParentheses(String lastItem) {
		return !lastItem.isEmpty()
				&& !lastItemProvider.isLastItemText()
				&& !lastItemProvider.isLastItemSimpleNumber();
	}

	private boolean doesLastItemNeedQuotes(String lastItem) {
		return !lastItem.isEmpty()
				&& lastItemProvider.isLastItemText()
				&& !lastItem.startsWith("\"")
				&& !lastItem.endsWith("\"");
	}

	/**
	 * @param currentElement The GeoElement of the current AV input.
	 * @param currentInput The text in the current AV input.
	 * @return The ANS text without quotes.
	 */
	public String getAnsForTextInput(GeoElement currentElement, String currentInput) {
		return getLastItemWithOptionalBrackets(currentElement, currentInput);
	}
}
