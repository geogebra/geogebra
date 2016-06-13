package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Provides JavaScript scripting for objects and initializes the public API.
 */
public class ScriptManagerW extends ScriptManager {

	private String ggbApplet = AppW.DEFAULT_APPLET_ID;
	private JavaScriptObject api;

	/**
	 * @param app
	 *            application
	 */
	public ScriptManagerW(AppW app) {
		super(app);

		// this should contain alphanumeric characters only,
		// but it is not checked otherwise
		ggbApplet = app.getDataParamId();

		api = initAppletFunctions(app.getGgbApi());
	}

	public static native void runCallback(JavaScriptObject onLoadCallback) /*-{
		if (typeof onLoadCallback === "function") {
			onLoadCallback();
		}
	}-*/;

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
			boolean standardJS = app.getKernel().getLibraryJavaScript()
			        .equals(Kernel.defaultLibraryJavaScript);
			if (!standardJS && !app.useBrowserForJavaScript()) {
				app.evalJavaScript(app, app.getKernel().getLibraryJavaScript(),
				        null);
			}
			if (!standardJS || app.useBrowserForJavaScript()) {

				String param = ((AppW) app).getDataParamId();
				if (param == null || "".equals(param)) {
					ggbOnInitStatic();
				} else {
					ggbOnInit(param, api);
				}

			}
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
			runCallback(((AppW) app).getAppletFrame().getOnLoadCallback());
		}
	}

	@Override
	public void callJavaScript(String jsFunction, Object[] args) {
		if (jsFunction != null && jsFunction.length() > 0
				&& jsFunction.charAt(0) <= '9') {
			String singleArg = args != null && args.length > 0
					? (String) args[0] : null;
			callListenerNative(this.api, jsFunction,
 singleArg, null);
			return;
		}
		app.callAppletJavaScript(jsFunction, args);
	}

	@Override
	public void callJavaScript(String jsFunction, Object arg0, Object arg1) {
		if (jsFunction != null && jsFunction.length() > 0
				&& jsFunction.charAt(0) <= '9') {
			String singleArg = arg0 != null ? arg0.toString() : null;
			callListenerNative(this.api, jsFunction, singleArg,
					arg1 == null ? null : arg1.toString());
			return;
		}
		((AppW) app).callAppletJavaScript(jsFunction, arg0, arg1);
	}

	private native void callListenerNative(JavaScriptObject api2,
			String jsFunction, String arg0, String arg1) /*-{
		api2.listeners[jsFunction * 1](arg0, arg1);

	}-*/;

	// TODO - needed for every ggm instance
	private native JavaScriptObject initAppletFunctions(
	        org.geogebra.web.html5.main.GgbAPIW ggbAPI) /*-{

		var ggbApplet = this.@org.geogebra.web.html5.main.ScriptManagerW::ggbApplet;

		//set the reference
		//$doc[ggbApplet] = $wnd[ggbApplet] = {};

		var api = {listeners:[]};
		var getId = function(obj) {
			if (typeof obj == 'string') {
				return obj;
			}
			api.listeners[api.listeners.length]=obj;
			return (api.listeners.length-1)+"";
		}
		api.getXML = function(objName) {
			if (objName) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getXML(Ljava/lang/String;)(objName);
			} else {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getXML()();
			}
		};

		api.getAlgorithmXML = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAlgorithmXML(Ljava/lang/String;)(objName);
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
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(param1, param2);
			} else if (param1) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(false, param1);
			} else {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getBase64()();
			}

		}

		api.setBase64 = function(base64string, callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setBase64(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(base64string, callback);
		}

		api.openFile = function(filename, callback) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::openFile(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(base64string, callback);
		}

		api.getContext2D = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getContext2D()();
		};

		api.login = function(token) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::login(Ljava/lang/String;)(token);
		};

		api.logout = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::logout()();
		};

		api.setXML = function(xml) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setXML(Ljava/lang/String;)(xml);
		};

		api.evalXML = function(xmlString) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalXML(Ljava/lang/String;)(xmlString);
		};

		api.setDisplayStyle = function(objName, style) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setDisplayStyle(Ljava/lang/String;Ljava/lang/String;)(objName, style);
		};

		api.evalCommand = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalCommand(Ljava/lang/String;)(cmdString);
		};

		api.evalCommandGetLabels = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalCommandGetLabels(Ljava/lang/String;)(cmdString);
		};

		api.evalCommandCAS = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalCommandCAS(Ljava/lang/String;)(cmdString);
		};

		api.evalGeoGebraCAS = function(cmdString) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::evalGeoGebraCAS(Ljava/lang/String;)(cmdString);
		};

		api.setFixed = function(objName, flag, selection) {
			if(typeof selection === 'undefined'){
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFixed(Ljava/lang/String;Z)(objName,flag);
			}else{
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFixed(Ljava/lang/String;ZZ)(objName,flag,selection);
			}
		};

		api.setOnTheFlyPointCreationActive = function(flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setOnTheFlyPointCreationActive(Z)(flag);
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
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setErrorDialogsActive(Z)(flag);
		};

		api.reset = function() {//TODO: implement this in Desktop and Web
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::reset()();
		};

		api.refreshViews = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::refreshViews()();
		};

		api.setVisible = function(objName, visible) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setVisible(Ljava/lang/String;Z)(objName,visible);
		};

		api.getVisible = function(objName, view) {
			if (view) {
				return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getVisible(Ljava/lang/String;I)(objName,view);
			}
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getVisible(Ljava/lang/String;)(objName);
		};

		api.setLayer = function(objName, layer) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLayer(Ljava/lang/String;I)(objName,layer);
		};

		api.getLayer = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLayer(Ljava/lang/String;)(objName);
		};

		api.setLayerVisible = function(layer, visible) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLayerVisible(IZ)(layer,visible);
		};

		api.setTrace = function(objName, flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setTrace(Ljava/lang/String;Z)(objName,flag);
		};

		api.setLabelVisible = function(objName, visible) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLabelVisible(Ljava/lang/String;Z)(objName,visible);
		};

		api.setLabelStyle = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLabelStyle(Ljava/lang/String;I)(objName,style);
		};

		api.getLabelStyle = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLabelStyle(Ljava/lang/String;)(objName);
		};

		api.getLabelVisible = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLabelVisible(Ljava/lang/String;)(objName);
		};

		api.setColor = function(objName, red, green, blue) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setColor(Ljava/lang/String;III)(objName,red,green,blue);
		};

		api.setCorner = function(objName, x, y, index) {
			if (!index) {
				index = 1;
			}
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCorner(Ljava/lang/String;DDI)(objName,x,y,index);
		};

		api.setLineStyle = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLineStyle(Ljava/lang/String;I)(objName,style);
		};

		api.setLineThickness = function(objName, thickness) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLineThickness(Ljava/lang/String;I)(objName,thickness);
		};

		api.setPointStyle = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPointStyle(Ljava/lang/String;I)(objName,style);
		};

		api.setPointSize = function(objName, style) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPointSize(Ljava/lang/String;I)(objName,style);
		};

		api.setFilling = function(objName, filling) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFilling(Ljava/lang/String;D)(objName,filling);
		};

		api.getColor = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getColor(Ljava/lang/String;)(objName);
		};

		api.getFilling = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getFilling(Ljava/lang/String;)(objName);
		};

		api.getLineStyle = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLineStyle(Ljava/lang/String;)(objName);
		};

		api.getLineThickness = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLineThickness(Ljava/lang/String;)(objName);
		};

		api.getPointStyle = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPointStyle(Ljava/lang/String;)(objName);
		};

		api.getPointSize = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPointSize(Ljava/lang/String;)(objName);
		};

		api.deleteObject = function(objName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::deleteObject(Ljava/lang/String;)(objName);
		};

		api.setAnimating = function(objName, animate) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAnimating(Ljava/lang/String;Z)(objName,animate);
		};

		api.setAnimationSpeed = function(objName, speed) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAnimationSpeed(Ljava/lang/String;D)(objName,speed);
		};

		api.startAnimation = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::startAnimation()();
		};

		api.stopAnimation = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::stopAnimation()();
		};

		api.hideCursorWhenDragging = function(hideCursorWhenDragging) {//TODO: CSS hacks in GeoGebraWeb
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::hideCursorWhenDragging(Z)(hideCursorWhenDragging);
		};

		api.isAnimationRunning = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isAnimationRunning()();
		};
		
		api.getFrameRate = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getFrameRate()();
		}

		api.renameObject = function(oldName, newName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::renameObject(Ljava/lang/String;Ljava/lang/String;)(oldName,newName);
		};

		api.exists = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exists(Ljava/lang/String;)(objName);
		};

		api.isDefined = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isDefined(Ljava/lang/String;)(objName);
		};

		api.getValueString = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getValueString(Ljava/lang/String;)(objName);
		};

		api.getListValue = function(objName, index) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getListValue(Ljava/lang/String;I)(objName, index);
		};

		api.getDefinitionString = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getDefinitionString(Ljava/lang/String;)(objName);
		};

		api.getLaTeXString = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLaTeXString(Ljava/lang/String;)(objName);
		};

		api.getLaTeXBase64 = function(objName, value) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getLaTeXBase64(Ljava/lang/String;Z)(objName, !!value);
		};

		api.getCommandString = function(objName, localize) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getCommandString(Ljava/lang/String;Z)(objName, typeof localize === 'undefined' ? true : localize);
		};

		api.getCaption = function(objName, subst) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getCaption(Ljava/lang/String;Z)(objName, !!subst);
		};

		api.setCaption = function(objName, caption) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCaption(Ljava/lang/String;Ljava/lang/String;)(objName, caption);
		};

		api.getXcoord = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getXcoord(Ljava/lang/String;)(objName);
		};

		api.getYcoord = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getYcoord(Ljava/lang/String;)(objName);
		};

		api.getZcoord = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getZcoord(Ljava/lang/String;)(objName);
		};

		api.setCoords = function(objName, x, y, z) {
			if (typeof z === 'undefined') {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCoords(Ljava/lang/String;DDD)(objName,x,y);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCoords(Ljava/lang/String;DD)(objName,x,y,z);
			}
		};

		api.getValue = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getValue(Ljava/lang/String;)(objName);
		};

		api.getVersion = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getVersion()();
		};

		api.getScreenshotBase64 = function(callback) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getScreenshotBase64(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
		}

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
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setValue(Ljava/lang/String;D)(objName,x);
		};

		api.setTextValue = function(objName, x) {

			x = x + "";

			if (typeof objName !== "string") {
				// avoid possible strange effects 
				return;
			}
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setTextValue(Ljava/lang/String;Ljava/lang/String;)(objName,x);
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
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setListValue(Ljava/lang/String;DD)(objName,x,y);
		};

		api.setRepaintingActive = function(flag) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setRepaintingActive(Z)(flag);
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
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxesVisible(ZZ)(arg1,arg2);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setAxesVisible(IZZZ)(arg1, arg2, arg3, !!arg4);
			}
		};

		api.getGridVisible = function(view) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getGridVisible(I)(view || 1);
		};

		api.setGridVisible = function(arg1, arg2) {
			if (typeof arg2 === "undefined") {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setGridVisible(Z)(arg1);
			} else {
				ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setGridVisible(IZ)(arg1, arg2);
			}
		};

		api.getAllObjectNames = function() {// deprecated since 3.0
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getAllObjectNames()();
		};

		api.getObjectNumber = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getObjectNumber()();
		};

		api.getObjectName = function(i) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getObjectName(I)(i);
		};

		api.getObjectType = function(objName) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getObjectType(Ljava/lang/String;)(objName);
		};

		api.setMode = function(mode) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setMode(I)(mode);
		};

		api.getMode = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getMode()();
		};

		api.openMaterial = function(material) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::openMaterial(Ljava/lang/String;)(material);
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
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerObjectUpdateListener(Ljava/lang/String;Ljava/lang/String;)(objname, getId(JSFunctionName));
		};

		api.unregisterObjectUpdateListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterObjectUpdateListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.registerObjectClickListener = function(objname, JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerObjectClickListener(Ljava/lang/String;Ljava/lang/String;)(objname, getId(JSFunctionName));
		};

		api.unregisterObjectClickListener = function(objname) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterObjectClickListener(Ljava/lang/String;)(objname);
		};

		api.registerClickListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::registerClickListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.unregisterClickListener = function(JSFunctionName) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::unregisterClickListener(Ljava/lang/String;)(getId(JSFunctionName));
		};

		api.undo = function(repaint) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::undo(Z)(repaint == true);
		};

		api.redo = function(repaint) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::redo(Z)(repaint == true);
		};

		api.newConstruction = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::newConstruction()();
		};

		api.debug = function(str) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::debug(Ljava/lang/String;)(str);
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
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableRightClick(Z)(enable);
		};

		api.enableLabelDrags = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableLabelDrags(Z)(enable);
		};

		api.enableShiftDragZoom = function(enable) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::enableShiftDragZoom(Z)(enable);
		};

		api.showToolBar = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showToolBar(Z)(show);
		};

		api.setCustomToolBar = function(toolbarDef) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setCustomToolBar(Ljava/lang/String;)(toolbarDef);
		};

		api.showMenuBar = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showMenuBar(Z)(show);
		};

		api.showAlgebraInput = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showAlgebraInput(Z)(show);
		};

		api.showResetIcon = function(show) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showResetIcon(Z)(show);
		};

		api.getViewProperties = function(show) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getViewProperties(I)(show);
		};

		api.setFont = function(label, size, bold, italic, serif) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setFont(Ljava/lang/String;IZZZ)(label,size,bold, italic,serif);
		};

		api.insertImage = function(url) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::insertImage(Ljava/lang/String;)(url);
		};

		api.recalculateEnvironments = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::recalculateEnvironments()();
		};

		api.isIndependent = function(label) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isIndependent(Ljava/lang/String;)(label);
		};

		api.isMoveable = function(label) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isMoveable(Ljava/lang/String;)(label);
		};

		api.setPerspective = function(code) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setPerspective(Ljava/lang/String;)(code+"");
		};

		api.getPNGBase64 = function(exportScale, transparent, dpi,
				copyToClipboard) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getPNGBase64(DZDZ)(exportScale, transparent, dpi, copyToClipboard);
		}

		api.getFileJSON = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getFileJSON(Z)(false);
		}

		api.setLanguage = function(lang) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setLanguage(Ljava/lang/String;)(lang);
		}

		api.showTooltip = function(lang) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::showTooltip(Ljava/lang/String;)(lang);
		}

		api.remove = function() {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::removeApplet()();
			$doc[ggbApplet] = $wnd[ggbApplet] = api = null;
		}

		api.getExerciseResult = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getExerciseResult()();
		};

		api.getExerciseFraction = function() {
			return ggbAPI.@org.geogebra.common.plugin.GgbAPI::getExerciseFraction()();
		};

		api.isExercise = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::isExercise()();
		};

		api.startExercise = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::startExercise()();
		};

		api.setExternalPath = function(path) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::setExternalPath(Ljava/lang/String;)(path);
		};

		api.checkSaved = function(path) {
			ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::checkSaved(Lcom/google/gwt/core/client/JavaScriptObject;)(path);
		};

		api.getCASObjectNumber = function(path) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::getCASObjectNumber()();
		};

		api.writePNGtoFile = function(filename, exportScale, transparent, DPI) {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::writePNGtoFile(Ljava/lang/String;DZD)(filename, exportScale, transparent, DPI);
		};

		api.exportPGF = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportPGF()();
		};

		api.exportPSTricks = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportPSTricks()();
		};

		api.exportAsymptote = function() {
			return ggbAPI.@org.geogebra.web.html5.main.GgbAPIW::exportAsymptote()();
		};

		$doc[ggbApplet] = $wnd[ggbApplet] = api;
		return api;
		// other methods from the Wiki (consider to implement here)
		// http://wiki.geogebra.org/en/Reference:JavaScript
		//
		// 
		// boolean writePNGtoFile(String filename, double exportScale, boolean transparent, double DPI)
		// String getIPAddress()
		// String getHostname()
	}-*/;

}
