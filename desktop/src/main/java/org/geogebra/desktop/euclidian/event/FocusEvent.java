package org.geogebra.desktop.euclidian.event;

import java.util.LinkedList;

import org.geogebra.common.main.App;

public class FocusEvent extends org.geogebra.common.euclidian.event.FocusEvent {

	public static LinkedList<FocusEvent> pool = new LinkedList<FocusEvent>();
	private java.awt.event.FocusEvent event;

	private FocusEvent(java.awt.event.FocusEvent e) {
		App.debug("possible missing release()");
		this.event = e;
	}

	public static org.geogebra.desktop.euclidian.event.FocusEvent wrapEvent(
			java.awt.event.FocusEvent e) {
		if (!pool.isEmpty()) {
			FocusEvent wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new FocusEvent(e);
	}

	public void release() {
		FocusEvent.pool.add(this);
	}
}
