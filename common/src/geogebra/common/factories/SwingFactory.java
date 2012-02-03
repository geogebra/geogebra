package geogebra.common.factories;

import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.main.AbstractApplication;

public abstract class SwingFactory {
	public static SwingFactory prototype = null;

	// TODO: find another place for this function
	public abstract AutoCompleteTextField newAutoCompleteTextField(int length,
			AbstractApplication application);

	public abstract JLabel newJLabel(String string);
}
