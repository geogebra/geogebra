package org.geogebra.desktop.euclidian.event;

import java.awt.event.KeyEvent;

import org.geogebra.common.euclidian.event.KeyHandler;

public class KeyListenerD implements java.awt.event.KeyListener {

	private KeyHandler handler;

	public KeyListenerD(KeyHandler handler) {
		this.handler = handler;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		org.geogebra.desktop.euclidian.event.KeyEvent event = org.geogebra.desktop.euclidian.event.KeyEvent
				.wrapEvent(e);
		handler.keyReleased(event);
		event.release();

	}

}
