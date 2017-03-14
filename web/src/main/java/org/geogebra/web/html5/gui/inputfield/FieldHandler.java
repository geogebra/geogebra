package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.HasKeyboardTF;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FieldHandler implements FocusHandler, BlurHandler {

	AppW appl;

	public FieldHandler(AppW app) {
		appl = app;
	}

	@Override
	public void onFocus(FocusEvent event) {
		Object source = event.getSource();

		if (source instanceof TextBox) {
			TextBox tb = (TextBox) event.getSource();
			Widget parent = tb.getParent().getParent();
			if (parent instanceof AutoCompleteTextFieldW) {
				event.preventDefault();
				focusGained((AutoCompleteTextFieldW) parent, appl);

			}
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		// TODO: 1st parameter
		focusLost(null, appl);
	}

	public static void focusGained(HasKeyboardTF field, AppW app) {
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			if (field != null) {
				field.startOnscreenKeyboardEditing();
				app.showKeyboard(field, true);
			}
		}
	}

	public static void focusLost(HasKeyboardTF field, final AppW app) {
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			field.endOnscreenKeyboardEditing();
			if (CancelEventTimer.cancelKeyboardHide()) {
				return;
			}
			if (app.hasPopup() && app.has(Feature.DIALOGS_OVERLAP_KEYBOARD)) {
				app.getGuiManager().setOnScreenKeyboardTextField(null);
				return;
			}
			app.hideKeyboard();
		}
	}

}
