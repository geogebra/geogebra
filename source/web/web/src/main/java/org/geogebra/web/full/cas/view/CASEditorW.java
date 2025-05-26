package org.geogebra.web.full.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.gwtproject.event.dom.client.HumanInputEvent;
import org.gwtproject.user.client.ui.IsWidget;

/**
 * Common interface for plaintext or LaTeX based CAS editors
 */
public interface CASEditorW extends CASTableCellEditor, AutoCompleteW, IsWidget {

	/**
	 * Clear the editor
	 */
	void resetInput();

	/**
	 * (De)activate autocomplete for comment cells / normal cells
	 * 
	 * @param b
	 *            whether to activate autocomplete
	 */
	void setAutocomplete(boolean b);

	/**
	 * @return LaTeX content
	 */
	String getLaTeX();

	/**
	 * Inserts input string, doesn't treat it like autocomplete (no highlight)
	 * 
	 * @param input
	 *            input stru=ing
	 */
	void insertInput(String input);

	/**
	 * @param event
	 *            mouse / touch event that moved the caret
	 */
	void adjustCaret(HumanInputEvent<?> event);

	/**
	 * @param asText
	 *            whether this should be plain text mode
	 */
	void setEditAsText(boolean asText);
}
