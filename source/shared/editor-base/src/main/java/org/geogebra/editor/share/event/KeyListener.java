/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.event;

/**
 * Key event listener.
 */
public interface KeyListener {

	/**
	 * Called when key is pressed.
	 * @param keyEvent key event
	 * @return whether it was handled by this listener
	 */
	boolean onKeyPressed(KeyEvent keyEvent);

	/**
	 * Called when key is released.
	 * @param keyEvent key event
	 * @return whether it was handled by this listener
	 */
	boolean onKeyReleased(KeyEvent keyEvent);

	/**
	 * Called when key is typed.
	 * @param keyEvent key event
	 * @return whether it was handled by this listener
	 */
	boolean onKeyTyped(KeyEvent keyEvent);
}
