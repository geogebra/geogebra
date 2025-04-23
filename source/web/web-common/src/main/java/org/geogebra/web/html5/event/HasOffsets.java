package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.euclidian.event.PointerEventType;

/**
 * Set of methods related to graphics view events.
 * TODO break this down.
 */
public interface HasOffsets {
	LinkedList<PointerEvent> getMouseEventPool();

	LinkedList<PointerEvent> getTouchEventPool();

	int getEvID();

	PointerEventType getDefaultEventType();

	double getZoomLevel();
}
