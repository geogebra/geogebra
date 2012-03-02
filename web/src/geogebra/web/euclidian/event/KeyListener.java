package geogebra.web.euclidian.event;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.event.dom.client.KeyPressEvent;

public class KeyListener extends geogebra.common.euclidian.event.KeyListener
        implements com.google.gwt.event.dom.client.KeyPressHandler {

	public KeyListener(Object listener) {
		setListenerClass(listener);
	}

	public void onKeyPress(KeyPressEvent e) {
		AbstractApplication.debug("implementation needed"); // TODO
															// Auto-generated
															// method stub

		geogebra.web.euclidian.event.KeyEvent event = geogebra.web.euclidian.event.KeyEvent.wrapEvent(e);
		wrapKeyReleased(event);
		event.release();
	}
}
