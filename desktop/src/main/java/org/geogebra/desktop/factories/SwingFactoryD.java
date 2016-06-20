package org.geogebra.desktop.factories;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.factories.SwingFactory;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.javax.swing.GBoxD;

public class SwingFactoryD extends SwingFactory {

	// TODO: find another place for this function
	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
			App application, Drawable drawTextField) {
		return new AutoCompleteTextFieldD(length,
				application, drawTextField);
	}


	@Override
	public GBox createHorizontalBox(EuclidianController style) {
		return new GBoxD(
				javax.swing.Box.createHorizontalBox());
	}



}
