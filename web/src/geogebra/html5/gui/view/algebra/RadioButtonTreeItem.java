package geogebra.html5.gui.view.algebra;

import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;

import com.google.gwt.dom.client.Element;


public interface RadioButtonTreeItem {
	public Element getElement();
	public App getApplication();

	// methods to stop editing (from DrawEquationWeb)
	public boolean stopEditing(String latex);

	public boolean stopNewFormulaCreation(String a, String b, AsyncOperation cb);

	// in case of NewRadioButtonTreeItem (new formula creation)
	public boolean popupSuggestions();
	public boolean hideSuggestions();
	public boolean shuffleSuggestions(boolean down);
	public void scrollIntoView();
}
