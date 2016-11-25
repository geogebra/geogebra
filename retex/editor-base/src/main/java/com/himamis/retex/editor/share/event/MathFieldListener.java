package com.himamis.retex.editor.share.event;

import com.himamis.retex.editor.share.model.MathSequence;

public interface MathFieldListener {
	public void onEnter();

	public void onKeyTyped();

	public void onCursorMove();

	public void onUpKeyPressed();
	
	public void onDownKeyPressed();
	
	public String alt(int unicodeKeyChar, boolean shift);

	public String serialize(MathSequence selectionText);
}
