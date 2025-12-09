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
