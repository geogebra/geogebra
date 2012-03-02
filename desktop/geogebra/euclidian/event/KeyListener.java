package geogebra.euclidian.event;

import java.awt.event.KeyEvent;

public class KeyListener extends geogebra.common.euclidian.event.KeyListener
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
		geogebra.euclidian.event.KeyEvent event = geogebra.euclidian.event.KeyEvent.wrapEvent(e);
		wrapKeyReleased(event);
		event.release();
		
	}

}
