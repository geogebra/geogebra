package geogebra3D;

import geogebra.main.Application;
import geogebra.main.GlobalKeyDispatcherD;


public class GlobalKeyDispatcher3D extends GlobalKeyDispatcherD {

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
