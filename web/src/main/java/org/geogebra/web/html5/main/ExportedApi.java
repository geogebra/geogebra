package org.geogebra.web.html5.main;

import jsinterop.base.JsPropertyMap;

public interface ExportedApi extends JsPropertyMap<Object> {

	void setGgbAPI(GgbAPIW ggbAPI);

	void setScriptManager(ScriptManagerW scriptManager);
}
