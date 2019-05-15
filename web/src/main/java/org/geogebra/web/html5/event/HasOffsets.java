package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.euclidian.event.PointerEventType;

public interface HasOffsets {
	LinkedList<PointerEvent> getMouseEventPool();

	LinkedList<PointerEvent> getTouchEventPool();

	int mouseEventX(int clientX);

	int mouseEventY(int clientY);

	int touchEventX(int clientX);

	int touchEventY(int clientY);

	int getEvID();

	PointerEventType getDefaultEventType();
}
