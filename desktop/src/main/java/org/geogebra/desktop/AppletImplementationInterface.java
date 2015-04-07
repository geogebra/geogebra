package org.geogebra.desktop;

import org.geogebra.common.plugin.JavaScriptAPI;

public interface AppletImplementationInterface extends JavaScriptAPI {

	public void dispose();

	public void initInBackground();
}
