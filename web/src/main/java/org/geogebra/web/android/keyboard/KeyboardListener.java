package org.geogebra.web.android.keyboard;

import org.geogebra.common.main.Localization;
import org.geogebra.web.web.util.keyboardBase.KeyBoardProcessable;

public class KeyboardListener implements KeyBoardProcessable {

	public void setFocus(boolean focus) {
		// ignored
	}

	public void onEnter() {
		onEnterNative();
	}

	private native void onEnterNative() /*-{
		$wnd.androidKeyboard.onEnter();
	}-*/;

	public void onBackSpace() {
		onBackSpaceNative();
	}

	private native void onBackSpaceNative() /*-{
		$wnd.androidKeyboard.onBackSpace();
	}-*/;

	public void onArrow(ArrowType type) {
		onArrowNative(type.ordinal());
	}

	private native void onArrowNative(int arrow) /*-{
		$wnd.androidKeyboard.onArrow(arrow);
	}-*/;

	public void insertString(String text) {
		insertStringNative(text);
	}

	private native void insertStringNative(String text) /*-{
		$wnd.androidKeyboard.insertString(text);
	}-*/;

	public void scrollCursorIntoView() {
		scrollCursorIntoViewNative();
	}

	private native void scrollCursorIntoViewNative() /*-{
		$wnd.androidKeyboard.scrollCursorIntoView();
	}-*/;

	public boolean resetAfterEnter() {
		return false;
	}

	public void updateForNewLanguage(Localization localization) {
		// ignored
	}

}
