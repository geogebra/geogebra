package geogebra.plugin.jython;

import geogebra.main.Application;

public class AppletPythonBridge extends PythonBridge {
	@Override
	public synchronized void init(){
		//do not init
	}
	
	@Override
	public boolean isReady(){
		return false;
	}

}
