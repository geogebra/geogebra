package org.geogebra.web.full.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

import com.google.gwt.event.dom.client.HumanInputEvent;

/**
 * Common interface for plaintext or LaTeX based CAS editors
 */
public interface CASEditorW extends CASTableCellEditor, AutoCompleteW {

	/**
	 * Clear the editor
	 */
	public void resetInput();

	/**
	 * (De)activate autocomplete for comment cells / normal cells
	 * 
	 * @param b
	 *            whether to activate autocomplete
	 */
	public void setAutocomplete(boolean b);

	/**
	 * Editor callback to update text/latex
	 * 
	 * @param plain
	 *            input in GGB CAS syntax
	 * @param latex
	 *            input in LaTeX syntax
	 */
	public void setLaTeX(String plain, String latex);

	/**
	 * @return LaTeX content
	 */
	public String getLaTeX();

	/**
	 * Inserts input string, doesn't treat it like autocomplete (no highlight)
	 * 
	 * @param input
	 *            input stru=ing
	 */
	public void insertInput(String input);

	/**
	 * @param event
	 *            mouse / touch event that moved the caret
	 */
	public void adjustCaret(HumanInputEvent<?> event);

	/**
	 * @param asText
	 *            whether this should be plain text mode
	 */
	public void setEditAsText(boolean asText);
}
