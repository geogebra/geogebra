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
 * Input change listener used internally in editor framework.
 */
public interface MathFieldInternalListener {

	/**
	 * Called when the input changes in the math field internal
	 * @param mathFieldInternal internal
	 */
	void inputChanged(MathFieldInternal mathFieldInternal);
}
