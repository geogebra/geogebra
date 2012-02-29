package geogebra.awt.event;

import java.awt.event.KeyEvent;

public class KeyListener extends geogebra.common.awt.event.KeyListener
implements java.awt.event.KeyListener{

	public KeyListener(Object listener) {
		setListenerClass(listener);
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		geogebra.awt.event.KeyEvent event = geogebra.awt.event.KeyEvent.wrapEvent(e);
		wrapKeyReleased(event);
		event.release();
		
	}

}
