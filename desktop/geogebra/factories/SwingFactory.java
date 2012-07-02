package geogebra.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.GBox;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.main.AbstractApplication;
import geogebra.javax.swing.GComboBoxD;

public class SwingFactory extends geogebra.common.factories.SwingFactory {

	// TODO: find another place for this function
	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
			AbstractApplication application, Drawable drawTextField) {
		return new geogebra.gui.inputfield.AutoCompleteTextFieldD(length, application, drawTextField);
	}

	@Override
	public GLabel newJLabel(String string) {
		return new geogebra.javax.swing.GLabelD(string);
	}

	@Override
	public GBox createHorizontalBox() {
		return new geogebra.javax.swing.BoxD(javax.swing.Box.createHorizontalBox());
	}

	@Override
	public GComboBoxD newJComboBox() {
		return new geogebra.javax.swing.GComboBoxD();
	}

}
