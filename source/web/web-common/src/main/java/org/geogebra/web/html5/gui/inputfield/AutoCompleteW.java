package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.web.html5.main.AppW;

/**
 * Input field with autocomplete.
 */
public interface AutoCompleteW {
	/**
	 * @return whether autocomplete is active
	 */
	boolean getAutoComplete();

	/**
	 * Focus or blur the input field.
	 * @param focus whether to focus
	 */
	void setFocus(boolean focus);

	/**
	 * Insert a string at caret position.
	 * @param text string to insert
	 */
	void insertString(String text);

	/**
	 * @return text value
	 */
	String getText();

	/**
	 * Set text content.
	 * @param s text content.
	 */
	void setText(String s);

	/**
	 * @return whether the autocomplete suggestion panel is open
	 */
	boolean isSuggesting();

	/**
	 * Move focus to the text field.
	 */
	void requestFocus();

	/**
	 * Insert autocompletion result.
	 * @param s autocompletion result
	 */
	void autocomplete(String s);

	/**
	 * @return current command: in context where commands are allowed that means current word,
	 * otherwise empty string.
	 */
	String getCommand();

	/**
	 * @return parent application
	 */
	AppW getApplication();
}
