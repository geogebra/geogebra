/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.editor;

/**
 * Editor features that can be enabled/disabled at run time.
 */
public class EditorFeatures {

	private boolean mixedNumbersEnabled = true;

	/**
	 * @return whether mixed numbers are enabled
	 */
	public boolean areMixedNumbersEnabled() {
		return mixedNumbersEnabled;
	}

	public void setMixedNumbersEnabled(boolean mixedNumbersEnabled) {
		this.mixedNumbersEnabled = mixedNumbersEnabled;
	}
}
