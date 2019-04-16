package org.geogebra.common.cas.view;

import org.geogebra.common.gui.SetLabels;

/**
 * Interface for CAS cell editor
 */
public interface CASTableCellEditor extends SetLabels {
	/**
	 * @return end position of selected substring
	 */
	int getInputSelectionEnd();

	/**
	 * @return start position of selected substring
	 */
	int getInputSelectionStart();

	/**
	 * @return selected text
	 */
	String getInputSelectedText();

	/**
	 * @return content of input area
	 */
	String getInput();

	/**
	 * @param selStart
	 *            start position of selected substring
	 */
	void setInputSelectionStart(int selStart);

	/**
	 * @param selEnd
	 *            end position of selected substring
	 */
	void setInputSelectionEnd(int selEnd);

	/**
	 * Clears input area
	 */
	void clearInputText();

	/**
	 * @param string
	 *            new content of the editor
	 */
	void setInput(String string);

	/**
	 * Start editing if it is not active
	 */
	void ensureEditing();

	/**
	 * @param explicit
	 *            whether this came from keyboard (false: triggered by blur)
	 */
	void onEnter(boolean explicit);

	/**
	 * @return whether editr is currently focused
	 */
	boolean hasFocus();

	/**
	 * @param ratio
	 *            pixel ratio
	 */
	void setPixelRatio(double ratio);
}
