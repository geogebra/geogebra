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
		org.geogebra.desktop.euclidian.event.FocusEvent event = org.geogebra.desktop.euclidian.event.FocusEvent
				.wrapEvent(e);
		wrapFocusGained(event);
		event.release();
	}

	public void focusLost(FocusEvent e) {
		org.geogebra.desktop.euclidian.event.FocusEvent event = org.geogebra.desktop.euclidian.event.FocusEvent
				.wrapEvent(e);
		wrapFocusLost(event);
		event.release();
	}

}
