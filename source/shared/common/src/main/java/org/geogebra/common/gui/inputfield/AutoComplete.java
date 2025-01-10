/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.common.gui.inputfield;

/**
 * Defines the API that autocomplete components must implement
 * @author Matt Welsh
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
