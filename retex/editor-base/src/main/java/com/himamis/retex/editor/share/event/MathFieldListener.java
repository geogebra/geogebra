package com.himamis.retex.editor.share.event;

public interface MathFieldListener {

	void onEnter();

	void onKeyTyped(String key);

	boolean onArrowKeyPressed(int keyCode);

	void onInsertString();

	boolean onEscape();

	void onTab(boolean shiftDown);
}
