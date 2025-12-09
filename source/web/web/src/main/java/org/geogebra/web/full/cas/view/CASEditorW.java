/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
