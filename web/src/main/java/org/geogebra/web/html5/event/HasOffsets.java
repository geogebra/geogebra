package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.euclidian.event.PointerEventType;

public interface HasOffsets {
	public LinkedList<PointerEvent> getMouseEventPool();

	public LinkedList<PointerEvent> getTouchEventPool();

	public int mouseEventX(int clientX);

	public int mouseEventY(int clientY);

	public int touchEventX(int clientX);

	public int touchEventY(int clientY);

	public int getEvID();

	public PointerEventType getDefaultEventType();
}
