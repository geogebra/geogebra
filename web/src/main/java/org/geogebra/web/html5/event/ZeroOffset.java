package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.euclidian.event.PointerEventType;

public class ZeroOffset implements HasOffsets {
	/** singleton instance */
	public static final ZeroOffset INSTANCE = new ZeroOffset();
	private LinkedList<PointerEvent> mousePool = new LinkedList<>();
	private LinkedList<PointerEvent> touchPool = new LinkedList<>();

	@Override
	public LinkedList<PointerEvent> getMouseEventPool() {
		return mousePool;
	}

	@Override
	public LinkedList<PointerEvent> getTouchEventPool() {
		return touchPool;
	}

	@Override
	public int mouseEventX(int clientX) {
		return clientX;
	}

	@Override
	public int mouseEventY(int clientY) {
		return clientY;
	}

	@Override
	public int getEvID() {
		return 0;
	}

	@Override
	public int touchEventX(int clientX) {
		return clientX;
	}

	@Override
	public int touchEventY(int clientY) {
		return clientY;
	}

	@Override
	public PointerEventType getDefaultEventType() {
		return PointerEventType.MOUSE;
	}

}
