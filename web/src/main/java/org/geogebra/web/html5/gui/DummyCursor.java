package org.geogebra.web.html5.gui;

import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.FieldHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;

/**
 * handle dummy cursor on android
 *
 */
public class DummyCursor {

	/**
	 * adds a dummy cursor
	 * 
	 * @param dummyCursor
	 *            true if dummy cursor is visible
	 * @param textField
	 *            the text field to add the dummy cursor to
	 */
	public static void addDummyCursor(final boolean dummyCursor,
			final HasKeyboardTF textField) {
		if (dummyCursor || Browser.isIPad()) {
			return;
		}
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				int caretPos = textField.getCursorPos();
				addDummyCursor(caretPos, dummyCursor, textField);
			}
		});
	}

	/**
	 * adds a dummy cursor at a specified position
	 * 
	 * @param caretPos
	 *            position to add the dummy cursor at
	 * @param dummyCursor
	 *            true if dummy cursor is visible
	 * @param textField
	 *            the textfield to add the dummy cursor to
	 */
	public static void addDummyCursor(int caretPos, boolean dummyCursor,
			HasKeyboardTF textField) {
		if (dummyCursor || Browser.isIPad()) {
			return;
		}
		String text = textField.getValue();
		text = text.substring(0, caretPos) + '|' + text.substring(caretPos);
		textField.setValue(text);
		textField.setCursorPos(caretPos);
		textField.toggleDummyCursor(true);
	}

	/**
	 * removes a dummy cursor
	 * 
	 * @param dummyCursor
	 *            true if dummy cursor is visible
	 * @param textField
	 *            the text field to remove the dummy cursor from
	 * @return current cursor position
	 */
	public static int removeDummyCursor(boolean dummyCursor,
			HasKeyboardTF textField) {
		if (!dummyCursor || Browser.isIPad()) {
			return -1;
		}
		String text = textField.getValue();
		int cpos = textField.getCursorPos();
		text = text.substring(0, cpos) + text.substring(cpos + 1);
		textField.setValue(text);
		textField.toggleDummyCursor(false);
		return cpos;
	}

	/**
	 * enables the ggb keyboard, sets textfield to readonly to prevent native
	 * keyboard
	 * 
	 * @param app
	 *            application
	 * @param textField
	 *            text field to enable keyboard for
	 */
	public static void enableGGBKeyboard(final AppW app,
			final HasKeyboardTF textField) {
		if (!app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			return;
		}
		if (Browser.isTabletBrowser()) {
			// avoid native keyboard opening
			textField.setReadOnly(true);
		}
		textField.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				FieldHandler.focusGained(textField, app);
			}
		});
		textField.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				FieldHandler.focusLost(textField, app);
			}
		});
	}
}
