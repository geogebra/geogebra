package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Input field with autocomplete.
 */
public interface AutoCompleteW {
	@MissingDoc
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

	@MissingDoc
	String getText();

	/**
	 * Set text content.
	 * @param s text content.
	 */
	void setText(String s);

	@MissingDoc
	boolean isSuggesting();

	@MissingDoc
	void requestFocus();

	@MissingDoc
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

	@MissingDoc
	AppW getApplication();
}
