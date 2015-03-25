package geogebra.html5.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public interface AutoCompleteW {
	public boolean getAutoComplete();

	public List<String> resetCompletions();

	public List<String> getCompletions();

	public void setFocus(boolean b);

	public void insertString(String text);

	public void toggleSymbolButton(boolean toggled);

	public ArrayList<String> getHistory();

	public String getText();

	public void setText(String s);

	public int getAbsoluteLeft();

	public int getAbsoluteTop();

	public boolean isSuggesting();

	public void requestFocus();

	public Widget toWidget();
}
