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
	/** application */
	protected AppW app;
	/** text field */
	protected HasKeyboardTF textField;
	private boolean dummyActive = false;

	/**
	 * @param textField
	 *            text field that needs dummy cursor
	 * @param app
	 *            application
	 */
	public DummyCursor(HasKeyboardTF textField, AppW app) {
		this.textField = textField;
		this.app = app;
	}

	/**
	 * adds a dummy cursor
	 */
	public void add() {
		if (dummyActive || Browser.isIPad()) {
			return;
		}
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				int caretPos = textField.getCursorPos();
				addAt(caretPos);
			}
		});
	}

	/**
	 * adds a dummy cursor at a specified position
	 * 
	 * @param caretPos
	 *            position to add the dummy cursor at
	 */
	public void addAt(int caretPos) {
		if (dummyActive || Browser.isIPad()) {
			return;
		}
		String text = textField.getValue();
		text = text.substring(0, caretPos) + '|' + text.substring(caretPos);
		textField.setValue(text);
		textField.setCursorPos(caretPos);
		dummyActive = true;
	}

	/**
	 * removes a dummy cursor
	 *
	 * @return current cursor position
	 */
	public int remove() {
		if (!dummyActive || Browser.isIPad()) {
			return -1;
		}
		String text = textField.getValue();
		int cpos = textField.getCursorPos();
		text = text.substring(0, cpos) + text.substring(cpos + 1);
		textField.setValue(text);
		dummyActive = false;
		return cpos;
	}

	/**
	 * enables the ggb keyboard, sets textfield to readonly to prevent native
	 * keyboard
	 * 
	 */
	public void enableGGBKeyboard() {
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

	/**
	 * @return true if dummyCursor is visible
	 */
	public boolean isActive() {
		return dummyActive;
	}
}
