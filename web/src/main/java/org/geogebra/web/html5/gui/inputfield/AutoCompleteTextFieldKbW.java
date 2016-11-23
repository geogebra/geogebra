package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.euclidian.event.AutoCompleteTextFieldKb;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AutoCompleteTextFieldKbW extends AutoCompleteTextFieldW implements
		FocusHandler, AutoCompleteTextFieldKb {

	public AutoCompleteTextFieldKbW(int i, App app) {
		super(i, app);
		
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			addFocusHandler(this);
		}
	}
	

	public void onFocus(FocusEvent event) {
		Object source = event.getSource();

		if (source instanceof TextBox) {
			TextBox tb = (TextBox) event.getSource();
			Widget parent = tb.getParent().getParent();
			if (parent instanceof AutoCompleteTextFieldW) {
				if (Browser.isAndroid() || Browser.isIPad()) {
					tb.setFocus(false);
					event.preventDefault();
					((AutoCompleteTextFieldW) parent)
							.startOnscreenKeyboardEditing();
					}
				app.showKeyboard((AutoCompleteTextFieldW) parent, true);
			}
		}
	}
}
