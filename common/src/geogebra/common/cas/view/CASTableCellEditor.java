package geogebra.common.cas.view;

import geogebra.common.gui.SetLabels;

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
	 * @param selStart start position of selected substring
	 */
	void setInputSelectionStart(int selStart);

	/**
	 * @param selEnd end position of selected substring
	 */
	void setInputSelectionEnd(int selEnd);

}
