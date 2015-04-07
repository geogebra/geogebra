package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.euclidian.event.PointerEventType;

public class ZeroOffset implements HasOffsets {

	public static final ZeroOffset instance = new ZeroOffset();
	private LinkedList<PointerEvent> mousePool = new LinkedList<PointerEvent>();

	public LinkedList<PointerEvent> getMouseEventPool() {
		return mousePool;
	}

	private LinkedList<PointerEvent> touchPool = new LinkedList<PointerEvent>();

	public LinkedList<PointerEvent> getTouchEventPool() {
		return touchPool;
	}

	public int mouseEventX(int clientX) {
		return clientX;
	}

	public int mouseEventY(int clientY) {
		return clientY;
	}

	public int getEvID() {
		return 0;
	}

	public int touchEventX(int clientX) {
		return clientX;
	}

	public int touchEventY(int clientY) {
		return clientY;
	}

	public PointerEventType getDefaultEventType() {
		return PointerEventType.MOUSE;
	}
}
