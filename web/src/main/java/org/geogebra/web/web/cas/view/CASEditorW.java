package org.geogebra.web.web.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

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

	@Override
	public void ensureEditing();

	/**
	 * @param explicit
	 *            whether enter was really pressed (false for just blur)
	 */
	public void onEnter(boolean explicit);
}
