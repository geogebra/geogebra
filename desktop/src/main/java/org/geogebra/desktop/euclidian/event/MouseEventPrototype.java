package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

public interface MouseEventPrototype {

	/**
	 *
	 * @param event to check.
	 * @return if event is a right click.
	 */
	boolean isRightClick(MouseEvent event);

	/**
	 *
	 * @param event to check.
	 * @return if control/option key is down.
	 */
	boolean isControlDown(MouseEvent event);
}
