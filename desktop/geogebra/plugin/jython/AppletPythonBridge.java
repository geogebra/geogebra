package geogebra.plugin.jython;

import geogebra.main.AppD;

public class AppletPythonBridge extends PythonBridgeD {
	public AppletPythonBridge(AppD app) {
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
