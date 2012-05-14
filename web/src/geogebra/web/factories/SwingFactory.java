package geogebra.web.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.AbstractJComboBox;
import geogebra.common.javax.swing.Box;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.main.AbstractApplication;

public class SwingFactory extends geogebra.common.factories.SwingFactory {

	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
	        AbstractApplication application, Drawable drawTextField) {
		return new geogebra.web.gui.inputfield.AutoCompleteTextField(length, application, drawTextField);
	}

	@Override
	public JLabel newJLabel(String string) {
		return new geogebra.web.javax.swing.JLabel(string);
	}

	@Override
	public Box createHorizontalBox() {
		return new geogebra.web.javax.swing.Box();
	}

	@Override
    public AbstractJComboBox newJComboBox() {
	    return new geogebra.web.javax.swing.JComboBox();
    }

}
