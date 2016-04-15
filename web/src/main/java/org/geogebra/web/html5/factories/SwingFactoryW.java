package org.geogebra.web.html5.factories;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.factories.SwingFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.AbstractJComboBox;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.javax.swing.GLabel;
import org.geogebra.common.javax.swing.GPanel;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.javax.swing.GBoxW;
import org.geogebra.web.html5.javax.swing.GComboBoxW;
import org.geogebra.web.html5.javax.swing.GLabelW;
import org.geogebra.web.html5.javax.swing.GPanelW;

public class SwingFactoryW extends SwingFactory {

	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
	        App application, Drawable drawTextField) {
		return new AutoCompleteTextFieldW(length,
		        application, drawTextField);
	}

	@Override
	public GLabel newJLabel(String string, boolean bool) {
		return new GLabelW(string, bool);
	}

	@Override
	public GBox createHorizontalBox(EuclidianController style) {
		return new GBoxW(style);
	}

	@Override
	public AbstractJComboBox newJComboBox(App app, int view) {
		return new GComboBoxW(app, view);
	}

	@Override
	public GPanel newGPanel() {
		return new GPanelW();
	}

}
