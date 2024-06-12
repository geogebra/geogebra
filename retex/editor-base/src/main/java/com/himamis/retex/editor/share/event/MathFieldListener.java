package com.himamis.retex.editor.share.event;

// TODO try to document this collection of random-ish methods
public interface MathFieldListener {

	void onEnter();

	void onKeyTyped(String key);

	boolean onArrowKeyPressed(int keyCode);

	default void onInsertString() {
		// rarely needed
	}

	boolean onEscape();

	boolean onTab(boolean shiftDown);
}
