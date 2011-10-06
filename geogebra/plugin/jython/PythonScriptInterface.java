package geogebra.plugin.jython;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

public interface PythonScriptInterface {
	public void init(Application app);
	public void handleEvent(String eventType, GeoElement eventTarget);
}
