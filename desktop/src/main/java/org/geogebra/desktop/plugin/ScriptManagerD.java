package org.geogebra.desktop.plugin;

import java.util.HashMap;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.desktop.main.AppD;
import org.mozilla.javascript.Scriptable;

//import org.concord.framework.data.stream.DataListener;
//import org.concord.framework.data.stream.DataStreamEvent;
//import org.concord.sensor.SensorDataProducer;
//import org.mozilla.javascript.Context;
//import org.mozilla.javascript.Scriptable;

public class ScriptManagerD extends ScriptManager {

	// library of functions that is available to all JavaScript calls
	// init() is called when GeoGebra starts up (eg to start listeners)
	/*
	 * private String libraryScriptxxx ="function ggbOnInit() {}"; private
	 * String libraryScriptxx ="function ggbOnInit() {"+
	 * "ggbApplet.evalCommand('A=(1,2)');" +
	 * //"ggbApplet.registerAddListener('listener');" +
	 * "ggbApplet.registerObjectUpdateListener('A','listener');" + "}" +
	 * "function listener() {//java.lang.System.out.println('add listener called');\n"
	 * + "var x = ggbApplet.getXcoord('A');" +
	 * "var y = ggbApplet.getYcoord('A');" + "var len = Math.sqrt(x*x + y*y);" +
	 * "if (len > 5) { x=x*5/len; y=y*5/len; }" + "" +
	 * "ggbApplet.unregisterObjectUpdateListener('A');" +
	 * "ggbApplet.setCoords('A',x,y);" +
	 * "ggbApplet.registerObjectUpdateListener('A','listener');" + "}";
	 */

	protected HashMap<Construction, Scriptable> globalScopeMap;

	public ScriptManagerD(App app) {
		super(app);

		globalScopeMap = new HashMap<Construction, Scriptable>();

		// evalScript("ggbOnInit();");
	}

	@Override
	public void ggbOnInit() {

		try {
			// call only if libraryJavaScript is not the default (ie do nothing)
			if (!((AppD) app).getKernel().getLibraryJavaScript()
					.equals(Kernel.defaultLibraryJavaScript)) {
				evalJavaScript(((AppD) app), "ggbOnInit();", null);
			}
		} catch (Exception e) {
			App.debug("Error calling ggbOnInit(): " + e.getMessage());
		}

	}

	@Override
	public synchronized void initJavaScript() {

		if (app.isApplet()) {
			((AppD) app).getApplet().initJavaScript();
		}
	}

	@Override
	public void callJavaScript(String jsFunction, Object[] args) {

		if (app.isApplet() && app.useBrowserForJavaScript()) {
			((AppD) app).getApplet().callJavaScript(jsFunction, args);
		} else {

			StringBuilder sb = new StringBuilder();
			sb.append(jsFunction);
			sb.append("(");
			for (int i = 0; i < args.length; i++) {
				sb.append('"');
				sb.append(args[i].toString());
				sb.append('"');
				if (i < args.length - 1)
					sb.append(",");
			}
			sb.append(");");

			App.debug(sb.toString());

			evalJavaScript(app, sb.toString(), null);

		}
	}

	public HashMap<Construction, Scriptable> getGlobalScopeMap() {
		return globalScopeMap;
	}

	public void setGlobalScript() {

		Scriptable globalScope = CallJavaScript.evalGlobalScript(app);
		globalScopeMap.put(app.getKernel().getConstruction(), globalScope);

	}

	public void evalJavaScript(App app, String script, String arg) {

		if (globalScopeMap.get(app.getKernel().getConstruction()) == null) {
			setGlobalScript();
		}
		CallJavaScript.evalScript(app, script, arg);
	}

}
