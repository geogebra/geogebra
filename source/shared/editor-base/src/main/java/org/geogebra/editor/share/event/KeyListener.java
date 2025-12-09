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
