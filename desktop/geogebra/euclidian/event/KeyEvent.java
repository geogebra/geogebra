package geogebra.euclidian.event;

import geogebra.common.main.App;
import geogebra.main.AppD;

import java.util.LinkedList;

public class KeyEvent extends geogebra.common.euclidian.event.KeyEvent{

	public static LinkedList<KeyEvent> pool = new LinkedList<KeyEvent>();
	private java.awt.event.KeyEvent event;

	public KeyEvent(java.awt.event.KeyEvent e) {
		App.debug("possible missing release()");
		this.event = e;
	}
	
	public static geogebra.euclidian.event.KeyEvent wrapEvent(java.awt.event.KeyEvent e) {
		if(!pool.isEmpty()){
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
	public char getKeyChar() {
		return event.getKeyChar();
	}

	@Override
	public boolean isCtrlDown() {
		return AppD.isControlDown(event);
	}

	@Override
	public boolean isAltDown() {
		return AppD.isAltDown(event);
	}

}
