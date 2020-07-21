package com.himamis.retex.editor.share.event;

import com.himamis.retex.editor.share.model.MathSequence;

public interface MathFieldListener {

	void onEnter();

	void onKeyTyped(String key);

	void onCursorMove();

	void onUpKeyPressed();
	
	void onDownKeyPressed();
	
	String serialize(MathSequence selectionText);

	void onInsertString();

	boolean onEscape();

	void onTab(boolean shiftDown);
	
}
