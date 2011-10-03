package geogebra3D;

import java.awt.event.KeyEvent;

import geogebra.main.Application;
import geogebra.main.GlobalKeyDispatcher;


public class GlobalKeyDispatcher3D extends GlobalKeyDispatcher {

	public GlobalKeyDispatcher3D(Application app) {
		super(app);
	}

	/*
	protected boolean handleKeyPressed(KeyEvent event) {	

		if (event.getKeyCode()==KeyEvent.VK_W) {					
			((Application3D) app).toggleWireframe();
			return true;
		}

		return super.handleKeyPressed(event);
	}
*/
}
