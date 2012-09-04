package geogebra.web.main;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.plugin.ScriptManagerCommon;

public class ScriptManager extends ScriptManagerCommon {

	public String ggbApplet = "ggbApplet";

	public ScriptManager(App app) {
	    this.app = app;

	    // this should contain alphanumeric characters only,
	    // but it is not checked otherwise
	    ggbApplet = ((AppW)app).getArticleElement().getDataParamId();

	    initAppletFunctions((geogebra.web.main.GgbAPI)(app.getGgbApi()));
    }

	@Override
	public void ggbOnInit() {
		
		if (app.useBrowserForJavaScript()) {
		
			String param = ((AppW)app).getArticleElement().getDataParamId();
			if (param == null || "".equals(param)) {
				AppW.ggbOnInit();
			} else {
				AppW.ggbOnInit(param);
			}
		
		} else {
			// call only if libraryJavaScript is not the default (ie do nothing)
			if (!app.getKernel().getLibraryJavaScript().equals(Kernel.defaultLibraryJavaScript))
				app.evalJavaScript(app,"ggbOnInit();"+app.getKernel().getLibraryJavaScript(), null);			
			
		}
	}

	@Override
    public void callJavaScript(String jsFunction, Object[] args) {
	    app.callAppletJavaScript(jsFunction, args);	    
    }
	
