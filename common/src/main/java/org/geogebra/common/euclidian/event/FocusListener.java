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

	protected void wrapFocusGained() {
		if (listenerClass instanceof FocusListenerDelegate) {
			((FocusListenerDelegate) listenerClass).focusGained();
		} else {
			Log.debug("other type");
		}
	}

	protected void wrapFocusLost() {
		if (listenerClass instanceof FocusListenerDelegate) {
			((FocusListenerDelegate) listenerClass).focusLost();
		} else {
			Log.debug("other type");
		}
	}

	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
