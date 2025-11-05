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

import org.geogebra.editor.share.event.KeyEvent;

/**
 * Listener for arrow keys that were not handled by editor.
 */
@FunctionalInterface
public interface UnhandledArrowListener {
	/**
	 * Runs when arrow key is pressed and not handled by the editor.
	 * @param keyCode key code from {@link org.geogebra.editor.share.util.JavaKeyCodes}
	 * @param keyboardType keyboard type
	 */
	void onArrow(int keyCode, KeyEvent.KeyboardType keyboardType);
}
