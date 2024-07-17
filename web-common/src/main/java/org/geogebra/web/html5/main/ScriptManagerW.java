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
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.scripting.Sandbox;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.JsRunnable;

import elemental2.core.Function;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Provides JavaScript scripting for objects and initializes the public API.
 */
public class ScriptManagerW extends ScriptManager {

	public static final String ASSESSMENT_APP_PREFIX = "ggbAssess";
	private final JsPropertyMap<Object> exportedApi;
	private Sandbox sandbox;
	private static boolean mayExportDefaultApplet = true;

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
		if (app.getAppletId().equals(AppletParameters.DEFAULT_APPLET_ID)) {
			preventExport();
		}
	}

	private static void preventExport() {
		mayExportDefaultApplet = false;
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

	public static void ggbOnInitExternal(String arg, Object self) {
		JsEval.callNativeGlobalFunction("ggbOnInit", arg, self);
	}

	@Override
	public void ggbOnInit() {
		try {
			tryTabletOnInit();
			final String param = ((AppW) app).getAppletId();
			if (app.useBrowserForJavaScript()) {
				ggbOnInitExternal(param, exportedApi);
			} else if (!app.getEventDispatcher().isDisabled(ScriptType.JAVASCRIPT)) {
				ggbOnInitInternal(param);
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

		GeoGebraFrameW appletFrame = ((AppW) app).getAppletFrame();
		if (appletFrame != null
				&& appletFrame.getOnLoadCallback() != null
				&& !appletFrame.appletOnLoadCalled()) {
			JsEval.callNativeFunction(
					appletFrame.getOnLoadCallback(), exportedApi);
			// callback only needed on first file load, not switching slides
			appletFrame.appletOnLoadCalled(true);
		}
	}

	private void ggbOnInitInternal(String param) {
		String libraryJavaScript = app.getKernel().getLibraryJavaScript();
		boolean standardJS = libraryJavaScript
				.equals(Kernel.defaultLibraryJavaScript);
		if (!standardJS) {
			libraryJavaScript += ";ggbOnInit(\"" + param + "\",ggbApplet)";
			app.evalJavaScript(app, libraryJavaScript, null);
		}
	}

	private void tryTabletOnInit() {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().callPlugin("GgbOnInit", new Object[] {1});
		}
	}

	@Override
	protected void callListener(String listener, Object[] args) {
		if (((AppW) app).getAppletParameters().getParamSandbox()) {
			if (!getSandbox().callByName(listener, args)) {
				Log.error("global listeners not supported in sandbox");
			}
		} else {
			updateGlobalApplet();
			JsEval.callNativeGlobalFunction(listener, args);
		}
	}

	@Override
	protected void callNativeListener(Object listener, Object[] args) {
		if (!((AppW) app).getAppletParameters().getParamSandbox()
				|| !getSandbox().call(listener, args)) {
			updateGlobalApplet();
			JsEval.callNativeFunction(listener, args);
		}
	}

	@Override
	protected void callClientListeners(List<JsReference> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}

		// The array elements are for compatibility purposes only,
		// only the named parameters are documented. Maybe if
		// you are reading this years in the future, you can remove them
		JsArray<String> args = JsArray.of();
		JsPropertyMap<Object> asMap = Js.asPropertyMap(args);
		args.push(evt.type.getName());
		asMap.set("type", evt.type.getName());

		if (evt.targets != null) {
			JsArray<String> targets = JsArray.of();

			for (GeoElement geo : evt.targets) {
				args.push(geo.getLabelSimple());
				targets.push(geo.getLabelSimple());
			}

			asMap.set("targets", targets);
		} else if (evt.target != null) {
			args.push(evt.target.getLabelSimple());
			asMap.set("target", evt.target.getLabelSimple());
		} else {
			args.push("");
		}

		if (evt.argument != null) {
			args.push(evt.argument);
			asMap.set("argument", evt.argument);
		}

		if (evt.jsonArgument != null) {
			addToJsObject(asMap, evt.jsonArgument);
		}

		for (JsReference listener : listeners) {
			callListener(listener, args);
		}
	}

	/**
	 * @param jsMap js map object to be filled with data from Java map
	 * @param map (String, Object) map to be converted to JavaScript,
	 *            Object can be Integer, Double, String or String[] (used e.g. by mousedown hits).
	 */
	public static void addToJsObject(JsPropertyMap<Object> jsMap, Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			Object object = entry.getValue();
			if (object instanceof Integer) {
				jsMap.set(entry.getKey(), unbox((Integer) object));
			} if (object instanceof String[]) {
				JsArray<String> clean = JsArray.of();
				for (String s: (String[]) object) {
					clean.push(s);
				}
				jsMap.set(entry.getKey(), clean);
			} else {
				jsMap.set(entry.getKey(), object);
			}
		}
	}

	/* Workaround for strange autoboxing */
	private static int unbox(Integer object) {
		return object;
	}

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

	private void updateGlobalApplet() {
		// if one applet has "ggbApplet" as ID, keep the global reference
		// also only export it if it's already global (see ASSESSMENT_APP_PREFIX)
		if (mayExportDefaultApplet
				&& Js.asPropertyMap(DomGlobal.window).has("ggbApplet")) {
			export("ggbApplet", exportedApi);
		}
	}

	/**
	 * @return JavaScript sandbox
	 */
	public Sandbox getSandbox() {
		if (sandbox == null) {
			sandbox = new Sandbox(getApi());
		}
		return sandbox;
	}
}
