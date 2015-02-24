package geogebra.html5.main;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.plugin.ScriptManager;

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
			App.debug("almost there" + app.useBrowserForJavaScript());
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
			App.debug(t.getMessage());
		}
		// set this to run always
		String articleid = ((AppW) app).getArticleId();
		if (articleid != null) {
			AppW.appletOnLoad(articleid);
		}
		if (((AppW) app).getGeoGebraFrame() != null
		        && ((AppW) app).getGeoGebraFrame().onLoadCallback != null) {
			runCallback(((AppW) app).getGeoGebraFrame().onLoadCallback);
		}
	}

	@Override
	public void callJavaScript(String jsFunction, Object[] args) {
		app.callAppletJavaScript(jsFunction, args);
	}

	// TODO - needed for every ggm instance
	private native JavaScriptObject initAppletFunctions(
	        geogebra.html5.main.GgbAPIW ggbAPI) /*-{

		var ggbApplet = this.@geogebra.html5.main.ScriptManagerW::ggbApplet;

		//set the reference
		//$doc[ggbApplet] = $wnd[ggbApplet] = {};

		var api = {};

		api.getXML = function(objName) {
			if (objName) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getXML(Ljava/lang/String;)(objName);
			} else {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getXML()();
			}
		};

		api.getAlgorithmXML = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getAlgorithmXML(Ljava/lang/String;)(objName);
		};

		api.getBase64 = function(param1, param2) {
			if (param2 === false) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(Z)(false);
			}
			if (param2 === true) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(Z)(true);
			}
			if (param2) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(param1, param2);
			} else if (param1) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(false, param1);
			} else {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64()();
			}

		}

		api.setBase64 = function(base64string) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::setBase64(Ljava/lang/String;)(base64string);
		}

		api.getContext2D = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getContext2D()();
		};

		api.login = function(token) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::login(Ljava/lang/String;)(token);
		};

		api.logout = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::logout()();
		};

		api.setXML = function(xml) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setXML(Ljava/lang/String;)(xml);
		};

		api.evalXML = function(xmlString) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::evalXML(Ljava/lang/String;)(xmlString);
		};

		api.evalCommand = function(cmdString) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::evalCommand(Ljava/lang/String;)(cmdString);
		};

		api.evalCommandCAS = function(cmdString) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::evalCommandCAS(Ljava/lang/String;)(cmdString);
		};

		api.evalGeoGebraCAS = function(cmdString) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::evalGeoGebraCAS(Ljava/lang/String;)(cmdString);
		};

		api.setFixed = function(objName, flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setFixed(Ljava/lang/String;Z)(objName,flag);
		};

		api.setOnTheFlyPointCreationActive = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setOnTheFlyPointCreationActive(Z)(flag);
		};

		api.setUndoPoint = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setUndoPoint()();
		};

		api.setSaved = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setSaved()();
		};

		api.initCAS = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::initCAS()();
		};

		api.uploadToGeoGebraTube = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::uploadToGeoGebraTube()();
		};

		api.setErrorDialogsActive = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setErrorDialogsActive(Z)(flag);
		};

		api.reset = function() {//TODO: implement this in Desktop and Web
			ggbAPI.@geogebra.html5.main.GgbAPIW::reset()();
		};

		api.refreshViews = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::refreshViews()();
		};

		api.setVisible = function(objName, visible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setVisible(Ljava/lang/String;Z)(objName,visible);
		};

		api.getVisible = function(objName, view) {
			if (view) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getVisible(Ljava/lang/String;I)(objName,view);
			}
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getVisible(Ljava/lang/String;)(objName);
		};

		api.setLayer = function(objName, layer) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLayer(Ljava/lang/String;I)(objName,layer);
		};

		api.getLayer = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getLayer(Ljava/lang/String;)(objName);
		};

		api.setLayerVisible = function(layer, visible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLayerVisible(IZ)(layer,visible);
		};

		api.setTrace = function(objName, flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setTrace(Ljava/lang/String;Z)(objName,flag);
		};

		api.setLabelVisible = function(objName, visible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLabelVisible(Ljava/lang/String;Z)(objName,visible);
		};

		api.setLabelStyle = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLabelStyle(Ljava/lang/String;I)(objName,style);
		};

		api.setColor = function(objName, red, green, blue) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setColor(Ljava/lang/String;III)(objName,red,green,blue);
		};

		api.setCorner = function(objName, x, y, index) {
			if (!index) {
				index = 1;
			}
			ggbAPI.@geogebra.html5.main.GgbAPIW::setCorner(Ljava/lang/String;DDI)(objName,x,y,index);
		};

		api.setLineStyle = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLineStyle(Ljava/lang/String;I)(objName,style);
		};

		api.setLineThickness = function(objName, thickness) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLineThickness(Ljava/lang/String;I)(objName,thickness);
		};

		api.setPointStyle = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setPointStyle(Ljava/lang/String;I)(objName,style);
		};

		api.setPointSize = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setPointSize(Ljava/lang/String;I)(objName,style);
		};

		api.setFilling = function(objName, filling) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setFilling(Ljava/lang/String;D)(objName,filling);
		};

		api.getColor = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getColor(Ljava/lang/String;)(objName);
		};

		api.getFilling = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getFilling(Ljava/lang/String;)(objName);
		};

		api.getLineStyle = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getLineStyle(Ljava/lang/String;)(objName);
		};

		api.getLineThickness = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getLineThickness(Ljava/lang/String;)(objName);
		};

		api.getPointStyle = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getPointStyle(Ljava/lang/String;)(objName);
		};

		api.getPointSize = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getPointSize(Ljava/lang/String;)(objName);
		};

		api.deleteObject = function(objName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::deleteObject(Ljava/lang/String;)(objName);
		};

		api.setAnimating = function(objName, animate) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setAnimating(Ljava/lang/String;Z)(objName,animate);
		};

		api.setAnimationSpeed = function(objName, speed) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setAnimationSpeed(Ljava/lang/String;D)(objName,speed);
		};

		api.startAnimation = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::startAnimation()();
		};

		api.stopAnimation = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::stopAnimation()();
		};

		api.hideCursorWhenDragging = function(hideCursorWhenDragging) {//TODO: CSS hacks in GeoGebraWeb
			ggbAPI.@geogebra.html5.main.GgbAPIW::hideCursorWhenDragging(Z)(hideCursorWhenDragging);
		};

		api.isAnimationRunning = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::isAnimationRunning()();
		};

		api.renameObject = function(oldName, newName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::renameObject(Ljava/lang/String;Ljava/lang/String;)(oldName,newName);
		};

		api.exists = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::exists(Ljava/lang/String;)(objName);
		};

		api.isDefined = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::isDefined(Ljava/lang/String;)(objName);
		};

		api.getValueString = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getValueString(Ljava/lang/String;)(objName);
		};

		api.getListValue = function(objName, index) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getListValue(Ljava/lang/String;I)(objName, index);
		};

		api.getDefinitionString = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getDefinitionString(Ljava/lang/String;)(objName);
		};

		api.getCommandString = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getCommandString(Ljava/lang/String;)(objName);
		};

		api.getXcoord = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getXcoord(Ljava/lang/String;)(objName);
		};

		api.getYcoord = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getYcoord(Ljava/lang/String;)(objName);
		};

		api.setCoords = function(objName, x, y) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setCoords(Ljava/lang/String;DD)(objName,x,y);
		};

		api.getValue = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getValue(Ljava/lang/String;)(objName);
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
			ggbAPI.@geogebra.html5.main.GgbAPIW::setValue(Ljava/lang/String;D)(objName,x);
		};

		api.setTextValue = function(objName, x) {

			x = x + "";

			if (typeof objName !== "string") {
				// avoid possible strange effects 
				return;
			}
			ggbAPI.@geogebra.html5.main.GgbAPIW::setTextValue(Ljava/lang/String;Ljava/lang/String;)(objName,x);
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
			ggbAPI.@geogebra.html5.main.GgbAPIW::setListValue(Ljava/lang/String;DD)(objName,x,y);
		};

		api.setRepaintingActive = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setRepaintingActive(Z)(flag);
		};

		api.setCoordSystem = function(xmin, xmax, ymin, ymax) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setCoordSystem(DDDD)(xmin,xmax,ymin,ymax);
		};

		api.setAxesVisible = function(xVisible, yVisible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setAxesVisible(ZZ)(xVisible,yVisible);
		};

		api.setGridVisible = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setGridVisible(Z)(flag);
		};

		api.getAllObjectNames = function() {// deprecated since 3.0
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getAllObjectNames()();
		};

		api.getObjectNumber = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getObjectNumber()();
		};

		api.getObjectName = function(i) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getObjectName(I)(i);
		};

		api.getObjectType = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getObjectType(Ljava/lang/String;)(objName);
		};

		api.setMode = function(mode) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setMode(I)(mode);
		};

		api.openMaterial = function(material) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::openMaterial(Ljava/lang/String;)(material);
		};

		// not supported by GgbAPI Desktop,Web
		//$wnd[ggbApplet].callJavaScript = function(jsFunction, args) {
		//	ggbAPI.@geogebra.html5.main.GgbAPIW::callJavaScript(Ljava/lang/String;Ljava/lang/String;)(jsFunction,args);
		//};

		api.registerAddListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerAddListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerStoreUndoListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerStoreUndoListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.unregisterAddListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterAddListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerRemoveListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerRemoveListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.unregisterRemoveListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterRemoveListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerClearListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerClearListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.unregisterClearListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterClearListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerRenameListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerRenameListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.unregisterRenameListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerRenameListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.unregisterUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerClientListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerClientListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.unregisterClientListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterClientListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.registerObjectUpdateListener = function(objname, JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerObjectUpdateListener(Ljava/lang/String;Ljava/lang/String;)(objname, JSFunctionName);
		};

		api.unregisterObjectUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterObjectUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};

		api.undo = function(repaint) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::undo(Z)(repaint == true);
		};

		api.redo = function(repaint) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::redo(Z)(repaint == true);
		};

		api.newConstruction = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::newConstruction()();
		};

		api.debug = function(str) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::debug(Ljava/lang/String;)(str);
		};

		api.startEditing = function(str) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::startEditing()();
		};

		api.setWidth = function(width) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setWidth(I)(width);
		};

		api.setHeight = function(height) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setHeight(I)(height);
		};

		api.setSize = function(width, height) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setSize(II)(width, height);
		};

		api.enableRightClick = function(enable) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::enableRightClick(Z)(enable);
		};

		api.enableLabelDrags = function(enable) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::enableLabelDrags(Z)(enable);
		};

		api.enableShiftDragZoom = function(enable) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::enableShiftDragZoom(Z)(enable);
		};

		api.showToolBar = function(show) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::showToolBar(Z)(show);
		};

		api.showMenuBar = function(show) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::showMenuBar(Z)(show);
		};

		api.showAlgebraInput = function(show) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::showAlgebraInput(Z)(show);
		};

		api.showResetIcon = function(show) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::showResetIcon(Z)(show);
		};

		api.getViewProperties = function(show) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getViewProperties(I)(show);
		};

		api.setFont = function(label, size, bold, italic, serif) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setFont(Ljava/lang/String;IZZZ)(label,size,bold, italic,serif);
		};

		api.insertImage = function(url) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::insertImage(Ljava/lang/String;)(url);
		};

		api.recalculateEnvironments = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::recalculateEnvironments()();
		};

		api.isIndependent = function(label) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::isIndependent(Ljava/lang/String;)(label);
		};

		api.isMoveable = function(label) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::isMoveable(Ljava/lang/String;)(label);
		};

		api.setPerspective = function(code) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setPerspective(Ljava/lang/String;)(code);
		};

		api.getPNGBase64 = function(exportScale, transparent, dpi) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getPNGBase64(DZD)(exportScale,transparent,dpi);
		}

		api.getFileJSON = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getFileJSON(Z)(false);
		}

		api.setLanguage = function(lang) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::setLanguage(Ljava/lang/String;)(lang);
		}

		api.remove = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::removeApplet()();
			$doc[ggbApplet] = $wnd[ggbApplet] = api = null;
		}

		$doc[ggbApplet] = $wnd[ggbApplet] = api;
		return api;
		// other methods from the Wiki (consider to implement here)
		// http://wiki.geogebra.org/en/Reference:JavaScript
		//
		// 
		// boolean writePNGtoFile(String filename, double exportScale, boolean transparent, double DPI)
		// void openFile(String strURL)
		// String getIPAddress()
		// String getHostname()
	}-*/;

}
