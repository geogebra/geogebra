package geogebra.html5.main;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.plugin.ScriptManager;

/**
 * Provides JavaScript scripting for objects and initializes the public API.
 */
public class ScriptManagerW extends ScriptManager {

	private String ggbApplet = AppWeb.DEFAULT_APPLET_ID;

	/**
	 * @param app application
	 */
	public ScriptManagerW(AppWeb app) {
		super(app);

	    // this should contain alphanumeric characters only,
	    // but it is not checked otherwise
	    ggbApplet = app.getDataParamId();

	    initAppletFunctions(app.getGgbApi());
    }


	@Override
	public void ggbOnInit() {
		try{
			if (app.useBrowserForJavaScript()) {
			
				String param = ((AppWeb)app).getDataParamId();
				if (param == null || "".equals(param)) {
					AppWeb.ggbOnInit();
				} else {
					AppWeb.ggbOnInit(param);
				}
				
			
			} else {
				// call only if libraryJavaScript is not the default (ie do nothing)
				if (!app.getKernel().getLibraryJavaScript().equals(Kernel.defaultLibraryJavaScript))
					app.evalJavaScript(app,"ggbOnInit();"+app.getKernel().getLibraryJavaScript(), null);			
				
			}
			
		}catch(Throwable t){
			App.debug(t.getMessage());
		}
		//set this to run always
		String articleid = ((AppWeb) app).getArticleId();
		if (articleid != null) {
			AppWeb.appletOnLoad(articleid);
		}
	}
	
	

	@Override
    public void callJavaScript(String jsFunction, Object[] args) {
	    app.callAppletJavaScript(jsFunction, args);	    
    }
	
