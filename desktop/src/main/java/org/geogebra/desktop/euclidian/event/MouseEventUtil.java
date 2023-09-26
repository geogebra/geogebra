package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.desktop.main.AppD;

/**
 * Utility class to handle events in a platform specific way
 */
public class MouseEventUtil {
	private final static MouseEventPrototype prototype = AppD.MAC_OS
			? new MacOSMouseEventPrototype()
			: new DefaultMouseEventPrototype();


	/**
	 *
	 * @param event to check.
	 * @return if event is a right click.
	 */
	public static boolean isRightClick(MouseEvent event) {
		return prototype.isRightClick(event);
	}

	/**
	 *
	 * @param event to check.
	 * @return if control/option key is down.
	 */
	public static boolean isControlDown(MouseEvent event) {
		return prototype.isControlDown(event);
	}

	/**
	 *
	 * @param event to check.
	 * @return if meta key is down.
	 */public static boolean isMetaDown(MouseEvent event) {
		return prototype.isMetaDown(event);
	}

	/**
	 *
	 * @param event to check.
	 * @return if key for multiple select is down.
	 */
	public static boolean hasMultipleSelectModifier(MouseEventD event) {
		return prototype.hasMultipleSelectModifier(event);
	}
}