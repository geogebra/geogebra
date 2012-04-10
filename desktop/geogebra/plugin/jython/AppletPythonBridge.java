package geogebra.plugin.jython;

import geogebra.main.Application;

public class AppletPythonBridge extends PythonBridge {
	public AppletPythonBridge(Application app) {
		super(app);
	}

	@Override
	public synchronized void init(){
		//do not init
	}
	
	@Override
	public boolean isReady(){
		return false;
	}

}
