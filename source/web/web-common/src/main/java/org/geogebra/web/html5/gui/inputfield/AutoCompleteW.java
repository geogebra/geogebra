package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Input field with autocomplete.
 */
public interface AutoCompleteW {
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

	String getText();

	/**
	 * Set text content.
	 * @param s text content.
	 */
	void setText(String s);

	boolean isSuggesting();

	void requestFocus();

	Widget toWidget();

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

	AppW getApplication();
}
