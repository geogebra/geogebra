package geogebra.plugin.jython;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

public interface PythonScriptInterface {
	public void init(Application app);
	public void handleEvent(String eventType, GeoElement eventTarget);
	public void notifySelected(GeoElement geo, boolean addToSelection);
	public void toggleWindow();
	public boolean isWindowVisible();
}
