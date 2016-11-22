package org.geogebra.web.web.gui.view.probcalculator;

import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FieldFocusHandler implements FocusHandler {

	private AppW app;

	public FieldFocusHandler(AppW appl) {
		app = appl;
	}

	public void onFocus(FocusEvent event) {
		Object source = event.getSource();

		if (source instanceof TextBox) {
			TextBox tb = (TextBox) event.getSource();
			if (app.has(Feature.ONSCREEN_KEYBOARD_AT_PROBCALC)) {
				Widget parent = tb.getParent().getParent();
				if (parent instanceof AutoCompleteTextFieldW) {
					if (Browser.isAndroid() || Browser.isIPad()) {
						tb.setFocus(false);
						event.preventDefault();
						((AutoCompleteTextFieldW) parent)
							.startOnscreenKeyboardEditing();
					} else {
						tb.selectAll();
					}
					app.showKeyboard((AutoCompleteTextFieldW) parent, true);
				}
			} else {
				tb.selectAll();
			}
		}

	}

}
