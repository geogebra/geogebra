package org.geogebra.desktop.euclidian.event;

import java.util.LinkedList;

import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;

public class KeyEvent extends org.geogebra.common.euclidian.event.KeyEvent {

	public static LinkedList<KeyEvent> pool = new LinkedList<KeyEvent>();
	private java.awt.event.KeyEvent event;

	public KeyEvent(java.awt.event.KeyEvent e) {
		App.debug("possible missing release()");
		this.event = e;
	}

	public static org.geogebra.desktop.euclidian.event.KeyEvent wrapEvent(
			java.awt.event.KeyEvent e) {
		if (!pool.isEmpty()) {
			KeyEvent wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new KeyEvent(e);
	}

	public void release() {
		KeyEvent.pool.add(this);
	}

	@Override
	public boolean isEnterKey() {
		return event.getKeyChar() == '\n';
	}

	@Override
	public boolean isCtrlDown() {
		return AppD.isControlDown(event);
	}

	@Override
	public boolean isAltDown() {
		return AppD.isAltDown(event);
	}

	@Override
	public char getCharCode() {
		return event.getKeyChar();
	}

	@Override
	public void preventDefault() {
		event.consume();
	}

}
