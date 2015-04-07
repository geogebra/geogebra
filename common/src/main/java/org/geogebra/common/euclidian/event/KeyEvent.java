package org.geogebra.common.euclidian.event;

/**
 * Common class for key events.
 */
public abstract class KeyEvent {

	/**
	 * @return true iff enter was pressed.
	 */
	public abstract boolean isEnterKey();

	/**
	 * @return true iff Ctrl was pressed.
	 */
	public abstract boolean isCtrlDown();

	/**
	 * @return true iff Alt was pressed.
	 */
	public abstract boolean isAltDown();

	/**
	 * @return the char code of the pressed key.
	 */
	public abstract char getCharCode();

	/**
	 * Prevents the wrapped native event's default action.
	 */
	public abstract void preventDefault();

}
