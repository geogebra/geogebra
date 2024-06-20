package com.himamis.retex.editor.share.event;

// TODO Try to document this collection of random-ish methods (what is their meaning?
//  when are they called?; what does the return value mean?)
//  Also,
//  - can we align the return types?
//  - try to find a better (more descriptive) name for onInsertString()
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
