//------------------------------------------------------------------------------
// Copyright (c) 1999-2001 Matt Welsh. 
//------------------------------------------------------------------------------

package org.geogebra.common.gui.inputfield;

/**
 * Defines the API that autocomplete components must implement
 */
public interface AutoComplete {

	/**
	 * Sets whether the component is currently performing autocomplete lookups
	 * as keystrokes are performed.
	 *
	 * @param val
	 *            True or false.
	 */
	void setAutoComplete(boolean val);

	/**
	 * Gets whether the component is currently performing autocomplete lookups
	 * as keystrokes are performed.
	 *
	 * @return True or false.
	 */
	boolean getAutoComplete();
}
