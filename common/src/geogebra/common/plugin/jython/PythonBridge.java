package geogebra.common.plugin.jython;

import geogebra.common.kernel.geos.GeoElement;

public abstract class PythonBridge {

	public abstract void click(GeoElement geo1);
	
	public abstract void setEventHandler(GeoElement geo, String evtType, String code);
	
	public abstract void removeEventHandler(GeoElement geo, String evtType);

	public abstract void eval(String script);
	
}
