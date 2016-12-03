package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.euclidian.event.AutoCompleteTextFieldKb;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;

public class AutoCompleteTextFieldKbW extends AutoCompleteTextFieldW implements
		AutoCompleteTextFieldKb {

	public AutoCompleteTextFieldKbW(int i, AppW app) {
		super(i, app);
		
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			addFocusHandler(new FieldHandler(app));
			addBlurHandler(new FieldHandler(app));
		}
	}
	
}
