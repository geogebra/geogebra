package org.geogebra.web.html5.main;

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
	private String ggbApplet;
	private JavaScriptObject api;

	/**
	 * @param app
	 *            application
	 */
	public ScriptManagerW(AppW app) {
		super(app);

		// this should contain alphanumeric characters only,
		// but it is not checked otherwise
		ggbApplet = app.getAppletId();

		api = initAppletFunctions(app.getGgbApi());
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
					ggbOnInit(param, api);
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
					((AppW) app).getAppletFrame().getOnLoadCallback(), api);
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
				callListenerNativeArray(this.api, jsFunction, args);
				return;
			}
			String singleArg = args != null && args.length > 0
					? args[0] : null;
			callListenerNative(this.api, jsFunction, singleArg, null);
			return;
		}
		app.callAppletJavaScript(jsFunction, args);
		} catch (Throwable t) {
			// Log.printStacktrace("");
			Log.warn("Error in user script: " + jsFunction + " : "
					+ t.getMessage());
		}
	}

	@Override
	public void callJavaScript(String jsFunction, String arg0, String arg1) {
		try {
			if (jsFunction != null && jsFunction.length() > 0
					&& jsFunction.charAt(0) <= '9') {
				callListenerNative(this.api, jsFunction, arg0, arg1);
				return;
			}
			JsEval.callAppletJavaScript(jsFunction, arg0, arg1);
		} catch (Throwable t) {
			// Log.printStacktrace("");
			Log.warn("Error in user script: " + jsFunction + " : "
					+ t.getMessage());
		}
	}

	private native void callListenerNative(JavaScriptObject api2,
			String jsFunction, String arg0, String arg1) /*-{
		api2.listeners[jsFunction * 1](arg0, arg1);

	}-*/;

	private native void callListenerNativeArray(JavaScriptObject api2,
			String jsFunction, String... args) /*-{
		api2.listeners[jsFunction * 1](args);

	}-*/;

	// TODO - needed for every ggm instance
	private native JavaScriptObject initAppletFunctions(
	        org.geogebra.web.html5.main.GgbAPIW ggbAPI) /*-{

		var ggbApplet = this.@org.geogebra.web.html5.main.ScriptManagerW::ggbApplet;

		//set the reference
		//$doc[ggbApplet] = $wnd[ggbApplet] = {};

		var api = {listeners:[]};
		var getId = function(obj) {
			if (typeof obj === 'string') {
				return obj;
			}
			for(var i = 0;i<api.listeners.length;i++){
				if(api.listeners[i] === obj){
					return i + "";
				}
			}
			api.listeners[api.listeners.length]=obj;
			return (api.listeners.length-1) + "";
		};

		api.getXML = function(objName) {
			if (objName) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getXML(Ljava/lang/String;)(objName + "");
			} else {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getXML()();
			}
		};

		api.getAlgorithmXML = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAlgorithmXML(Ljava/lang/String;)(objName + "");
		};

		api.getPerspectiveXML = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPerspectiveXML()();
		};

		api.getBase64 = function(param1, param2) {
			if (param2 === false) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64(Z)(false);
			}
			if (param2 === true) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64(Z)(true);
			}
			if (param2) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(!!param1, param2);
			} else if (param1) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(false, param1);
			} else {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64()();
			}

		};

		api.setBase64 = function(base64string, callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setBase64(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(base64string + "", callback);
		};

		api.openFile = function(filename, callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::openFile(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(base64string + "", callback);
		};

		api.login = function(token, ui) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::login(Ljava/lang/String;Z)(token  + "", !!ui);
		};

		api.logout = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::logout()();
		};

		api.setXML = function(xml) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setXML(Ljava/lang/String;)(xml + "");
		};

		api.evalXML = function(xmlString) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalXML(Ljava/lang/String;)(xmlString + "");
		};

		api.setDisplayStyle = function(objName, style) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setDisplayStyle(Ljava/lang/String;Ljava/lang/String;)(objName + "", style + "");
		};

		api.evalCommand = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalCommandNoException(Ljava/lang/String;)(cmdString + "");
		};

		api.evalCommandGetLabels = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalCommandGetLabelsNoException(Ljava/lang/String;)(cmdString + "");
		};

		api.asyncEvalCommand = function(cmdString) {
		    return new Promise(function(resolve, reject) {
                ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::asyncEvalCommand(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(cmdString + "", resolve, reject);
			})
		};

		api.asyncEvalCommandGetLabels = function(cmdString) {
		    return new Promise(function(resolve, reject) {
                ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::asyncEvalCommandGetLabels(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(cmdString + "", resolve, reject);
			})
		};

		api.evalCommandCAS = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalCommandCAS(Ljava/lang/String;)(cmdString + "");
		};

		api.evalGeoGebraCAS = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalGeoGebraCAS(Ljava/lang/String;)(cmdString + "");
		};

		api.setFixed = function(objName, flag, selection) {
			if(typeof selection === 'undefined'){
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFixed(Ljava/lang/String;Z)(objName + "", !!flag);
			}else{
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFixed(Ljava/lang/String;ZZ)(objName + "", !!flag, !!selection);
			}
		};

		api.setOnTheFlyPointCreationActive = function(flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setOnTheFlyPointCreationActive(Z)(!!flag);
		};

		api.setUndoPoint = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setUndoPoint()();
		};

		api.setSaved = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setSaved()();
		};

		api.initCAS = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::initCAS()();
		};

		api.uploadToGeoGebraTube = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::uploadToGeoGebraTube()();
		};

		api.setErrorDialogsActive = function(flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setErrorDialogsActive(Z)(!!flag);
		};

		api.reset = function() {//TODO: implement this in Desktop and Web
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::reset()();
		};

		api.refreshViews = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::refreshViews()();
		};

		api.setVisible = function(objName, visible) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setVisible(Ljava/lang/String;Z)(objName + "", !!visible);
		};

		api.getVisible = function(objName, view) {
			if (typeof view !== 'undefined') {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getVisible(Ljava/lang/String;I)(objName + "",view);
			}
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getVisible(Ljava/lang/String;)(objName + "");
		};

		api.setLayer = function(objName, layer) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLayer(Ljava/lang/String;I)(objName + "",layer);
		};

		api.getLayer = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLayer(Ljava/lang/String;)(objName + "");
		};

		api.setLayerVisible = function(layer, visible) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLayerVisible(IZ)(layer, !!visible);
		};

		api.setTrace = function(objName, flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setTrace(Ljava/lang/String;Z)(objName + "", !!flag);
		};

		api.isTracing = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isTracing(Ljava/lang/String;)(objName + "");
		};

		api.setLabelVisible = function(objName, visible) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLabelVisible(Ljava/lang/String;Z)(objName + "", !!visible);
		};

		api.setLabelStyle = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLabelStyle(Ljava/lang/String;I)(objName + "",style);
		};

		api.getLabelStyle = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLabelStyle(Ljava/lang/String;)(objName + "");
		};

		api.getLabelVisible = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLabelVisible(Ljava/lang/String;)(objName + "");
		};

		api.setColor = function(objName, red, green, blue) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setColor(Ljava/lang/String;III)(objName + "",red,green,blue);
		};

		api.setCorner = function(objName, x, y, index) {
			if (!index) {
				index = 1;
			}
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCorner(Ljava/lang/String;DDI)(objName + "",x,y,index);
		};

		api.setLineStyle = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLineStyle(Ljava/lang/String;I)(objName + "",style);
		};

		api.setLineThickness = function(objName, thickness) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLineThickness(Ljava/lang/String;I)(objName + "",thickness);
		};

		api.setPointStyle = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPointStyle(Ljava/lang/String;I)(objName + "",style);
		};

		api.setPointSize = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPointSize(Ljava/lang/String;I)(objName + "",style);
		};

		api.setFilling = function(objName, filling) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFilling(Ljava/lang/String;D)(objName + "",filling);
		};

		api.getColor = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getColor(Ljava/lang/String;)(objName + "");
		};

		api.getPenColor = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPenColor()();
		};

		api.getPenSize = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPenSize()();
		};

		api.setPenSize = function(size) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPenSize(I)(size);
		};

		api.setPenColor = function(red,green,blue) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPenColor(III)(red,green,blue);
		};

		api.getFilling = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getFilling(Ljava/lang/String;)(objName + "");
		};

		api.getLineStyle = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLineStyle(Ljava/lang/String;)(objName + "");
		};

		api.getLineThickness = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLineThickness(Ljava/lang/String;)(objName + "");
		};

		api.getPointStyle = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPointStyle(Ljava/lang/String;)(objName + "");
		};

		api.getPointSize = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPointSize(Ljava/lang/String;)(objName + "");
		};

		api.deleteObject = function(objName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::deleteObject(Ljava/lang/String;)(objName + "");
		};

		api.setAnimating = function(objName, animate) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAnimating(Ljava/lang/String;Z)(objName + "", !!animate);
		};

		api.setAnimationSpeed = function(objName, speed) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAnimationSpeed(Ljava/lang/String;D)(objName + "",speed);
		};

		api.startAnimation = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::startAnimation()();
		};

		api.stopAnimation = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::stopAnimation()();
		};
		
		api.setAuxiliary = function(objName, auxiliary) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAuxiliary(Ljava/lang/String;Z)(objName + "", !!auxiliary);
		};

		api.hideCursorWhenDragging = function(hideCursorWhenDragging) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::hideCursorWhenDragging(Z)(!!hideCursorWhenDragging);
		};

		api.isAnimationRunning = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isAnimationRunning()();
		};
		
		api.getFrameRate = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getFrameRate()();
		};

		api.renameObject = function(oldName, newName, force) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::renameObject(Ljava/lang/String;Ljava/lang/String;Z)(oldName + "",newName + "", !!force);
		};

		api.exists = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exists(Ljava/lang/String;)(objName + "");
		};

		api.isDefined = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isDefined(Ljava/lang/String;)(objName + "");
		};

		api.getValueString = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getValueString(Ljava/lang/String;)(objName + "");
		};

		api.getListValue = function(objName, index) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getListValue(Ljava/lang/String;I)(objName + "", index);
		};

		api.getDefinitionString = function(objName, localize) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getDefinitionString(Ljava/lang/String;Z)(objName + "",  typeof localize === 'undefined' ? true : !!localize);
		};

		api.getLaTeXString = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLaTeXString(Ljava/lang/String;)(objName + "");
		};

		api.getLaTeXBase64 = function(objName, value) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLaTeXBase64(Ljava/lang/String;Z)(objName + "", !!value);
		};

		api.getCommandString = function(objName, localize) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getCommandString(Ljava/lang/String;Z)(objName + "", typeof localize === 'undefined' ? true : !!localize);
		};

		api.getCaption = function(objName, subst) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getCaption(Ljava/lang/String;Z)(objName + "", !!subst);
		};

		api.setCaption = function(objName, caption) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCaption(Ljava/lang/String;Ljava/lang/String;)(objName + "", caption + "");
		};

		api.getXcoord = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getXcoord(Ljava/lang/String;)(objName + "");
		};

		api.getYcoord = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getYcoord(Ljava/lang/String;)(objName + "");
		};

		api.getZcoord = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getZcoord(Ljava/lang/String;)(objName + "");
		};

		api.setCoords = function(objName, x, y, z) {
			if (typeof z === 'undefined') {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCoords(Ljava/lang/String;DD)(objName + "",x,y);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCoords(Ljava/lang/String;DDD)(objName + "",x,y,z);
			}
		};

		api.getValue = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getValue(Ljava/lang/String;)(objName + "");
		};

		api.getVersion = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getVersion()();
		};

		api.getScreenshotBase64 = function(callback) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getScreenshotBase64(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
		};

		api.getThumbnailBase64 = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getThumbnailBase64()();
		};

		api.setValue = function(objName, x) {
			// #4035 
			// need to support possible syntax error 
			// eg setValue("a","3") rather than setValue("a",3) 
			if (typeof x === "string") {
				if (x === "true") {
					x = true;
				} else if (x === "false") {
					x = false;
				} else {
					// force string -> number (might give NaN) 
					x = x * 1;
				}
			}

			if (typeof x !== "number" && typeof x !== "boolean") {
				// avoid possible strange effects 
				return;
			}
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setValue(Ljava/lang/String;D)(objName + "",x);
		};

		api.setTextValue = function(objName, x) {

			x = x + "";

			if (typeof objName !== "string") {
				// avoid possible strange effects 
				return;
			}
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setTextValue(Ljava/lang/String;Ljava/lang/String;)(objName + "",x);
		};

		api.setListValue = function(objName, x, y) {
			// #4035 
			// need to support possible syntax error 
			if (typeof x === "string") {
				if (x === "true") {
					x = 1;
				} else if (x === "false") {
					x = 0;
				} else {
					// force string -> number (might give NaN) 
					x = x * 1;
				}
			}

			if (typeof y === "string") {
				if (y === "true") {
					y = 1;
				} else if (y === "false") {
					y = 0;
				} else {
					// force string -> number (might give NaN) 
					y = y * 1;
				}
			}

			if (typeof x !== "number" || typeof y !== "number") {
				// avoid possible strange effects 
				return;
			}
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setListValue(Ljava/lang/String;DD)(objName + "",x,y);
		};

		api.setRepaintingActive = function(flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setRepaintingActive(Z)(!!flag);
		};

		api.setCoordSystem = function(xmin, xmax, ymin, ymax, zmin, zmax,
				verticalY) {
			if (typeof zmin !== "number") {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCoordSystem(DDDD)(xmin,xmax,ymin,ymax);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCoordSystem(DDDDDDZ)(xmin,xmax,ymin,ymax,zmin,zmax, !!verticalY);
			}
		};

		api.setAxesVisible = function(arg1, arg2, arg3, arg4) {
			if (typeof arg3 === "undefined") {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxesVisible(ZZ)(!!arg1, !!arg2);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxesVisible(IZZZ)(arg1, !!arg2, !!arg3, !!arg4);
			}
		};

		api.setAxisUnits = function(arg1, arg2, arg3, arg4) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxisUnits(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(arg1, arg2 + "", arg3 + "", arg4 + "");
		};

		api.setAxisLabels = function(arg1, arg2, arg3, arg4) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxisLabels(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(arg1, arg2 + "", arg3 + "", arg4 + "");
		};

		api.setAxisSteps = function(arg1, arg2, arg3, arg4) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxisSteps(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(arg1, arg2 + "",arg3 + "", arg4 + "");
		};

		api.getAxisUnits = $entry(function(arg1) {
			return [].concat(ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAxisUnits(I)(1 * arg1 || 1));
		});

		api.getAxisLabels = $entry(function(arg1) {
			return [].concat(ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAxisLabels(I)(1 * arg1 || 1));
		});

		api.setPointCapture = function(view, capture) {
			if(typeof capture === "undefined"){
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPointCapture(II)(1, view);
			}else{
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPointCapture(II)(view, capture);
			}
		};

		api.getGridVisible = function(view) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getGridVisible(I)(view || 1);
		};

		api.setGridVisible = function(arg1, arg2) {
			if (typeof arg2 === "undefined") {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setGridVisible(Z)(!!arg1);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setGridVisible(IZ)(arg1, !!arg2);
			}
		};

		api.getAllObjectNames = function(objectType) {
			if(typeof objectType === "undefined"){
				return [].concat(ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAllObjectNames()());
			}else{
				return [].concat(ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAllObjectNames(Ljava/lang/String;)(objectType + ""));
			}
			
		};

		api.getObjectNumber = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getObjectNumber()();
		};

		api.getObjectName = function(i) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getObjectName(I)(i);
		};

		api.getObjectType = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getObjectType(Ljava/lang/String;)(objName + "");
		};

		api.setMode = function(mode) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setMode(I)(mode);
		};

		api.getMode = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getMode()();
		};
		
		api.getToolName = function(i) {
        	return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getToolName(I)(i);
        };

		api.openMaterial = function(material) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::openMaterial(Ljava/lang/String;)(material + "");
		};

		// not supported by GgbAPI Desktop,Web
		//$wnd[ggbApplet].callJavaScript = function(jsFunction, args) {
		//	ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::callJavaScript(Ljava/lang/String;Ljava/lang/String;)(jsFunction,args);
		//};

		api.registerAddListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerAddListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerStoreUndoListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerStoreUndoListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterAddListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterAddListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerRemoveListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerRemoveListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterRemoveListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterRemoveListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerClearListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerClearListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterClearListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterClearListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerRenameListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerRenameListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterRenameListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerRenameListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerUpdateListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerUpdateListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterUpdateListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterUpdateListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerClientListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerClientListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterClientListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterClientListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerObjectUpdateListener = function(objname, JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerObjectUpdateListener(Ljava/lang/String;Ljava/lang/String;)(objname + "", getId(JSFunctionName));
		};

		api.unregisterObjectUpdateListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterObjectUpdateListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerObjectClickListener = function(objname, JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerObjectClickListener(Ljava/lang/String;Ljava/lang/String;)(objname + "", getId(JSFunctionName));
		};

		api.unregisterObjectClickListener = function(objname) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterObjectClickListener(Ljava/lang/String;)(objname + "");
		};

		api.registerClickListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerClickListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterClickListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterClickListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.undo = function(repaint) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::undo(Z)(!!repaint);
		};

		api.redo = function(repaint) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::redo(Z)(!!repaint);
		};

		api.newConstruction = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::newConstruction()();
		};

		api.debug = function(str) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::debug(Ljava/lang/String;)(str + "");
		};

		api.setWidth = function(width) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setWidth(I)(width);
		};

		api.setHeight = function(height) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setHeight(I)(height);
		};

		api.setSize = function(width, height) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setSize(II)(width, height);
		};

		api.enableRightClick = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableRightClick(Z)(!!enable);
		};

		api.enableLabelDrags = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableLabelDrags(Z)(!!enable);
		};

		api.enableShiftDragZoom = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableShiftDragZoom(Z)(!!enable);
		};

		api.showToolBar = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showToolBar(Z)(!!show);
		};

		api.setCustomToolBar = function(toolbarDef) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCustomToolBar(Ljava/lang/String;)(toolbarDef + "");
		};

		api.showMenuBar = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showMenuBar(Z)(!!show);
		};

		api.showAlgebraInput = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showAlgebraInput(Z)(!!show);
		};

		api.showResetIcon = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showResetIcon(Z)(!!show);
		};

		api.getViewProperties = function(show) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getViewProperties(I)(show);
		};

		api.setFont = function(label, size, bold, italic, serif) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFont(Ljava/lang/String;IZZZ)(label + "", size, !!bold, !!italic, !!serif);
		};

		api.insertImage = function(url, corner1, corner2, corner4) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::insertImage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(url + "", corner1+"", corner2+"", corner4+"");
		};

		api.recalculateEnvironments = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::recalculateEnvironments()();
		};

		api.isIndependent = function(label) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isIndependent(Ljava/lang/String;)(label + "");
		};

		api.isMoveable = function(label) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isMoveable(Ljava/lang/String;)(label + "");
		};

		api.setPerspective = function(code) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPerspective(Ljava/lang/String;)(code + "");
		};
		
		api.enableCAS = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableCAS(Z)(!!enable);
		};
		
		api.enable3D = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enable3D(Z)(!!enable);
		};

		api.getPNGBase64 = function(exportScale, transparent, dpi,
				copyToClipboard, greyscale) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPNGBase64(DZDZZ)(exportScale, !!transparent, dpi, !!copyToClipboard, !!greyscale);
		};
		
		api.exportGIF = function(sliderLabel, scale, timeBetweenFrames, isLoop, filename, rotate) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportGIF(Ljava/lang/String;DDZLjava/lang/String;D)(sliderLabel, scale, timeBetweenFrames | 500, !!isLoop, filename, rotate | 0);
		};

		api.getFileJSON = function(thumbnail) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getFileJSON(Z)(!!thumbnail);
		};
		
		api.setFileJSON = function(zip) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFileJSON(Lcom/google/gwt/core/client/JavaScriptObject;)(zip);
		};

		api.setLanguage = function(lang) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLanguage(Ljava/lang/String;)(lang + "");
		};

		api.showTooltip = function(lang) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showTooltip(Ljava/lang/String;)(lang + "");
		};

		api.remove = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::removeApplet()();
			$doc[ggbApplet] = $wnd[ggbApplet] = api = null;
		};

		// APPS-646 deprecated, needs changing to getValue("correct")
        api.getExerciseFraction = function() {
			return ggbAPI.@org.geogebra.common.plugin.GgbAPI::getExerciseFraction()();
		};

		// APPS-646 Exercises no longer supported
        api.isExercise = function() {
			return false;
		};

		api.setExternalPath = function(path) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setExternalPath(Ljava/lang/String;)(path + "");
		};

		api.checkSaved = function(path) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::checkSaved(Lcom/google/gwt/core/client/JavaScriptObject;)(path);
		};

		api.getCASObjectNumber = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getCASObjectNumber()();
		};

		api.writePNGtoFile = function(filename, exportScale, transparent, DPI, greyscale) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::writePNGtoFile(Ljava/lang/String;DZDZ)(filename + "", exportScale, !!transparent, DPI, !!greyscale);
		};

		api.exportPGF = function(callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportPGF(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
		};
		
		api.exportSVG = function(filename) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportSVG(Ljava/lang/String;)(filename);
		};

		api.exportPDF = function(scale, filename, sliderLabel) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportPDF(DLjava/lang/String;Ljava/lang/String;)(scale | 1, filename, sliderLabel);
		};

		api.exportPSTricks = function(callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportPSTricks(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
		};

		api.exportAsymptote = function(callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportAsymptote(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
		};

		api.setRounding = function(digits) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setRounding(Ljava/lang/String;)(digits + "");
		};

		api.getRounding = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getRounding()();
		};

		api.copyTextToClipboard = function(text) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::copyTextToClipboard(Ljava/lang/String;)(text + "");
		};
		
		api.evalLaTeX = function(text,mode) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalLaTeX(Ljava/lang/String;I)(text + "", mode);
		};

		api.evalMathML = function(text) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalMathML(Ljava/lang/String;)(text + "");
		};

		api.getScreenReaderOutput = function(text) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getScreenReaderOutput(Ljava/lang/String;)(text + "");
		};

		api.getEditorState = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getEditorState()();
		};

		api.setEditorState = function(text, label) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setEditorState(Ljava/lang/String;Ljava/lang/String;)(text + "", label || "");
		};

		api.exportCollada = function(xmin, xmax, ymin, ymax, zmin, zmax, 
					xyScale, xzScale, xTickDistance, yTickDistance, zTickDistance) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportCollada(DDDDDDDDDDD)(
			    xmin || -5, xmax || 5, ymin || -5, ymax || 5, zmin || -5, zmax || 5, xyScale || 1, 
			    xzScale || 1, xTickDistance || -1, yTickDistance || -1, zTickDistance || -1);
		};

		api.exportSimple3d = function(name, xmin, xmax, ymin, ymax, zmin, zmax, 
					xyScale, xzScale, xTickDistance, yTickDistance, zTickDistance) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportSimple3d(Ljava/lang/String;DDDDDDDDDDD)(
			    name + "", xmin, xmax, ymin, ymax, zmin, zmax, xyScale, 
			    xzScale, xTickDistance, yTickDistance, zTickDistance);
		};

		api.translate = function(arg1, callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::translate(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(arg1 + "", callback);
		};

		api.exportConstruction = function(flags) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportConstruction(Lcom/google/gwt/core/client/JsArrayString;)(flags || ["color","name","definition","value"]);
		};

		api.getConstructionSteps = function(breakpoints) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getConstructionSteps(Z)(!!breakpoints);
		};

		api.setConstructionStep = function(n, breakpoints) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setConstructionStep(DZ)(n, !!breakpoints);
		};

		api.previousConstructionStep = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::previousConstructionStep()();
		};
		
		api.nextConstructionStep = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::nextConstructionStep()();
		};

		api.getEmbeddedCalculators = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getEmbeddedCalculators()();
		};

		api.getFrame = function(){
			return ggbApi.@org.geogebra.web.html5.main.GgbAPIW::getFrame()();
		}

		api.enableFpsMeasurement = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableFpsMeasurement()();
		};

		api.disableFpsMeasurement = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::disableFpsMeasurement()();
		};

		api.testDraw = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::testDraw()();
		};

		api.startDrawRecording = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::startDrawRecording()();
		};

		api.endDrawRecordingAndLogResults = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::endDrawRecordingAndLogResults()();
		};

		$doc[ggbApplet] = $wnd[ggbApplet] = api;
		return api;

	}-*/;

	public JavaScriptObject getApi() {
		return api;
	}
}
