package geogebra.factories;

import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.main.AbstractApplication;

public class SwingFactory extends geogebra.common.factories.SwingFactory {

	// TODO: find another place for this function
	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
			AbstractApplication application) {
		return new geogebra.gui.inputfield.AutoCompleteTextField(length, application);
	}

	@Override
	public JLabel newJLabel(String string) {
		return new geogebra.javax.swing.JLabel(string);
	}

}
