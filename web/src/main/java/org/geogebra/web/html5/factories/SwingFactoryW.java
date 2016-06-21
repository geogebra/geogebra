package org.geogebra.web.html5.factories;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.factories.SwingFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.javax.swing.GBoxW;

/** Html5 factory for euclidian view components */
public class SwingFactoryW extends SwingFactory {

	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
	        App application, Drawable drawTextField) {
		return new AutoCompleteTextFieldW(length,
		        application, drawTextField);
	}

	@Override
	public GBox createHorizontalBox(EuclidianController style) {
		return new GBoxW(style);
	}


}
