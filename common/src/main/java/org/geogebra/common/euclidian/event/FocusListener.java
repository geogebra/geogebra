package org.geogebra.common.euclidian.event;

import org.geogebra.common.util.debug.Log;

public class FocusListener {

	private Object listenerClass;

	public FocusListener() {
		this(null);
	}

	public FocusListener(Object listener) {
		listenerClass = listener;
	}

	protected void wrapFocusGained(GFocusEvent event) {
		if (listenerClass instanceof FocusListenerDelegate) {
			((FocusListenerDelegate) listenerClass).focusGained(event);
		} else {
			Log.debug("other type");
		}
	}

	protected void wrapFocusGained() {
		wrapFocusGained(null);
	}

	protected void wrapFocusLost(GFocusEvent event) {
		if (listenerClass instanceof FocusListenerDelegate) {
			((FocusListenerDelegate) listenerClass).focusLost(event);
		} else {
			Log.debug("other type");
		}
	}

	protected void wrapFocusLost() {
		wrapFocusLost(null);
	}

	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
