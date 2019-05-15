package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;

import com.google.gwt.user.client.ui.Widget;

public interface AutoCompleteW {
	boolean getAutoComplete();

	List<String> resetCompletions();

	List<String> getCompletions();

	void setFocus(boolean b, boolean sv);

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

	boolean isForCAS();

	String getCommand();

	App getApplication();
}
