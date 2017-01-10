package org.geogebra.web.web.gui.util;

import org.geogebra.common.gui.inputfield.AltKeys;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.HasKeyboardTF;
import org.geogebra.web.html5.gui.inputfield.FieldHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextArea;

/** Class for future syntax highlighting, line numbering and so on. */
public class ScriptArea extends TextArea
 implements KeyPressHandler,
		KeyDownHandler, KeyUpHandler, HasKeyboardTF {

	private boolean dummyCursor = false;

	/**
	 * Creates new script area
	 */
	public ScriptArea() {
		setStyleName("scriptArea");
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		addKeyUpHandler(this);
	}

	public void onKeyUp(KeyUpEvent e) {
		if (e.isAltKeyDown() && !e.isControlKeyDown()) {

			String s = AltKeys.getAltSymbols((char) e.getNativeKeyCode(),
					e.isShiftKeyDown(), true);

			if (s != null) {
				insertString(s);
			}
		}
	}

	private void setText(int start, int end, String text) {
		// clear selection if there is one		
		if (start != end) {
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			setCursorPos(start);
		}

		int pos = getCursorPos();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));
		setText(sb.toString());

		// setCaretPosition(pos + text.length());
		final int newPos = pos + text.length();
		setCursorPos(newPos, false);
	}

	private int getSelectionEnd() {
		return getSelectionStart() + getSelectionLength();
	}

	private int getSelectionStart() {
		return getText().indexOf(getSelectedText());
	}

	/**
	 * @param caretPos
	 *            caret position
	 * @param moveDummyCursor
	 *            whether dummy cursor needs to be moved
	 */
	public void setCursorPos(int caretPos, boolean moveDummyCursor) {
		if (dummyCursor && moveDummyCursor) {
			if (caretPos == this.getText().length()) {
				return;
			}
			removeDummyCursor();
			addDummyCursor(caretPos);
		} else {
			setSelectionRange(caretPos, 0);
		}
	}
	
	@Override
	public void setCursorPos(int pos){
		setCursorPos(pos, true);
	}

	/**
	 * @param text
	 *            inserted text
	 */
	public void insertString(String text) {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		setText(start, end, text);
		// if (insertHandler != null) {
		// insertHandler.onInsert(text);
		// }
	}

	public void onKeyDown(KeyDownEvent e) {

		if (GlobalKeyDispatcherW.isBadKeyEvent(e)
				|| e.getNativeKeyCode() == GWTKeycodes.KEY_F1) {
			e.preventDefault();
		}

	}

	public void onKeyPress(KeyPressEvent e) {
		if (GlobalKeyDispatcherW.isBadKeyEvent(e)) {
			e.preventDefault();
			e.stopPropagation();
			return;
		}
	}

	public void startOnscreenKeyboardEditing() {
		if (Browser.isAndroid()) {
			addDummyCursor();
		}
	}

	public void endOnscreenKeyboardEditing() {
		if (Browser.isAndroid()) {
			removeDummyCursor();
		}
	}

	public void addDummyCursor() {
		if (dummyCursor) {
			return;
		}
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				int caretPos = getCursorPos();
				addDummyCursor(caretPos);
			}
		});
	}

	public int removeDummyCursor() {
		if (!dummyCursor) {
			return -1;
		}
		String text = getText();
		int cpos = getCursorPos();
		text = text.substring(0, cpos) + text.substring(cpos + 1);

		setValue(text);
		dummyCursor = false;
		return cpos;
	}

	/**
	 * @param caretPos
	 *            caret position
	 */
	public void addDummyCursor(int caretPos) {
		if (dummyCursor) {
			return;
		}
		String text = getText();
		text = text.substring(0, caretPos) + '|' + text.substring(caretPos);

		setValue(text);
		setCursorPos(caretPos);
		dummyCursor = true;
	}

	public void setFocus(boolean focus, boolean scheduled) {
		// TODO Auto-generated method stub

	}

	public void ensureEditing() {
		// TODO Auto-generated method stub
	}

	public void onEnter(boolean b) {
		// TODO Auto-generated method stub

	}

	public boolean needsAutofocus() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param app
	 *            application
	 */
	public void enableGGBKeyboard(final AppW app) {
		if (!app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			return;
		}

		if (Browser.isTabletBrowser()) {
			// avoid native keyboard opening
			setReadOnly(true);
		}

		addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				FieldHandler.focusGained(ScriptArea.this, app);
			}
		});

		addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				FieldHandler.focusLost(ScriptArea.this, app);
			}
		});
	}

}
