package org.geogebra.test.euclidian;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.ViewTextField;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.App;

public class TextFieldCommonJre extends ViewTextField {

	/**
	 * @param euclidianView view
	 */
	public TextFieldCommonJre(EuclidianView euclidianView) {
		super(euclidianView);
	}

	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length, App application, Drawable drawTextField) {
		return new AutoCompleteTextFieldC();
	}

	@Override
	public GBox createHorizontalBox(EuclidianController style) {
		return new GBoxC();
	}
}
