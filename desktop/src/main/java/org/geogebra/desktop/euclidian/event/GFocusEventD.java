package org.geogebra.desktop.euclidian.event;

import java.awt.event.FocusEvent;
import java.util.LinkedList;

import org.geogebra.common.euclidian.event.GFocusEvent;
import org.geogebra.common.util.debug.Log;

public class GFocusEventD extends GFocusEvent {

	private static final LinkedList<GFocusEventD> pool = new LinkedList<GFocusEventD>();

	private GFocusEventD(FocusEvent e) {
		Log.debug("possible missing release()");
	}

	public static GFocusEventD wrapEvent(FocusEvent e) {
		if (!pool.isEmpty()) {
			GFocusEventD wrap = pool.getLast();
			pool.removeLast();
			return wrap;
		}
		return new GFocusEventD(e);
	}

	public void release() {
		GFocusEventD.pool.add(this);
	}
}
