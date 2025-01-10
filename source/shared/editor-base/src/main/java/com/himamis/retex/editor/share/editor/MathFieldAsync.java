package com.himamis.retex.editor.share.editor;

/**
 * Interface for Web implementation of MathField where requestViewFocus is
 * asynchronous
 */
public interface MathFieldAsync {

	/**
	 * Focus and run callback
	 * 
	 * @param callback
	 *            callback
	 */
	void requestViewFocus(Runnable callback);

}
