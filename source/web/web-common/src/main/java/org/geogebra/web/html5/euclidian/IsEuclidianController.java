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

	void setExternalHandling(boolean b);

	/**
	 * Handle touch start event for two pointers.
	 * @param x1 first posinter's x-coordinate
	 * @param y1 first pointer's y-coordinate
	 * @param x2 second posinter's x-coordinate
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

	int getEvNo();

	LongTouchManager getLongTouchManager();

	void setActualSticky(boolean b);

	boolean isDraggingBeyondThreshold();

	int getMode();

	void setMode(int i, ModeSetter ms);

	void onPointerEventStart(AbstractEvent e);

	void onPointerEventMove(PointerEvent e);

	void onPointerEventEnd(PointerEvent e);

	MouseTouchGestureControllerW getOffsets();
}
