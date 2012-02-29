package geogebra.web.awt.event;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.KeyPressEvent;

import geogebra.common.main.AbstractApplication;

public class KeyEvent extends geogebra.common.awt.event.KeyEvent{

	public static LinkedList<KeyEvent> pool = new LinkedList<KeyEvent>();
	private com.google.gwt.event.dom.client.KeyPressEvent event;

	private KeyEvent(com.google.gwt.event.dom.client.KeyPressEvent e) {
		AbstractApplication.debug("possible missing release()");
		this.event = e;
	}
	
	public static KeyEvent wrapEvent(com.google.gwt.event.dom.client.KeyPressEvent e) {
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
		//TODO This function doesn't give back all character properly,
		//but currently (2011.febr.29) this function used by
		//DrawTextField.keyReleased(KeyEvent) only, which wants to know
		//if we typed a '\n' or not.
	    AbstractApplication.debug("implementation needed - just finishing"); // TODO
	    if (event.getUnicodeCharCode() == 13) return '\n';
	    return event.getCharCode();
    }

}
