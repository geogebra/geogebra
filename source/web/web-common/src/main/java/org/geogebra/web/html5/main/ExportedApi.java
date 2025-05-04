package org.geogebra.web.html5.main;

import jsinterop.base.JsPropertyMap;

/**
 * Exported API object.
 */
public interface ExportedApi extends JsPropertyMap<Object> {

	/**
	 * @param ggbAPI the applet API
	 */
	void setGgbAPI(GgbAPIW ggbAPI);

	/**
	 * @param scriptManager the script manager
	 */
	void setScriptManager(ScriptManagerW scriptManager);
}
