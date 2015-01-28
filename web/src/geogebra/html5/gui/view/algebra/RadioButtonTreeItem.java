package geogebra.html5.gui.view.algebra;


public interface RadioButtonTreeItem {

	// methods to stop editing (from DrawEquationWeb)
	public void stopEditing(String latex);
	public boolean stopNewFormulaCreation(String latex);
}
