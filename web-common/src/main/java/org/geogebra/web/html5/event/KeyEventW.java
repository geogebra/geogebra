package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.event.dom.client.KeyPressEvent;

public final class KeyEventW
		extends org.geogebra.common.euclidian.event.KeyEvent {

	private static final LinkedList<KeyEventW> POOL = new LinkedList<>();
	private KeyPressEvent event;

	private KeyEventW(KeyPressEvent e) {
		Log.debug("possible missing release()");
		this.event = e;
	}

	/**
	 * @param e
	 *            GWT event
	 * @return wrapped event
	 */
	public static KeyEventW wrapEvent(KeyPressEvent e) {
		if (!POOL.isEmpty()) {
			KeyEventW wrap = POOL.getLast();
			wrap.event = e;
			POOL.removeLast();
			return wrap;
		}
		return new KeyEventW(e);
	}

	/**
	 * Make the event reusable.
	 */
	public void release() {
		KeyEventW.POOL.add(this);
	}

	@Override
	public boolean isEnterKey() {
		return event.getNativeEvent().getKeyCode() == 13
				|| event.getNativeEvent().getKeyCode() == 10
				|| (event.getNativeEvent().getKeyCode() == 0 && event
						.getNativeEvent().getCharCode() == 13);
	}

	@Override
	public boolean isCtrlDown() {
		return event.isControlKeyDown();
	}

	@Override
	public boolean isAltDown() {
		return event.isAltKeyDown();
	}

	@Override
	public char getCharCode() {
		return event.getCharCode();
	}

	@Override
	public void preventDefault() {
		event.preventDefault();
	}

}
