package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.InlineLabel;

public class CursorOverlay extends FlowPanel {

	private String text = "";
	int cursorPos = -1;

	public CursorOverlay() {
		setStyleName("cursorOverlay");
	}

	/**
	 * Hide keyboard and reset the keyaord field
	 * @param app application
	 */
	public static void hideKeyboard(AppW app) {
		if (CancelEventTimer.cancelKeyboardHide()) {
			return;
		}
		KeyboardManagerInterface kbManager = app.getKeyboardManager();
		if (app.hasPopup() && kbManager != null) {
			kbManager.setOnScreenKeyboardTextField(null);
			return;
		}
		app.hideKeyboard();
	}

	/**
	 * @param cursorPos cursor position
	 * @param text textfield content
	 */
	public void update(int cursorPos, String text) {
		if (text.equals(this.text) && cursorPos == this.cursorPos) {
			return;
		}
		this.text = text;
		this.cursorPos = cursorPos;
		CursorOverlay dummyCursor = this;
		dummyCursor.clear();
		InlineLabel prefix = new InlineLabel(text.substring(0, cursorPos));
		dummyCursor.add(prefix);
		InlineLabel w = new InlineLabel("|");
		w.setStyleName("virtualCursor");
		dummyCursor.add(w);
		dummyCursor.add(new InlineLabel(text.substring(cursorPos)));
		int offset = prefix.getOffsetWidth() - dummyCursor.getElement().getScrollLeft();
		int scrollPadding = 10;
		if (offset < 0) {
			dummyCursor.getElement().setScrollLeft(prefix.getOffsetWidth() - scrollPadding);
		} else if (offset > dummyCursor.getOffsetWidth() - scrollPadding) {
			dummyCursor.getElement().setScrollLeft(prefix.getOffsetWidth()
					- dummyCursor.getOffsetWidth() + scrollPadding);
		}
	}
}
