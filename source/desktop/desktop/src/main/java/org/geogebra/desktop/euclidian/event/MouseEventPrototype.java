package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.common.euclidian.event.AbstractEvent;

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

	/**
	 *
	 * @param event to check.
	 * @return if meta key is down.
	 */
	boolean isMetaDown(MouseEvent event);

	/**
	 *
	 * @param event to check.
	 * @return if key for multiple select is down.
	 */
	boolean hasMultipleSelectModifier(AbstractEvent event);
}
