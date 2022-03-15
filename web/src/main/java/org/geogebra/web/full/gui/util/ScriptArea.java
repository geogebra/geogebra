package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.DummyCursor;
import org.geogebra.web.html5.gui.HasKeyboardTF;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.himamis.retex.editor.share.util.AltKeys;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/** Class for future syntax highlighting, line numbering and so on. */
public class ScriptArea extends TextArea
 implements KeyPressHandler,
		KeyDownHandler, KeyUpHandler, HasKeyboardTF {

	/** application */
	protected AppW app;
	private DummyCursor dummyCursor;
	private boolean isFocused = false;

	/**
	 * Creates new script area
	 * 
	 * @param app
	 *            application
	 */
	public ScriptArea(AppW app) {
		this.app = app;
		setStyleName("scriptArea");
		dummyCursor = new DummyCursor(this, app);
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		addKeyUpHandler(this);
	}

	@Override
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

	@Override
	public void onKeyDown(KeyDownEvent e) {

		if (GlobalKeyDispatcherW.isBadKeyEvent(e)
				|| e.getNativeKeyCode() == GWTKeycodes.KEY_F1) {
			e.preventDefault();
		}
		handleTabletKeyboard(e);
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
				&& e.getNativeEvent().getKeyCode() != 0) {
			insertString(Character.toString(e.getCharCode()));
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
	 *            caret position
	 */
	public void addDummyCursor(int caretPos) {
		dummyCursor.addAt(caretPos);
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);
		isFocused = focus;
	}

	@Override
	public void ensureEditing() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean needsAutofocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasFocus() {
		return isFocused;
	}

	@Override
	public boolean acceptsCommandInserts() {
		return false;
	}

	/**
	 * Enable keyboard.
	 */
	public void enableGGBKeyboard() {
		dummyCursor.enableGGBKeyboard();
	}

	private void handleTabletKeyboard(KeyDownEvent e) {
		if (!Browser.isTabletBrowser() || app.isWhiteboardActive()) {
			return;
		}
		int keyCode = e.getNativeKeyCode();
		if (keyCode == 0 && Browser.isIPad()) {
			int arrowType = Browser.getIOSArrowKeys(e.getNativeEvent());
			if (arrowType != -1) {
				keyCode = arrowType;
			}
		}
		switch (keyCode) {
		case GWTKeycodes.KEY_BACKSPACE:
			onBackSpace();
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
		int caretPos = getCursorPos();
		if (caretPos > 0) {
			setCursorPos(caretPos - 1);
		}
	}

	private void onArrowRight() {
		int caretPos = getCursorPos();
		if (caretPos < getText().length()) {
			setCursorPos(caretPos + 1);
		}
	}

	private void onBackSpace() {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		if (end - start < 1) {
			end = getCursorPos();
			start = end - 1;
		}
		if (start >= 0) {
			int cpos = removeDummyCursor();
			setText(start, end, "");
			addDummyCursor(cpos - 1);
		}
	}

	@Override
	public String getValue() {
		return getText();
	}
}
