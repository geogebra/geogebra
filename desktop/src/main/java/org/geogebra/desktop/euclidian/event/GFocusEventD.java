package org.geogebra.desktop.euclidian.event;

import java.awt.event.FocusEvent;
import java.util.LinkedList;

import org.geogebra.common.euclidian.event.GFocusEvent;
import org.geogebra.common.main.App;

public class GFocusEventD extends GFocusEvent {

	public static LinkedList<GFocusEventD> pool = new LinkedList<GFocusEventD>();
	private java.awt.event.FocusEvent event;

	private GFocusEventD(FocusEvent e) {
		App.debug("possible missing release()");
		this.event = e;
	}

	public static GFocusEventD wrapEvent(FocusEvent e) {
		if (!pool.isEmpty()) {
			GFocusEventD wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new GFocusEventD(e);
	}

	public void release() {
		GFocusEventD.pool.add(this);
	}
}
