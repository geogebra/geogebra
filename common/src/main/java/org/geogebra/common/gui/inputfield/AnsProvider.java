package org.geogebra.common.gui.inputfield;

/**
 * Provides the text expected after pressing the ANS button.
 */
public class AnsProvider {

	private HasLastItem lastItemProvider;

	public AnsProvider(HasLastItem lastItemProvider) {
		this.lastItemProvider = lastItemProvider;
	}

	/**
	 * @param currentInput The text in the current AV input.
	 * @return The ANS text with parentheses or quotes if required.
	 */
	public String getAns(String currentInput) {
		String lastItem = getLastItemWithOptionalBrackets(currentInput);
		if (doesLastItemNeedQuotes(lastItem)) {
			return "\"" + lastItem + "\"";
		}
		return lastItem;
	}

	private String getLastItemWithOptionalBrackets(String currentInput) {
		String lastItem = lastItemProvider.getLastItem();
		if (!currentInput.isEmpty() && doesLastItemNeedParentheses()) {
			return "(" + lastItem + ")";
		}
		return lastItem;
	}

	private boolean doesLastItemNeedParentheses() {
		return !lastItemProvider.isLastItemText() && !lastItemProvider.isLastItemSimpleNumber();
	}

	private boolean doesLastItemNeedQuotes(String lastItem) {
		return lastItemProvider.isLastItemText()
				&& !lastItem.startsWith("\"")
				&& !lastItem.endsWith("\"");
	}

	/**
	 * @param currentInput The text in the current AV input.
	 * @return The ANS text without quotes.
	 */
	public String getAnsForTextInput(String currentInput) {
		return getLastItemWithOptionalBrackets(currentInput);
	}
}
