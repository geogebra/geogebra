package org.geogebra.web.html5.main;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Provides JavaScript scripting for objects and initializes the public API.
 */
public class ScriptManagerW extends ScriptManager {

	@ExternalAccess
	private JavaScriptObject exportedApi;
	private HashMap<String, JavaScriptObject> listeners = new HashMap<>();
	private ApiExporter exporter;

	/**
	 * @param app
	 *            application
	 */
	public ScriptManagerW(AppW app, ApiExporter exporter) {
		super(app);
		this.exporter = exporter;
		// this should contain alphanumeric characters only,
		// but it is not checked otherwise
		exportedApi = initAppletFunctions(app.getGgbApi(), app.getAppletId());
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

	public static native void ggbOnInit(String arg, JavaScriptObject self) /*-{
		if (typeof $wnd.ggbOnInit === 'function')
			$wnd.ggbOnInit(arg, self);
	}-*/;

	@Override
	public void ggbOnInit() {
		try {
			// Log.debug("almost there" + app.useBrowserForJavaScript());
			// assignGgbApplet();
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
			JsEval.runCallback(
					((AppW) app).getAppletFrame().getOnLoadCallback(), exportedApi);
		}
	}

	private native void tryTabletOnInit() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin('GgbOnInit', [ 1 ]);
		}
	}-*/;

	@Override
	public void callJavaScript(String jsFunction, String[] args) {
		try {
		if (jsFunction != null && jsFunction.length() > 0
				&& jsFunction.charAt(0) <= '9') {
				if (args != null && args.length > 1) {
					callListenerNativeArray(listeners.get(jsFunction), args);
					return;
				}
				String singleArg = args != null && args.length > 0 ? args[0]
						: null;
				callListenerNative(listeners.get(jsFunction), singleArg, null);
				return;
			}
			app.callAppletJavaScript(jsFunction, args);
		} catch (Throwable t) {
			Log.warn("Error in user script: " + jsFunction + " : "
					+ t.getMessage());
		}
	}

	@Override
	public void callJavaScript(String jsFunction, String[] args, HashMap<String, String> jsonArgument) {
		if (jsonArgument == null) {
			callJavaScript(jsFunction, args);
		} else {
			try {
				callListenerNativeJson(listeners.get(jsFunction),
						convertToJSObject(jsonArgument), args);
			} catch (Throwable t) {
				Log.warn("Error in user script: " + jsFunction + " : "
						+ t.getMessage());
			}
		}
	}

	public static JavaScriptObject convertToJSObject(HashMap<String, String> object) {
		JavaScriptObject json = JavaScriptObject.createObject();
		for (Entry<String, String> entry : object.entrySet()) {
			set(json, entry.getKey(), entry.getValue());
		}

		return json;
	}

	private static native void set(JavaScriptObject json, String key, String value) /*-{
		json[key] = value;
	}-*/;

	@Override
	public void callJavaScript(String jsFunction, String arg0, String arg1) {
		try {
			if (jsFunction != null && jsFunction.length() > 0
					&& jsFunction.charAt(0) <= '9') {
				callListenerNative(listeners.get(jsFunction), arg0, arg1);
				return;
			}
			JsEval.callAppletJavaScript(jsFunction, arg0, arg1);
		} catch (Throwable t) {
			Log.warn("Error in user script: " + jsFunction + " : "
					+ t.getMessage());
		}
	}

	private native void callListenerNative(JavaScriptObject listener,
			String arg0, String arg1) /*-{
		listener(arg0, arg1);
	}-*/;

	private native void callListenerNativeArray(JavaScriptObject listener,
			String... args) /*-{
		listener(args);
	}-*/;

	private native void callListenerNativeJson(JavaScriptObject listener,
		   JavaScriptObject json, String... args) /*-{
		for (key in json) {
			if (json.hasOwnProperty(key)) {
				args[key] = parsed[key];
			}
		}

		listener(args);
	}-*/;

	private JavaScriptObject initAppletFunctions(GgbAPIW ggbAPI,
			String globalName) {
		JavaScriptObject api = JavaScriptObject.createObject();
		exporter.addFunctions(api, ggbAPI);
		exporter.addListenerFunctions(api, ggbAPI,
				getListenerMappingFunction());
		export(api, ggbAPI, globalName);
		return api;
	}

	@ExternalAccess
	private String getListenerID(JavaScriptObject listener) {
		for (Entry<String, JavaScriptObject> entry : listeners.entrySet()) {
			if (entry.getValue() == listener) {
				return entry.getKey();
			}
		}
		String newID = listeners.size() + "";
		listeners.put(newID, listener);
		return newID;
	}

	private native JavaScriptObject getListenerMappingFunction() /*-{
		var that = this;
		return function(listener) {
			if (typeof listener === 'string') {
				return listener;
			} else {
				return that.@org.geogebra.web.html5.main.ScriptManagerW::getListenerID(Lcom/google/gwt/core/client/JavaScriptObject;)(listener);
			}
		}
	}-*/;

	private native void export(JavaScriptObject api, GgbAPIW ggbAPI, String globalName) /*-{
		api.remove = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::removeApplet()();
			$doc[globalName] = $wnd[globalName] = api = null;
		};

		$doc[globalName] = $wnd[globalName] = api;
	}-*/;

	public JavaScriptObject getApi() {
		return exportedApi;
	}
}
