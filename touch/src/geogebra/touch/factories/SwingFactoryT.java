package geogebra.touch.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.main.App;
import geogebra.html5.factories.SwingFactoryW;
import geogebra.touch.gui.dialogs.AutoCompleteTextFieldT;

public class SwingFactoryT extends SwingFactoryW {
	@Override
	public AutoCompleteTextField newAutoCompleteTextField(int length,
			App application, Drawable drawTextField) {
		return new AutoCompleteTextFieldT(length, application, drawTextField);
	}
}
