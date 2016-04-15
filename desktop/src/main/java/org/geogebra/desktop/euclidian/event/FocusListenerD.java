package org.geogebra.desktop.euclidian.event;

import java.awt.event.FocusEvent;

/**
 * @author judit
 *
 */
public class FocusListenerD extends
		org.geogebra.common.euclidian.event.FocusListener implements
		java.awt.event.FocusListener {

	public FocusListenerD(Object listener) {
		setListenerClass(listener);
	}

	public void focusGained(FocusEvent e) {
		org.geogebra.desktop.euclidian.event.GFocusEventD event = org.geogebra.desktop.euclidian.event.GFocusEventD
				.wrapEvent(e);
		wrapFocusGained(event);
		event.release();
	}

	public void focusLost(FocusEvent e) {
		org.geogebra.desktop.euclidian.event.GFocusEventD event = org.geogebra.desktop.euclidian.event.GFocusEventD
				.wrapEvent(e);
		wrapFocusLost(event);
		event.release();
	}

}
