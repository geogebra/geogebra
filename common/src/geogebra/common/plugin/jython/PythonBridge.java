package geogebra.common.plugin.jython;

import geogebra.common.kernel.geos.GeoElement;

public abstract class PythonBridge {

	public abstract void click(GeoElement geo1);
	
	public abstract void setEventListener(GeoElement geo, String evtType, String code);

	public abstract void eval(String script);
}
