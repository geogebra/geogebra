package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;

public interface IsEuclidianController extends LongTouchHandler {

	void setExternalHandling(boolean b);

	void twoTouchStart(double x1, double y1, double x2, double y2);

	void setDefaultEventType(PointerEventType pointerEventType,
			boolean pointerDown);

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
