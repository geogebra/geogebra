package org.geogebra.common.euclidian.event;

/**
 * Key release event handler.
 */
@FunctionalInterface
public interface KeyHandler {
	/**
	 * Handles new character
	 * 
	 * @param e
	 *            key event
	 */
	public void keyReleased(KeyEvent e);

}
