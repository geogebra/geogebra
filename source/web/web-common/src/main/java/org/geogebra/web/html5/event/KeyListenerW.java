package org.geogebra.web.html5.event;

import org.geogebra.common.euclidian.event.KeyHandler;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyPressHandler;

public class KeyListenerW implements KeyPressHandler {
	private KeyHandler handler;

	public KeyListenerW(KeyHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onKeyPress(KeyPressEvent e) {

		KeyEventW event = KeyEventW.wrapEvent(e);
		handler.keyReleased(event);
		event.release();
	}
}
