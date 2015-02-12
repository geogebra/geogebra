package geogebra.html5.gui.view.algebra;


public interface RadioButtonTreeItem {

	// methods to stop editing (from DrawEquationWeb)
	public void stopEditing(String latex);
	public boolean stopNewFormulaCreation(String a, String b);

	// in case of NewRadioButtonTreeItem (new formula creation)
	public boolean popupSuggestions();
	public boolean hideSuggestions();
	public boolean shuffleSuggestions(boolean down);
}
