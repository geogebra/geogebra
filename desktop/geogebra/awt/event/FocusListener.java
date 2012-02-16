package geogebra.awt.event;

import java.awt.event.FocusEvent;

/**
 * @author judit
 *
 */
public class FocusListener extends geogebra.common.awt.event.FocusListener implements java.awt.event.FocusListener{

	public FocusListener(Object listener) {
		setListenerClass(listener);
	}


	public void focusGained(FocusEvent e) {
		geogebra.awt.event.FocusEvent event = geogebra.awt.event.FocusEvent.wrapEvent(e);
		wrapFocusGained(event);
		event.release();
	}


	public void focusLost(FocusEvent e) {
		geogebra.awt.event.FocusEvent event = geogebra.awt.event.FocusEvent.wrapEvent(e);
		wrapFocusLost(event);
		event.release();
	}

	

}
