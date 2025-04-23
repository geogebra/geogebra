package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Input field with autocomplete.
 */
public interface AutoCompleteW {
	boolean getAutoComplete();

	void setFocus(boolean focus);

	void insertString(String text);

	String getText();

	void setText(String s);

	boolean isSuggesting();

	void requestFocus();

	Widget toWidget();

	void autocomplete(String s);

	void updatePosition(AbstractSuggestionDisplay sug);

	/**
	 * @return current command: in context where commands are allowed that means current word,
	 * otherwise empty string.
	 */
	String getCommand();

	AppW getApplication();
}
