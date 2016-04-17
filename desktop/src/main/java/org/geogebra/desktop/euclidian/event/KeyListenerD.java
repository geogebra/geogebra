package org.geogebra.desktop.euclidian.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.geogebra.common.euclidian.event.KeyHandler;

public class KeyListenerD implements KeyListener {

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
		KeyEventD event = KeyEventD.wrapEvent(e);
		handler.keyReleased(event);
		event.release();

	}

}
