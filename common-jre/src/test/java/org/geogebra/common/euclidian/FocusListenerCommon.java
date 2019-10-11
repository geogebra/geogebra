package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.event.FocusListener;

public class FocusListenerCommon extends FocusListener {

	public FocusListenerCommon(Object listener) {
		super(listener);
	}

	public void focusLost() {
		wrapFocusLost();
	}

}
