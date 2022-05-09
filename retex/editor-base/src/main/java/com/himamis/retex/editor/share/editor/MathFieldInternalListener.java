package com.himamis.retex.editor.share.editor;

/**
 * Input change listener used internally in editor framework.
 */
public interface MathFieldInternalListener {

	/**
	 * Called when the input changes in the math field internal
	 * @param mathFieldInternal internal
	 */
	void inputChanged(MathFieldInternal mathFieldInternal);

	/**
	 * Called when the cursor changes its position
	 * @param mathFieldInternal internal
	 */
	void cursorChanged(MathFieldInternal mathFieldInternal);
}
