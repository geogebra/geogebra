package org.geogebra.web.html5.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.script.JsScript;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Provides JavaScript scripting for objects and initializes the public API.
 */
public class ScriptManagerW extends ScriptManager {

	private HashMap<String, Object> listeners = new HashMap<>();
	private JsPropertyMap exportedApi;

	/**
	 * @param app
	 *            application
	 */
	public ScriptManagerW(AppW app, ExportedApi exporter) {
		super(app);
		exporter.setGgbAPI(app.getGgbApi());
		exporter.setScriptManager(this);
		this.exportedApi = bindMethods(exporter);
		export(exportedApi);
	}

	private JsPropertyMap<Object> bindMethods(ExportedApi exporter) {
		JsPropertyMap<Object> toExport = JsPropertyMap.of();
		JsPropertyMap<Object> exporterMap = Js.asPropertyMap(exporter);

		exporterMap.forEach(key -> {
			Object current = exporterMap.get(key);

			if ("function".equals(Js.typeof(current))) {
				toExport.set(key, Js.<Function>cast(current).bind(exporterMap));
			}
		});

		return toExport;
	}

	public static native void runCallback(JavaScriptObject onLoadCallback) /*-{
		if (typeof onLoadCallback === "function") {
			onLoadCallback();
		}
	}-*/;

	/**
	 * Run global ggbOnInit without parameters
	 */
	public static native void ggbOnInitStatic() /*-{
		if (typeof $wnd.ggbOnInit === 'function')
			$wnd.ggbOnInit();
	}-*/;

	public static native void ggbOnInit(String arg, Object self) /*-{
		if (typeof $wnd.ggbOnInit === 'function')
			$wnd.ggbOnInit(arg, self);
	}-*/;

	@Override
	public void ggbOnInit() {
		try {
			tryTabletOnInit();
			boolean standardJS = app.getKernel().getLibraryJavaScript()
					.equals(Kernel.defaultLibraryJavaScript);
			if (!standardJS && !app.useBrowserForJavaScript()) {
				app.evalJavaScript(app, app.getKernel().getLibraryJavaScript(),
						null);
			}
			if (!standardJS || app.useBrowserForJavaScript()) {
				final String param = ((AppW) app).getAppletId();

				if (param == null || "".equals(param)) {
					ggbOnInitStatic();
				} else {
					ggbOnInit(param, exportedApi);
				}
			}
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			Log.debug(t.getMessage());
		}
		// set this to run always
		String articleid = ((AppW) app).getArticleId();
		if (articleid != null) {
			AppW.appletOnLoad(articleid);
		}

		if (((AppW) app).getAppletFrame() != null
		        && ((AppW) app).getAppletFrame().getOnLoadCallback() != null) {
			JsEval.callNativeJavaScript(
					((AppW) app).getAppletFrame().getOnLoadCallback(), exportedApi);
		}
	}

	private native void tryTabletOnInit() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin('GgbOnInit', [ 1 ]);
		}
	}-*/;

	@Override
	protected void callListener(String listener, String... args) {
		if (listener.charAt(0) <= '9') {
			JsEval.callNativeJavaScript(listeners.get(listener), args);
		} else {
			JsEval.callNativeJavaScript(listener, args);
		}
	}

	@Override
	protected void callClientListeners(List<JsScript> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}

		// The array elements are for compatibility purposes only,
		// only the named parameters are documented. Maybe if
		// you are reading this years in the future, you can remove them
		JsArrayString args = JsArrayString.createArray().cast();

		args.push(evt.type.getName());
		set(args, "type", evt.type.getName());

		if (evt.targets != null) {
			JsArrayString targets = JsArrayString.createArray().cast();

			for (GeoElement geo : evt.targets) {
				args.push(geo.getLabelSimple());
				targets.push(geo.getLabelSimple());
			}

			set(args, "targets", targets);
		} else if (evt.target != null) {
			args.push(evt.target.getLabelSimple());
			set(args, "target", evt.target.getLabelSimple());
		} else {
			args.push("");
		}

		if (evt.argument != null) {
			args.push(evt.argument);
			set(args, "argument", evt.argument);
		}

		if (evt.jsonArgument != null) {
			addToJsObject(args, evt.jsonArgument);
		}

		for (JsScript listener : listeners) {
			if (listener.getText().charAt(0) <= '9') {
				JsEval.callNativeJavaScript(this.listeners.get(listener.getText()), args);
			} else {
				JsEval.callNativeJavaScript(listener.getText(), args);
			}
		}
	}

	/**
	 * @param jsObject jsObject to be filled with data from map
	 * @param map (String, Object) map to be converted to JavaScript,
	 *            Object can be Integer, Double, String or String[],
	 */
	public static void addToJsObject(JavaScriptObject jsObject, Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			Object object = entry.getValue();

			if (object instanceof Integer) {
				set(jsObject, entry.getKey(), (int) object);
			} else if (object instanceof Double
					|| object instanceof String[]) {
				set(jsObject, entry.getKey(), object);
			} else {
				set(jsObject, entry.getKey(), object.toString());
			}
		}
	}

	private static native void set(JavaScriptObject json, String key, int value) /*-{
		json[key] = value;
	}-*/;

	private static native void set(JavaScriptObject json, String key, Object value) /*-{
		json[key] = value;
	}-*/;

	private String getListenerID(Object listener) {
		for (Entry<String, Object> entry : listeners.entrySet()) {
			if (entry.getValue() == listener) {
				return entry.getKey();
			}
		}
		String newID = listeners.size() + "";
		listeners.put(newID, listener);
		return newID;
	}

	/**
	 * Get the listener id as a string for global js function name or function object
	 * @param listener function name in the global namespace or function object
	 * @return listener id
	 */
	public String getId(Object listener) {
		if ("string".equals(Js.typeof(listener))) {
			return Js.asString(listener);
		} else {
			return getListenerID(listener);
		}
	}

	/**
	 * @param toExport API object
	 */
	public void export(JsPropertyMap<Object> toExport) {
		String appletId = ((AppW) app).getAppletId();
		Js.asPropertyMap(DomGlobal.window).set(appletId, toExport);
		Js.asPropertyMap(DomGlobal.document).set(appletId, toExport);
	}

	public Object getApi() {
		return exportedApi;
	}
}
