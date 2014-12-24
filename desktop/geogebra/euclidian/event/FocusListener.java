package geogebra.euclidian.event;

import java.awt.event.FocusEvent;

/**
 * @author judit
 *
 */
public class FocusListener extends
		geogebra.common.euclidian.event.FocusListener implements
		java.awt.event.FocusListener {

	public FocusListener(Object listener) {
		setListenerClass(listener);
	}

	public void focusGained(FocusEvent e) {
		geogebra.euclidian.event.FocusEvent event = geogebra.euclidian.event.FocusEvent
				.wrapEvent(e);
		wrapFocusGained(event);
		event.release();
	}

	public void focusLost(FocusEvent e) {
		geogebra.euclidian.event.FocusEvent event = geogebra.euclidian.event.FocusEvent
				.wrapEvent(e);
		wrapFocusLost(event);
		event.release();
	}

}
