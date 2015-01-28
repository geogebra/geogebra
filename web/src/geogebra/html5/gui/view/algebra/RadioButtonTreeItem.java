package geogebra.html5.gui.view.algebra;

import java.util.List;

public interface RadioButtonTreeItem {

	// methods to stop editing (from DrawEquationWeb)
	public void stopEditing(String latex);
	public boolean stopNewFormulaCreation(String latex);

	// methods to be used by CompletionsPopup class
	public boolean getAutoComplete();
	public void resetCompletions();
	public List<String> getCompletions();
}
