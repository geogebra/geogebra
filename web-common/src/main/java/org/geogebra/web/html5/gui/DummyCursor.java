package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.CursorOverlay;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.event.dom.client.KeyDownEvent;

import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * handle dummy cursor on android
 *
 */
public class DummyCursor implements FocusHandler, BlurHandler {
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
	 * Handle event coming from actual keyboard on tablet
	 * @param e keyboard event
	 */
	public void handleTabletKeyboard(KeyDownEvent e) {
		if (!Browser.isTabletBrowser() || app.isWhiteboardActive()) {
			return;
		}
		int code = e.getNativeKeyCode();
		if (code == 0 && Browser.isIPad()) {
			int arrowType = Browser.getIOSArrowKeys(e.getNativeEvent());
			if (arrowType != -1) {
				code = arrowType;
			}
		}
		switch (code) {
		case GWTKeycodes.KEY_BACKSPACE:
			textField.onBackSpace();
			break;
		case GWTKeycodes.KEY_LEFT:
			onArrowLeft();
			break;
		case GWTKeycodes.KEY_RIGHT:
			onArrowRight();
			break;
		default:
			break;
		}
	}

	private void onArrowLeft() {
		int caretPos = textField.getCursorPos();
		if (caretPos > 0) {
			textField.setCursorPos(caretPos - 1);
		}
	}

	private void onArrowRight() {
		int caretPos = textField.getCursorPos();
		if (caretPos < textField.getText().length()) {
			textField.setCursorPos(caretPos + 1);
		}
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
		String text = textField.getText();
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
		String text = textField.getText();
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
		if (app.isWhiteboardActive()) {
			return;
		}
		if (Browser.isTabletBrowser()) {
			// avoid native keyboard opening
			textField.setReadOnly(true);
		}
		textField.addFocusHandler(this);
		textField.addBlurHandler(this);
	}

	/**
	 * @return true if dummyCursor is visible
	 */
	public boolean isActive() {
		return dummyActive;
	}

	@Override
	public void onFocus(FocusEvent event) {
		if (!app.isWhiteboardActive() && textField != null) {
			app.showKeyboard(textField, true);
			textField.startOnscreenKeyboardEditing();
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (!app.isWhiteboardActive()) {
			textField.endOnscreenKeyboardEditing();
			CursorOverlay.hideKeyboard(app);
		}
	}
}