	// TODO - needed for every ggm instance
	private native void initAppletFunctions(
			geogebra.html5.main.GgbAPIW ggbAPI) /*-{

		var ggbApplet = this.@geogebra.html5.main.ScriptManagerW::ggbApplet;

		//set the reference
		$doc[ggbApplet] = $wnd[ggbApplet] = {};


		$wnd[ggbApplet].getXML = function(objName) {
			if (objName) {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getXML(Ljava/lang/String;)(objName);
			} else {
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getXML()();
			}
		};

		$wnd[ggbApplet].getAlgorithmXML = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getAlgorithmXML(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getBase64 = function(param1, param2) {
			if(param2 === false){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(Z)(false);
			}
			if(param2 === true){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(Z)(true);
			}
			if(param2){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(param1, param2);
			}else if(param1){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64(ZLcom/google/gwt/core/client/JavaScriptObject;)(false, param1);
			}else{
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getBase64()();
			}
			
		}
		
		$wnd[ggbApplet].setBase64 = function(base64string) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::setBase64(Ljava/lang/String;)(base64string);
		}

		$wnd[ggbApplet].getContext2D = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getContext2D()();
		};

		$wnd[ggbApplet].login = function(xml) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::login(Ljava/lang/String;)(xml);
		};

		$wnd[ggbApplet].setXML = function(xml) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setXML(Ljava/lang/String;)(xml);
		};

		$wnd[ggbApplet].evalXML = function(xmlString) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::evalXML(Ljava/lang/String;)(xmlString);
		};

		$wnd[ggbApplet].evalCommand = function(cmdString) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::evalCommand(Ljava/lang/String;)(cmdString);
		};

		$wnd[ggbApplet].evalCommandCAS = function(cmdString) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::evalCommandCAS(Ljava/lang/String;)(cmdString);
		};
		
		$wnd[ggbApplet].evalGeoGebraCAS = function(cmdString) { 
	 		return ggbAPI.@geogebra.html5.main.GgbAPIW::evalGeoGebraCAS(Ljava/lang/String;)(cmdString); 
	 	};

		$wnd[ggbApplet].setFixed = function(objName, flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setFixed(Ljava/lang/String;Z)(objName,flag);
		};

		$wnd[ggbApplet].setOnTheFlyPointCreationActive = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setOnTheFlyPointCreationActive(Z)(flag);
		};

		$wnd[ggbApplet].setUndoPoint = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setUndoPoint()();
		};
		
		$wnd[ggbApplet].setSaved = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setSaved()();
		};

		$wnd[ggbApplet].initCAS = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::initCAS()();
		};
		
		$wnd[ggbApplet].uploadToGeoGebraTube = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::uploadToGeoGebraTube()();
		};
		
		

		// This is not yet used in GeoGebraWeb
		//$wnd[ggbApplet].setErrorDialogsActive = function(flag) {
		//	ggbAPI.@geogebra.html5.main.GgbAPIW::setErrorDialogsActive(Z)(flag);
		//};

		$wnd[ggbApplet].reset = function() {//TODO: implement this in Desktop and Web
			ggbAPI.@geogebra.html5.main.GgbAPIW::reset()();
		};

		$wnd[ggbApplet].refreshViews = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::refreshViews()();
		};

		$wnd[ggbApplet].setVisible = function(objName, visible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setVisible(Ljava/lang/String;Z)(objName,visible);
		};

		$wnd[ggbApplet].getVisible = function(objName, view) {
			if(view){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getVisible(Ljava/lang/String;I)(objName,view);
			}
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getVisible(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setLayer = function(objName, layer) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLayer(Ljava/lang/String;I)(objName,layer);
		};

		$wnd[ggbApplet].getLayer = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getLayer(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setLayerVisible = function(layer, visible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLayerVisible(IZ)(layer,visible);
		};

		$wnd[ggbApplet].setTrace = function(objName, flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setTrace(Ljava/lang/String;Z)(objName,flag);
		};

		$wnd[ggbApplet].setLabelVisible = function(objName, visible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLabelVisible(Ljava/lang/String;Z)(objName,visible);
		};

		$wnd[ggbApplet].setLabelStyle = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLabelStyle(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setColor = function(objName, red, green, blue) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setColor(Ljava/lang/String;III)(objName,red,green,blue);
		};

		$wnd[ggbApplet].setCorner = function(objName, x, y, index) {
			if(!index){
				index = 1;
			}
			ggbAPI.@geogebra.html5.main.GgbAPIW::setCorner(Ljava/lang/String;DDI)(objName,x,y,index);
		};

		$wnd[ggbApplet].setLineStyle = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLineStyle(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setLineThickness = function(objName, thickness) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setLineThickness(Ljava/lang/String;I)(objName,thickness);
		};

		$wnd[ggbApplet].setPointStyle = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setPointStyle(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setPointSize = function(objName, style) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setPointSize(Ljava/lang/String;I)(objName,style);
		};

		$wnd[ggbApplet].setFilling = function(objName, filling) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setFilling(Ljava/lang/String;D)(objName,filling);
		};

		$wnd[ggbApplet].getColor = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getColor(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getFilling = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getFilling(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getLineStyle = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getLineStyle(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getLineThickness = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getLineThickness(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getPointStyle = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getPointStyle(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getPointSize = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getPointSize(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].deleteObject = function(objName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::deleteObject(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setAnimating = function(objName, animate) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setAnimating(Ljava/lang/String;Z)(objName,animate);
		};

		$wnd[ggbApplet].setAnimationSpeed = function(objName, speed) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setAnimationSpeed(Ljava/lang/String;D)(objName,speed);
		};

		$wnd[ggbApplet].startAnimation = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::startAnimation()();
		};

		$wnd[ggbApplet].stopAnimation = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::stopAnimation()();
		};

		$wnd[ggbApplet].hideCursorWhenDragging = function(hideCursorWhenDragging) {//TODO: CSS hacks in GeoGebraWeb
			ggbAPI.@geogebra.html5.main.GgbAPIW::hideCursorWhenDragging(Z)(hideCursorWhenDragging);
		};

		$wnd[ggbApplet].isAnimationRunning = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::isAnimationRunning()();
		};

		$wnd[ggbApplet].renameObject = function(oldName, newName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::renameObject(Ljava/lang/String;Ljava/lang/String;)(oldName,newName);
		};

		$wnd[ggbApplet].exists = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::exists(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].isDefined = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::isDefined(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getValueString = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getValueString(Ljava/lang/String;)(objName);
		};
		
		$wnd[ggbApplet].getListValue = function(objName, index) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getListValue(Ljava/lang/String;I)(objName, index);
		};
		
		$wnd[ggbApplet].setListValue = function(objName, index, value) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::setListValue(Ljava/lang/String;ID)(objName, index, value);
		};

		$wnd[ggbApplet].getDefinitionString = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getDefinitionString(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getCommandString = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getCommandString(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getXcoord = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getXcoord(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].getYcoord = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getYcoord(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setCoords = function(objName, x, y) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setCoords(Ljava/lang/String;DD)(objName,x,y);
		};

		$wnd[ggbApplet].getValue = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getValue(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setValue = function(objName, x) {
			// #4035 
 		    // need to support possible syntax error 
 		    // eg setValue("a","3") rather than setValue("a",3) 
 		    if (typeof x === "string") { 
 		    	if (x ===  "true") { 
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

		$wnd[ggbApplet].setRepaintingActive = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setRepaintingActive(Z)(flag);
		};

		$wnd[ggbApplet].setCoordSystem = function(xmin, xmax, ymin, ymax) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setCoordSystem(DDDD)(xmin,xmax,ymin,ymax);
		};

		$wnd[ggbApplet].setAxesVisible = function(xVisible, yVisible) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setAxesVisible(ZZ)(xVisible,yVisible);
		};

		$wnd[ggbApplet].setGridVisible = function(flag) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setGridVisible(Z)(flag);
		};

		$wnd[ggbApplet].getAllObjectNames = function() {// deprecated since 3.0
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getAllObjectNames()();
		};

		$wnd[ggbApplet].getObjectNumber = function() {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getObjectNumber()();
		};

		$wnd[ggbApplet].getObjectName = function(i) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getObjectName(I)(i);
		};

		$wnd[ggbApplet].getObjectType = function(objName) {
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getObjectType(Ljava/lang/String;)(objName);
		};

		$wnd[ggbApplet].setMode = function(mode) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::setMode(I)(mode);
		};
		
		$wnd[ggbApplet].openMaterial = function(material) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::openMaterial(Ljava/lang/String;)(material);
		};
		
		$wnd[ggbApplet].ensureEditing = function() {
			ggbAPI.@geogebra.html5.main.GgbAPIW::ensureEditing()();
		};

		// not supported by GgbAPI Desktop,Web
		//$wnd[ggbApplet].callJavaScript = function(jsFunction, args) {
		//	ggbAPI.@geogebra.html5.main.GgbAPIW::callJavaScript(Ljava/lang/String;Ljava/lang/String;)(jsFunction,args);
		//};

		$wnd[ggbApplet].registerAddListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerAddListener(Ljava/lang/String;)(JSFunctionName);
		};
		
		$wnd[ggbApplet].registerStoreUndoListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerStoreUndoListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterAddListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterAddListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerRemoveListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerRemoveListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterRemoveListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterRemoveListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerClearListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerClearListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterClearListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterClearListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerRenameListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerRenameListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterRenameListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerRenameListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].unregisterUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};
		
		$wnd[ggbApplet].registerClientListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerClientListener(Ljava/lang/String;)(JSFunctionName);
		};
		
		$wnd[ggbApplet].unregisterClientListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterClientListener(Ljava/lang/String;)(JSFunctionName);
		};

		$wnd[ggbApplet].registerObjectUpdateListener = function(objname, JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::registerObjectUpdateListener(Ljava/lang/String;Ljava/lang/String;)(objname, JSFunctionName);
		};

		$wnd[ggbApplet].unregisterObjectUpdateListener = function(JSFunctionName) {
			ggbAPI.@geogebra.html5.main.GgbAPIW::unregisterObjectUpdateListener(Ljava/lang/String;)(JSFunctionName);
		};
		
		$wnd[ggbApplet].undo = function(repaint) {
				ggbAPI.@geogebra.html5.main.GgbAPIW::undo(Z)(repaint == true);
		};
		
		$wnd[ggbApplet].redo = function(repaint) {
				ggbAPI.@geogebra.html5.main.GgbAPIW::redo(Z)(repaint == true);
		};
		
		$wnd[ggbApplet].newConstruction = function(){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::newConstruction()();
		};
		
		$wnd[ggbApplet].debug = function(str){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::debug(Ljava/lang/String;)(str);
		};
		
		$wnd[ggbApplet].startEditing = function(str){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::startEditing()();
		};
		
		$wnd[ggbApplet].setWidth = function(width){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::setWidth(I)(width);
		};
		
		$wnd[ggbApplet].setHeight = function(height){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::setHeight(I)(height);
		};
		
		$wnd[ggbApplet].setSize = function(width, height){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::setSize(II)(width, height);
		};
		
		$wnd[ggbApplet].enableRightClick = function(enable){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::enableRightClick(Z)(enable);
		};
		
		$wnd[ggbApplet].enableLabelDrags = function(enable){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::enableLabelDrags(Z)(enable);
		};
		
		$wnd[ggbApplet].enableShiftDragZoom = function(enable){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::enableShiftDragZoom(Z)(enable);
		};
		
		$wnd[ggbApplet].showToolBar = function(show){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::showToolBar(Z)(show);
		};
		
		$wnd[ggbApplet].showMenuBar = function(show){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::showMenuBar(Z)(show);
		};
		
		$wnd[ggbApplet].showAlgebraInput = function(show){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::showAlgebraInput(Z)(show);
		};
		
		$wnd[ggbApplet].showResetIcon = function(show){ 
				ggbAPI.@geogebra.html5.main.GgbAPIW::showResetIcon(Z)(show);
		};
		
		$wnd[ggbApplet].getViewProperties = function(show){ 
				return ggbAPI.@geogebra.html5.main.GgbAPIW::getViewProperties(I)(show);
		};
		
		$wnd[ggbApplet].setFont = function(label, size, bold, italic, serif){
				ggbAPI.@geogebra.html5.main.GgbAPIW::setFont(Ljava/lang/String;IZZZ)(label,size,bold, italic,serif);
		};
		
		$wnd[ggbApplet].insertImage = function(url){
				ggbAPI.@geogebra.html5.main.GgbAPIW::insertImage(Ljava/lang/String;)(url);
		};
		
		$wnd[ggbApplet].recalculateEnvironments = function(){
				ggbAPI.@geogebra.html5.main.GgbAPIW::recalculateEnvironments()();
		};
		
		$wnd[ggbApplet].isIndependent = function(label){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::isIndependent(Ljava/lang/String;)(label);
		};
		
		$wnd[ggbApplet].isMoveable = function(label){
				return ggbAPI.@geogebra.html5.main.GgbAPIW::isMoveable(Ljava/lang/String;)(label);
		};
		
		$wnd[ggbApplet].setPerspective = function(code){
				ggbAPI.@geogebra.html5.main.GgbAPIW::setPerspective(Ljava/lang/String;)(code);
		};
		
		$wnd[ggbApplet].getPNGBase64 = function(exportScale, transparent, dpi){
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getPNGBase64(DZD)(exportScale,transparent,dpi);
		}
		
		$wnd[ggbApplet].getFileJSON = function(exportScale, transparent, dpi){
			return ggbAPI.@geogebra.html5.main.GgbAPIW::getFileJSON(Z)(false);
		}
		$doc[ggbApplet] = $wnd[ggbApplet];

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
