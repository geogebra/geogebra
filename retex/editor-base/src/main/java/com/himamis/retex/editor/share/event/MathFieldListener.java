package com.himamis.retex.editor.share.event;

public interface MathFieldListener {

	void onEnter();

	void onKeyTyped(String key);

	void onCursorMove();

	void onArrowKeyPressed(int keyCode);

	void onInsertString();

	boolean onEscape();

	void onTab(boolean shiftDown);
}
