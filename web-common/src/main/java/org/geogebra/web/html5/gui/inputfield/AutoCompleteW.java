package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

public interface AutoCompleteW {
	boolean getAutoComplete();

	void setFocus(boolean focus);

	void insertString(String text);

	ArrayList<String> getHistory();

	String getText();

	void setText(String s);

	int getAbsoluteLeft();

	int getAbsoluteTop();

	boolean isSuggesting();

	void requestFocus();

	Widget toWidget();

	void autocomplete(String s);

	void updatePosition(AbstractSuggestionDisplay sug);

	String getCommand();

	AppW getApplication();
}
