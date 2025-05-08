package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.euclidian.event.PointerEventType;

/**
 * Set of methods related to graphics view events.
 * TODO break this down.
 */
public interface HasOffsets {
	@MissingDoc
	LinkedList<PointerEvent> getMouseEventPool();

	@MissingDoc
	LinkedList<PointerEvent> getTouchEventPool();

	@MissingDoc
	int getEvID();

	@MissingDoc
	PointerEventType getDefaultEventType();

	@MissingDoc
	double getZoomLevel();
}
