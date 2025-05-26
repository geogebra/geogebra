package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;

/**
 * Common interface for 2D and 3D Euclidian controller in Web.
 */
public interface IsEuclidianController extends LongTouchHandler {

	/**
	 * Handle touch start event for two pointers.
	 * @param x1 first pointer's x-coordinate
	 * @param y1 first pointer's y-coordinate
	 * @param x2 second pointer's x-coordinate
	 * @param y2 second pointer's y-coordinate
	 */
	void twoTouchStart(double x1, double y1, double x2, double y2);

	/**
	 * Change the default pointer event type.
	 * @param pointerEventType pointer event type
	 * @param pointerDown whether this was triggered by pointer down event
	 */
	void setDefaultEventType(PointerEventType pointerEventType,
			boolean pointerDown);

	/**
	 * Handle touch move event for two pointers.
	 * @param x1 first posinter's x-coordinate
	 * @param y1 first pointer's y-coordinate
	 * @param x2 second posinter's x-coordinate
	 * @param y2 second pointer's y-coordinate
	 */
	void twoTouchMove(double x1, double y1, double x2, double y2);

	/**
	 * @return EV number (1 for EV1, 2 for EV2)
	 */
	int getEvNo();

	/**
	 * @return handler for long touch events
	 */
	LongTouchManager getLongTouchManager();

	/**
	 * TODO remove
	 * @param b ignored
	 */
	void setActualSticky(boolean b);

	/**
	 * @return whether current pointer movement exceeded the threshold for dragging
	 */
	boolean isDraggingBeyondThreshold();

	/**
	 * @return app mode
	 */
	int getMode();

	/**
	 * Notify controller about mode change.
	 * @param mode app mode
	 * @param ms mode setter
	 */
	void setMode(int mode, ModeSetter ms);

	/**
	 * Handle pointer down event.
	 * @param e pointer down event
	 */
	void onPointerEventStart(AbstractEvent e);

	/**
	 * Handle pointer move event.
	 * @param e pointer move event
	 */
	void onPointerEventMove(PointerEvent e);

	/**
	 * Handle pointer up event.
	 * @param e pointer up event
	 */
	void onPointerEventEnd(PointerEvent e);

	/**
	 * @return mouse, touch and gesture controller
	 */
	MouseTouchGestureControllerW getOffsets();
}
