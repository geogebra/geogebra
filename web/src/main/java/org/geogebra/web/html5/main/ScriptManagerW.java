package org.geogebra.web.html5.main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.JsReference;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.util.JsRunnable;

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

	public static final String ASSESSMENT_APP_PREFIX = "ggbAssess";
	private final JsPropertyMap<Object> exportedApi;

	/**
	 * @param app
	 *            application
	 */
	public ScriptManagerW(AppW app, ExportedApi exporter) {
		super(app);
		exporter.setGgbAPI(app.getGgbApi());
		exporter.setScriptManager(this);
		this.exportedApi = bindMethods(exporter);
		if (!app.getAppletId().startsWith(ASSESSMENT_APP_PREFIX)) {
			export(exportedApi);
		}
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

	/**
	 * NPE safe way of running stuff
	 * @param callback callback
	 */
	public static void runCallback(JsRunnable callback) {
		if (callback != null) {
			callback.run();
		}
	}

	public static void ggbOnInit(String arg, Object self) {
		JsEval.callNativeGlobalFunction("ggbOnInit", arg, self);
	}

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

				ggbOnInit(param, exportedApi);
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
			JsEval.callNativeFunction(
					((AppW) app).getAppletFrame().getOnLoadCallback(), exportedApi);
		}
	}

	private native void tryTabletOnInit() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin('GgbOnInit', [ 1 ]);
		}
	}-*/;

	@Override
	protected void callListener(String listener, Object[] args) {
		JsEval.callNativeGlobalFunction(listener, args);
	}

	@Override
	protected void callNativeListener(Object listener, Object[] args) {
		JsEval.callNativeFunction(listener, args);
	}

	@Override
	protected void callClientListeners(List<JsReference> listeners, Event evt) {
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

		for (JsReference listener : listeners) {
			callListener(listener, args);
		}
	}

	/**
	 * @param jsObject jsObject to be filled with data from map
	 * @param map (String, Object) map to be converted to JavaScript,
	 *            Object can be Integer, Double, String or String[],
	 */
	public static void addToJsObject(Object jsObject, Map<String, Object> map) {
		JsPropertyMap<Object> jsMap = Js.asPropertyMap(jsObject);
		for (Entry<String, Object> entry : map.entrySet()) {
			Object object = entry.getValue();
			if (object instanceof Integer) {
				jsMap.set(entry.getKey(), unbox((Integer) object));
			} else if (object instanceof Double
					|| object instanceof String[]) {
				jsMap.set(entry.getKey(), object);
			} else {
				jsMap.set(entry.getKey(), object.toString());
			}
		}
	}

	/* Workaround for strange autoboxing */
	private static int unbox(Integer object) {
		return object;
	}

	private static native void set(JavaScriptObject json, String key, Object value) /*-{
		json[key] = value;
	}-*/;

	/**
	 * @param toExport API object
	 */
	public void export(JsPropertyMap<Object> toExport) {
		String appletId = ((AppW) app).getAppletId();
		export(appletId, toExport);
	}

	/**
	 * @param appletId applet ID
	 * @param toExport API
	 */
	public static void export(String appletId, Object toExport) {
		if (toExport == null) {
			Js.asPropertyMap(DomGlobal.window).delete(appletId);
			Js.asPropertyMap(DomGlobal.document).delete(appletId);
		} else {
			Js.asPropertyMap(DomGlobal.window).set(appletId, toExport);
			Js.asPropertyMap(DomGlobal.document).set(appletId, toExport);
		}
	}

	public Object getApi() {
		return exportedApi;
	}
}
