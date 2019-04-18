package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.util.debug.Log;
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

	/**
	 * Handle focus event.
	 * 
	 * @param field
	 *            textfield
	 * @param app
	 *            app
	 */
	public static void focusGained(HasKeyboardTF field, AppW app) {
		Log.debug("focus gained in fieldhandler");
		if (!app.isWhiteboardActive() && field != null) {
			app.showKeyboard(field, false);
			field.startOnscreenKeyboardEditing();
		}
	}

	/**
	 * Handle blur event.
	 * 
	 * @param field
	 *            textfield
	 * @param app
	 *            app
	 */
	public static void focusLost(HasKeyboardTF field, final AppW app) {
		if (!app.isWhiteboardActive()) {
			field.endOnscreenKeyboardEditing();
			if (CancelEventTimer.cancelKeyboardHide()) {
				return;
			}
			if (app.hasPopup()) {
				app.getGuiManager().setOnScreenKeyboardTextField(null);
				return;
			}
			app.hideKeyboard();
		}
	}

}