	// TODO - needed for every ggm instance
	private native void initAppletFunctions(
			geogebra.web.main.GgbAPI ggbAPI) /*-{

		var ggbApplet = this.@geogebra.web.main.ScriptManager::ggbApplet;

		//set the reference
		$doc[ggbApplet] = $wnd[ggbApplet] = {};


		$wnd[ggbApplet].getXML = function(objName) {
			if (objName) {
				return ggbAPI.@geogebra.web.main.GgbAPI::getXML(Ljava/lang/String;)(objName);
			} else {
				return ggbAPI.@geogebra.web.main.GgbAPI::getXML()();
			}
		};

		$wnd[ggbApplet].getAlgorithmXML = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getAlgorithmXML(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getBase64 = function(callback) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getBase64(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
		}

		$wnd[ggbApplet].getContext2D = function() {
			return ggbAPI.@geogebra.web.main.GgbAPI::getContext2D()();
		};

		$wnd[ggbApplet].setXML = function(xml) {
			ggbAPI.@geogebra.web.main.GgbAPI::setXML(Ljava/lang/String;)(xml);
		};

		$wnd[ggbApplet].evalXML = function(xmlString) {
			ggbAPI.@geogebra.web.main.GgbAPI::evalXML(Ljava/lang/String;)(xmlString);
		};

		$wnd[ggbApplet].evalCommand = function(cmdString) {
			return ggbAPI.@geogebra.web.main.GgbAPI::evalCommand(Ljava/lang/String;)(cmdString);
		};

		$wnd[ggbApplet].setFixed = function(objName, flag) {
			ggbAPI.@geogebra.web.main.GgbAPI::setFixed(Ljava/lang/String;Z)(objName,flag);
		};

		$wnd[ggbApplet].setOnTheFlyPointCreationActive = function(flag) {
			ggbAPI.@geogebra.web.main.GgbAPI::setOnTheFlyPointCreationActive(Z)(flag);
		};

		$wnd[ggbApplet].setUndoPoint = function() {
			ggbAPI.@geogebra.web.main.GgbAPI::setUndoPoint()();
		};

		$wnd[ggbApplet].initCAS = function() {
			ggbAPI.@geogebra.web.main.GgbAPI::initCAS()();
		};
		
		$wnd[ggbApplet].uploadToGeoGebraTube = function() {
			ggbAPI.@geogebra.web.main.GgbAPI::uploadToGeoGebraTube()();
		};
		
		

		// This is not yet used in GeoGebraWeb
		//$wnd[ggbApplet].setErrorDialogsActive = function(flag) {
		//	ggbAPI.@geogebra.web.main.GgbAPI::setErrorDialogsActive(Z)(flag);
		//};

		$wnd[ggbApplet].reset = function() {//TODO: implement this in Desktop and Web
			ggbAPI.@geogebra.web.main.GgbAPI::reset()();
		};

		$wnd[ggbApplet].refreshViews = function() {
			ggbAPI.@geogebra.web.main.GgbAPI::refreshViews()();
		};

		$wnd[ggbApplet].setVisible = function(objName, visible) {
			ggbAPI.@geogebra.web.main.GgbAPI::setVisible(Ljava/lang/String;Z)(objName,visible);
		};

		$wnd[ggbApplet].getVisible = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getVisible(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setLayer = function(objName, layer) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLayer(Ljava/lang/String;I)(objName,layer);
		};

		$wnd[ggbApplet].getLayer = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getLayer(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setLayerVisible = function(layer, visible) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLayerVisible(IZ)(layer,visible);
		};

		$wnd[ggbApplet].setTrace = function(objName, flag) {
			ggbAPI.@geogebra.web.main.GgbAPI::setTrace(Ljava/lang/String;Z)(objName,flag);
		};

		$wnd[ggbApplet].setLabelVisible = function(objName, visible) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLabelVisible(Ljava/lang/String;Z)(objName,visible);
		};

		$wnd[ggbApplet].setLabelStyle = function(objName, style) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLabelStyle(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setLabelMode = function(objName, visible) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLabelMode(Ljava/lang/String;Z)(objName,visible);
		};

		$wnd[ggbApplet].setColor = function(objName, red, green, blue) {
			ggbAPI.@geogebra.web.main.GgbAPI::setColor(Ljava/lang/String;III)(objName,red,green,blue);
		};

		$wnd[ggbApplet].setLineStyle = function(objName, style) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLineStyle(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setLineThickness = function(objName, thickness) {
			ggbAPI.@geogebra.web.main.GgbAPI::setLineThickness(Ljava/lang/String;I)(objName,thickness);
		};

		$wnd[ggbApplet].setPointStyle = function(objName, style) {
			ggbAPI.@geogebra.web.main.GgbAPI::setPointStyle(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setPointSize = function(objName, style) {
			ggbAPI.@geogebra.web.main.GgbAPI::setPointSize(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setFilling = function(objName, filling) {
			ggbAPI.@geogebra.web.main.GgbAPI::setFilling(Ljava/lang/String;D)(objName,filling);
		};

		$wnd[ggbApplet].getColor = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getColor(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getFilling = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getFilling(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getLineStyle = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getLineStyle(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getLineThickness = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getLineThickness(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getPointStyle = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getPointStyle(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getPointSize = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getPointSize(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].deleteObject = function(objName) {
			ggbAPI.@geogebra.web.main.GgbAPI::deleteObject(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setAnimating = function(objName, animate) {
			ggbAPI.@geogebra.web.main.GgbAPI::setAnimating(Ljava/lang/String;Z)(objName,animate);
		};

		$wnd[ggbApplet].setAnimationSpeed = function(objName, speed) {
			ggbAPI.@geogebra.web.main.GgbAPI::setAnimationSpeed(Ljava/lang/String;D)(objName,speed);
		};

		$wnd[ggbApplet].startAnimation = function() {
			ggbAPI.@geogebra.web.main.GgbAPI::startAnimation()();
		};

		$wnd[ggbApplet].stopAnimation = function() {
			ggbAPI.@geogebra.web.main.GgbAPI::stopAnimation()();
		};

		$wnd[ggbApplet].hideCursorWhenDragging = function(hideCursorWhenDragging) {//TODO: CSS hacks in GeoGebraWeb
			ggbAPI.@geogebra.web.main.GgbAPI::hideCursorWhenDragging(Z)(hideCursorWhenDragging);
		};

		$wnd[ggbApplet].isAnimationRunning = function() {
			return ggbAPI.@geogebra.web.main.GgbAPI::isAnimationRunning()();
		};

		$wnd[ggbApplet].renameObject = function(oldName, newName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::renameObject(Ljava/lang/String;Ljava/lang/String;)(oldName,newName);
		};

		$wnd[ggbApplet].exists = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::exists(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].isDefined = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::isDefined(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getValueString = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getValueString(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getDefinitionString = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getDefinitionString(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getCommandString = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getCommandString(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getXcoord = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getXcoord(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getYcoord = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getYcoord(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setCoords = function(objName, x, y) {
			ggbAPI.@geogebra.web.main.GgbAPI::setCoords(Ljava/lang/String;DD)(objName,x,y);
		};

		$wnd[ggbApplet].getValue = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getValue(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setValue = function(objName, x) {
			ggbAPI.@geogebra.web.main.GgbAPI::setValue(Ljava/lang/String;D)(objName,x);
		};

		$wnd[ggbApplet].setRepaintingActive = function(flag) {
			ggbAPI.@geogebra.web.main.GgbAPI::setRepaintingActive(Z)(flag);
		};

		$wnd[ggbApplet].setCoordSystem = function(xmin, xmax, ymin, ymax) {
			ggbAPI.@geogebra.web.main.GgbAPI::setCoordSystem(DDDD)(xmin,xmax,ymin,ymax);
		};

		$wnd[ggbApplet].setAxesVisible = function(xVisible, yVisible) {
			ggbAPI.@geogebra.web.main.GgbAPI::setAxesVisible(ZZ)(xVisible,yVisible);
		};

		$wnd[ggbApplet].setGridVisible = function(flag) {
			ggbAPI.@geogebra.web.main.GgbAPI::setGridVisible(Z)(flag);
		};

		$wnd[ggbApplet].getAllObjectNames = function() {// deprecated since 3.0
			return ggbAPI.@geogebra.web.main.GgbAPI::getAllObjectNames()();
		};

		$wnd[ggbApplet].getObjectNumber = function() {
			return ggbAPI.@geogebra.web.main.GgbAPI::getObjectNumber()();
		};

		$wnd[ggbApplet].getObjectName = function(i) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getObjectName(I)(i);
		};

		$wnd[ggbApplet].getObjectType = function(objName) {
			return ggbAPI.@geogebra.web.main.GgbAPI::getObjectType(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setMode = function(mode) {
			ggbAPI.@geogebra.web.main.GgbAPI::setMode(I)(mode);
		};

		// not supported by GgbAPI Desktop,Web
		//$wnd[ggbApplet].callJavaScript = function(jsFunction, args) {
		//	ggbAPI.@geogebra.web.main.GgbAPI::callJavaScript(Ljava/lang/String;Ljava/lang/String;)(jsFunction,args);
		//};

		$wnd[ggbApplet].registerAddListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerAddListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterAddListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::unregisterAddListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerRemoveListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerRemoveListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterRemoveListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::unregisterRemoveListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerClearListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerClearListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterClearListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::unregisterClearListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerRenameListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerRenameListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterRenameListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerRenameListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::unregisterUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerObjectUpdateListener = function(objname, JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::registerObjectUpdateListener(Ljava/lang/String;Ljava/lang/String;)(objname, JSFunctionName);
		};

		$wnd[ggbApplet].unregisterObjectUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.web.main.GgbAPI::unregisterObjectUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};
		
		$doc[ggbApplet] = $wnd[ggbApplet];

		// other methods from the Wiki (consider to implement here)
		// http://wiki.geogebra.org/en/Reference:JavaScript
		//
		// String getPNGBase64(double exportScale, boolean transparent, double DPI)
		// boolean writePNGtoFile(String filename, double exportScale, boolean transparent, double DPI)
		// boolean isIndependent(String objName)
		// boolean isMoveable(String objName)
		// String getBase64()
		// void setBase64(String)
		// void openFile(String strURL)
		// String evalMathPiper(String string) // evalGeoGebraCAS(String string)
		// String getIPAddress()
		// String getHostname()
		// void debug(String string)
	}-*/;

}
