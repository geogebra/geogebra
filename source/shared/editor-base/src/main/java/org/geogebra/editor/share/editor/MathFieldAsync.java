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
 * Interface for Web implementation of MathField where requestViewFocus is
 * asynchronous
 */
public interface MathFieldAsync {

	/**
	 * Focus and run callback
	 * 
	 * @param callback
	 *            callback
	 */
	void requestViewFocus(Runnable callback);

}
