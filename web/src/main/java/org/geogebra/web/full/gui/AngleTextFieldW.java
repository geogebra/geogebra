package org.geogebra.web.full.gui;

import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.DummyCursor;
import org.geogebra.web.html5.gui.HasKeyboardTF;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Textfield that adds degree symbol to text
 * 
 * Later this shall inherit from MyTextField instead of TextBox
 */

public class AngleTextFieldW extends GTextBox
		implements KeyUpHandler, KeyPressHandler, KeyDownHandler,
		HasKeyboardTF {
	/** app */
	AppW app;
	private DummyCursor dummyCursor;

	/**
	 * @param columns
	 *            width
	 * @param app
	 *            app
	 */
	public AngleTextFieldW(int columns, AppW app) {
		super();
		this.app = app;
		setVisibleLength(columns);
		dummyCursor = new DummyCursor(this, app);
		this.addKeyUpHandler(this);
		this.addKeyPressHandler(this);
		this.addKeyDownHandler(this);
	}

	@Override
	public void onKeyUp(KeyUpEvent e) {

		boolean modifierKeyPressed = app.isMacOS() ? e.isControlKeyDown() : e
		        .isAltKeyDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltKeyDown() && e.isControlKeyDown()) {
			modifierKeyPressed = false;
		}

		// Application.debug(e+"");

		String insertString = "";

		switch (Character.toChars(e.getNativeEvent().getCharCode())[0]) {
		default:
			// do nothing
			break;
		case 'o':
			insertString = Unicode.DEGREE_STRING;
			break;
		case 'p':
			insertString = Unicode.PI_STRING;
			break;
		}

		if (modifierKeyPressed && !"".equals(insertString)) {
			insert(insertString);

			// e.consume();
		}
	}

	private void insert(String insertString) {
		int start = getCursorPos();
		int end = start + getSelectionLength();
		// clear selection if there is one
		if (start != end) {
			int pos = getCursorPos();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			if (pos < sb.length()) {
				setCursorPos(pos);
			}
		}
		String oldText = getText();
		// don't insert more than one degree sign or pi *in total*
		if (oldText.indexOf(Unicode.DEGREE_CHAR) == -1
				&& oldText.indexOf(Unicode.pi) == -1) {
			int pos = getCursorPos();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, pos));
			sb.append(insertString);
			sb.append(oldText.substring(pos));
			setText(sb.toString());
			setCursorPos(pos + insertString.length());
		}
	}

	@Override
	public void startOnscreenKeyboardEditing() {
		if (Browser.isAndroid()) {
			addDummyCursor();
		}
	}

	@Override
	public void endOnscreenKeyboardEditing() {
		if (Browser.isAndroid()) {
			removeDummyCursor();
		}
	}

	/**
	 * Enable onscreen keyboard
	 */
	public void enableGGBKeyboard() {
		dummyCursor.enableGGBKeyboard();
	}

	@Override
	public void addDummyCursor() {
		dummyCursor.add();
	}

	@Override
	public int removeDummyCursor() {
		return dummyCursor.remove();
	}

	/**
	 * @param caretPos
	 *            position to insert dummy cursor
	 */
	public void addDummyCursor(int caretPos) {
		dummyCursor.addAt(caretPos);
	}

	@Override
	public String getValue() {
		return getText();
	}

	@Override
	public void onKeyPress(KeyPressEvent e) {
		if (GlobalKeyDispatcherW.isBadKeyEvent(e)) {
			e.preventDefault();
			e.stopPropagation();
			return;
		}
		if (Browser.isTabletBrowser()
				&& !app.isWhiteboardActive()
				&& e.getNativeEvent().getKeyCode() != GWTKeycodes.KEY_BACKSPACE
				&& e.getNativeEvent().getKeyCode() != GWTKeycodes.KEY_ENTER
				&& e.getNativeEvent().getKeyCode() != 0) {
			if (!NavigatorUtil.isiOS()) {
				setCursorPos(removeDummyCursor(), false);
			}
			insert(Character.toString(e.getCharCode()));
			addDummyCursor();
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		dummyCursor.handleTabletKeyboard(event);
	}

	@Override
	public void onBackSpace() {
		int start = getCursorPos();
		int end = start + getSelectionLength();

		if (end - start < 1) {
			end = getCursorPos();
			start = end - 1;
		}
		if (start >= 0) {
			int cpos = removeDummyCursor();
			setSelectionRange(start, 1);
			insert("");
			addDummyCursor(cpos - 1);
		}
	}

	/**
	 * @param caretPos
	 *            caret position
	 * @param moveDummyCursor
	 *            whether dummy cursor needs to be moved
	 */
	public void setCursorPos(int caretPos, boolean moveDummyCursor) {
		if (dummyCursor.isActive() && moveDummyCursor) {
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
	public void setCursorPos(int pos) {
		setCursorPos(pos, true);
	}
}
