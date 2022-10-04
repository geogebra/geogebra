package org.geogebra.desktop.euclidian.event;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

import org.geogebra.desktop.main.AppD;

public class KeyEventD extends org.geogebra.common.euclidian.event.KeyEvent {

	private static final LinkedList<KeyEventD> pool = new LinkedList<>();
	private KeyEvent event;

	public KeyEventD(KeyEvent e) {
		this.event = e;
	}

	/**
	 * @param e native event
	 * @return wrapped event
	 */
	public static KeyEventD wrapEvent(KeyEvent e) {
		if (!pool.isEmpty()) {
			KeyEventD wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new KeyEventD(e);
	}

	public void release() {
		KeyEventD.pool.add(this);
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
