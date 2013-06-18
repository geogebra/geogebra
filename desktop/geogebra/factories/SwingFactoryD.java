package geogebra.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.GBox;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.javax.swing.GPanel;
import geogebra.common.main.App;
import geogebra.javax.swing.GComboBoxD;

public class SwingFactoryD extends geogebra.common.factories.SwingFactory {

	// TODO: find another place for this function
	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
			App application, Drawable drawTextField) {
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

	@Override
	public GPanel newGPanel() {
		return new geogebra.javax.swing.GPanelD();
	}

}
