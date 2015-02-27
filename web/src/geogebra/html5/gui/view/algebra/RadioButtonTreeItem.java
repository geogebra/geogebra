package geogebra.html5.gui.view.algebra;

import geogebra.common.util.AsyncOperation;


public interface RadioButtonTreeItem {

	// methods to stop editing (from DrawEquationWeb)
	public boolean stopEditing(String latex);

	public boolean stopNewFormulaCreation(String a, String b, AsyncOperation cb);

	// in case of NewRadioButtonTreeItem (new formula creation)
	public boolean popupSuggestions();
	public boolean hideSuggestions();
	public boolean shuffleSuggestions(boolean down);
	public void scrollIntoView();
}
