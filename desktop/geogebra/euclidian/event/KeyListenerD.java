package geogebra.euclidian.event;

import geogebra.common.euclidian.event.KeyHandler;

import java.awt.event.KeyEvent;

public class KeyListenerD 
implements java.awt.event.KeyListener{

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
		geogebra.euclidian.event.KeyEvent event = geogebra.euclidian.event.KeyEvent.wrapEvent(e);
		handler.keyReleased(event);
		event.release();
		
	}

}
