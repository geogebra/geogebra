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
 * Pointer event listener.
 */
public interface ClickListener {

	/**
	 * Handle pointer down event.
	 * @param x x-coordinate in DIP
	 * @param y y-coordinate in DIP
	 */
	void onPointerDown(int x, int y);

	/**
	 * Handle pointer up event.
	 * @param x x-coordinate in DIP
	 * @param y y-coordinate in DIP
	 */
	void onPointerUp(int x, int y);

	/**
	 * Handle pointer move event.
	 * @param x x-coordinate in DIP
	 * @param y y-coordinate in DIP
	 */
	void onPointerMove(int x, int y);

	/**
	 * Handle long-press event.
	 * @param x x-coordinate in DIP
	 * @param y y-coordinate in DIP
	 */
	void onLongPress(int x, int y);

	/**
	 * Handle scroll.
	 * @param dx x distance from current call to last call
	 * @param dy y distance from current call to last call
	 */
	void onScroll(int dx, int dy);

}
