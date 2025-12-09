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
