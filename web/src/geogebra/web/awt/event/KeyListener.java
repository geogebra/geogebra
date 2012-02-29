package geogebra.web.awt.event;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.event.dom.client.KeyPressEvent;

public class KeyListener extends geogebra.common.awt.event.KeyListener
        implements com.google.gwt.event.dom.client.KeyPressHandler {

	public KeyListener(Object listener) {
		setListenerClass(listener);
	}

	public void onKeyPress(KeyPressEvent e) {
		AbstractApplication.debug("implementation needed"); // TODO
															// Auto-generated
															// method stub

		geogebra.web.awt.event.KeyEvent event = geogebra.web.awt.event.KeyEvent.wrapEvent(e);
		wrapKeyReleased(event);
		event.release();
	}
}
