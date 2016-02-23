package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextArea;

// Class for future syntax highlighting, line numbering and so on.
public class ScriptArea extends TextArea implements KeyUpHandler {

	private AppW app;

	public ScriptArea(AppW app) {
		setStyleName("scriptArea");
		this.app = app;
		addKeyUpHandler(this);
	}

	public ScriptArea(Element element) {
		super(element);
	}

	public void onKeyUp(KeyUpEvent e) {
		if (e.isAltKeyDown()) {

			String s = app.getGlobalKeyDispatcher().getAltSymbols(e.getNativeKeyCode(),
					e.isShiftKeyDown());

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

		setCursorPos(newPos);
	}

	private int getSelectionEnd() {
		return getSelectionStart() + getSelectionLength();
	}

	private int getSelectionStart() {
		return getText().indexOf(getSelectedText());
	}

	public void insertString(String text) {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		setText(start, end, text);
		// if (insertHandler != null) {
		// insertHandler.onInsert(text);
		// }
	}

}
