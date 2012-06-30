package geogebra.web.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.AbstractJComboBox;
import geogebra.common.javax.swing.GBox;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.main.AbstractApplication;

public class SwingFactory extends geogebra.common.factories.SwingFactory {

	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
	        AbstractApplication application, Drawable drawTextField) {
		return new geogebra.web.gui.inputfield.AutoCompleteTextField(length, application, drawTextField);
	}

	@Override
	public GLabel newJLabel(String string) {
		return new geogebra.web.javax.swing.GLabelW(string);
	}

	@Override
	public GBox createHorizontalBox() {
		return new geogebra.web.javax.swing.GBoxW();
	}

	@Override
    public AbstractJComboBox newJComboBox() {
	    return new geogebra.web.javax.swing.GComboBoxW();
    }

}
