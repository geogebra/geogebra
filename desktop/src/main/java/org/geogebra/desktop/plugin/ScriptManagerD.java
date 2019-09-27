package org.geogebra.desktop.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.script.JsScript;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.mozilla.javascript.Scriptable;

public class ScriptManagerD extends ScriptManager {

	protected HashMap<Construction, Scriptable> globalScopeMap;

	public ScriptManagerD(App app) {
		super(app);

		globalScopeMap = new HashMap<Construction, Scriptable>();
	}

	@Override
	public void ggbOnInit() {
		try {
			// call only if libraryJavaScript is not the default (ie do nothing)
			if (!((AppD) app).getKernel().getLibraryJavaScript()
					.equals(Kernel.defaultLibraryJavaScript)) {
				evalJavaScript(app, "ggbOnInit();", null);
			}
		} catch (Exception e) {
			Log.debug("Error calling ggbOnInit(): " + e.getMessage());
		}
	}

	@Override
	protected void callClientListeners(List<JsScript> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}

		ArrayList<String> args = new ArrayList<>();
		args.add(evt.type.getName());
		if (evt.targets != null) {
			for (GeoElement geo : evt.targets) {
				args.add(geo.getLabelSimple());
			}
		} else if (evt.target != null) {
			args.add(evt.target.getLabelSimple());
		} else {
			args.add("");
		}
		if (evt.argument != null) {
			args.add(evt.argument);
		}

		for (JsScript listener : listeners) {
			callListener(listener.getText(), args.toArray(new String[0]));
		}
	}

	@Override
	protected void callListener(String jsFunction, String arg0, String arg1) {
		callListener(jsFunction, new String[] {arg0, arg1});
	}

	private void callListener(String jsFunction, String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append(jsFunction);
		sb.append("(");
		for (int i = 0; i < args.length; i++) {
			sb.append('"');
			sb.append(args[i].toString());
			sb.append('"');
			if (i < args.length - 1) {
				sb.append(",");
			}
		}
		sb.append(");");
		try {
			evalJavaScript(app, sb.toString(), null);
		} catch (Exception e) {
			Log.debug("error calling script " + e.getMessage());
		}
	}

	public HashMap<Construction, Scriptable> getGlobalScopeMap() {
		return globalScopeMap;
	}

	@Override
	public void setGlobalScript() {
		Scriptable globalScope = CallJavaScript.evalGlobalScript(app);
		globalScopeMap.put(app.getKernel().getConstruction(), globalScope);
	}

	public void evalJavaScript(App app, String script, String arg)
			throws Exception {

		if (globalScopeMap.get(app.getKernel().getConstruction()) == null) {
			setGlobalScript();
		}

		CallJavaScript.evalScript(app, script, arg);
	}
}
