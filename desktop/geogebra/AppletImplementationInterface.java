package geogebra;

import geogebra.common.plugin.JavaScriptAPI;

public interface AppletImplementationInterface extends JavaScriptAPI {

	public void dispose();

	public void initInBackground();
}
