package geogebra.web.main;

import geogebra.common.plugin.ScriptManagerCommon;

public class ScriptManager extends ScriptManagerCommon {

	public ScriptManager(Application app) {
	    this.app = app;
    }

	@Override
    public void callJavaScript(String jsFunction, Object[] args) {
	    app.callAppletJavaScript(jsFunction, args);	    
    }

}
