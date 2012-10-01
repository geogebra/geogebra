package geogebra.common.plugin.jython;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.script.CompiledPythonScript;

public abstract class PythonBridge {

	public abstract void click(GeoElement geo1);
	
	public abstract void setEventHandler(GeoElement geo, String evtType, String code);
	
	public abstract void removeEventHandler(GeoElement geo, String evtType);

	public abstract void eval(String script);
	
	public abstract CompiledPythonScript compileEventScript(String text);
	
}
