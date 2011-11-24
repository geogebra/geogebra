package geogebra.plugin;

import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.geos.GeoElement;
import geogebra.main.Application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

//import org.concord.framework.data.stream.DataListener;
//import org.concord.framework.data.stream.DataStreamEvent;
//import org.concord.sensor.SensorDataProducer;
//import org.mozilla.javascript.Context;
//import org.mozilla.javascript.Scriptable;


public class ScriptManager {
	
	private Application app;
	boolean listenersEnabled = true;
	
	// library of functions that is available to all JavaScript calls
	// init() is called when GeoGebra starts up (eg to start listeners)
	/*
	private String libraryScriptxxx ="function ggbOnInit() {}";
	private String libraryScriptxx ="function ggbOnInit() {"+
		"ggbApplet.evalCommand('A=(1,2)');" +
	//"ggbApplet.registerAddListener('listener');" +
	"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}" +
			"function listener() {//java.lang.System.out.println('add listener called');\n" +
			"var x = ggbApplet.getXcoord('A');" +
			"var y = ggbApplet.getYcoord('A');" +
			"var len = Math.sqrt(x*x + y*y);" +
			"if (len > 5) { x=x*5/len; y=y*5/len; }" +
			"" +
			"ggbApplet.unregisterObjectUpdateListener('A');" +
			"ggbApplet.setCoords('A',x,y);" +
			"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}";*/
	
	public ScriptManager(Application app) {
		this.app = app;
		
		//evalScript("ggbOnInit();");
	}
	
	public void ggbOnInit() {
		
		try {
			app.getKernel();
			// call only if libraryJavaScript is not the default (ie do nothing)
			if (!app.getKernel().getLibraryJavaScript().equals(Kernel.defaultLibraryJavaScript))
					CallJavaScript.evalScript(app, "ggbOnInit();", null);
		} catch (Exception e) {
			Application.debug("Error calling ggbOnInit(): "+e.getMessage());
		}
	}
	



	/*
	 * Change listener implementation
	 * Java to JavaScript
	 *
	 */
	
	// maps between GeoElement and JavaScript function names
	private HashMap<GeoElement, String> updateListenerMap;
	private ArrayList<String> addListeners = new ArrayList<String>(), 
			removeListeners  = new ArrayList<String>(), 
			renameListeners  = new ArrayList<String>(), 
			updateListeners  = new ArrayList<String>(), 
			clearListeners  = new ArrayList<String>(),	
			penListeners  = new ArrayList<String>();
	private JavaToJavaScriptView javaToJavaScriptView;
	
	/*
	 * needed for eg File -> New
	 */
	public void resetListeners() {
		if (addListeners != null)
			addListeners.clear();
		
		if (removeListeners != null)
			removeListeners.clear();
		
		if (renameListeners != null)
			renameListeners.clear();
		
		if (updateListeners != null)
			updateListeners.clear();
		
		if (clearListeners != null)
			clearListeners.clear();
		
		if (penListeners != null)
			penListeners.clear();

	}
	
	/**
	 * Registers a JavaScript function as an add listener for the applet's construction.
	 *  Whenever a new object is created in the GeoGebraApplet's construction, the JavaScript 
	 *  function JSFunctionName is called using the name of the newly created object as a single argument. 
	 */
	public synchronized void registerAddListener(String JSFunctionName) {
		registerGlobalListener(addListeners,JSFunctionName,"registerAddListener");
	}
	
	private void registerGlobalListener(ArrayList<String> listenerList,
			String jSFunctionName, String string) {
		if (jSFunctionName == null || jSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (listenerList == null) {
			listenerList = new ArrayList<String>();			
		}		
		listenerList.add(jSFunctionName);				
		Application.debug(string + ": " + jSFunctionName);
		
	}

	/**
	 * Removes a previously registered add listener 
	 * @see #registerAddListener(String) 
	 */
	public synchronized void unregisterAddListener(String JSFunctionName) {
		if (addListeners != null) {
			addListeners.remove(JSFunctionName);
			Application.debug("unregisterAddListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a remove listener for the applet's construction.
	 * Whenever an object is deleted in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public synchronized void registerRemoveListener(String JSFunctionName) {
		registerGlobalListener(removeListeners,JSFunctionName,"registerRemoveListener");
	}
	
	/**
	 * Removes a previously registered remove listener 
	 * @see #registerRemoveListener(String) 
	 */
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		if (removeListeners != null) {
			removeListeners.remove(JSFunctionName);
			Application.debug("unregisterRemoveListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a clear listener for the applet's construction.
	 * Whenever the construction in the GeoGebraApplet's is cleared (i.e. all objects are removed), the JavaScript 
	 * function JSFunctionName is called using no arguments. 	
	 */
	public synchronized void registerClearListener(String JSFunctionName) {
		registerGlobalListener(clearListeners,JSFunctionName,"registerClearListener");
	}
	
	/**
	 * Removes a previously registered clear listener 
	 * @see #registerClearListener(String) 
	 */
	public synchronized void unregisterClearListener(String JSFunctionName) {
		if (clearListeners != null) {
			clearListeners.remove(JSFunctionName);
			Application.debug("unregisterClearListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a rename listener for the applet's construction.
	 * Whenever an object is renamed in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public synchronized void registerRenameListener(String JSFunctionName) {
		registerGlobalListener(renameListeners,JSFunctionName,"registerRenameListener");
	}
	
	/**
	 * Removes a previously registered rename listener.
	 * @see #registerRenameListener(String) 
	 */
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		if (renameListeners != null) {
			renameListeners.remove(JSFunctionName);
			Application.debug("unregisterRenameListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as an update listener for the applet's construction.
	 * Whenever any object is updated in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the updated object as a single argument. 	
	 */
	public synchronized void registerUpdateListener(String JSFunctionName) {
		registerGlobalListener(updateListeners,JSFunctionName,"registerUpdateListener");
	}
	
	/**
	 * Removes a previously registered update listener.
	 * @see #registerRemoveListener(String) 
	 */
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		if (updateListeners != null) {
			updateListeners.remove(JSFunctionName);
			Application.debug("unregisterUpdateListener: " + JSFunctionName);
		}	
	}
	
	public synchronized void registerPenListener(String JSFunctionName) {
		registerGlobalListener(penListeners,JSFunctionName,"registerPenListener");
	}
	
	public synchronized void unregisterPenListener(String JSFunctionName) {
		if (penListeners != null) {
			penListeners.remove(JSFunctionName);
			Application.debug("unregisterPenListener: " + JSFunctionName);
		}	
	}
	
	/**
	 * Registers a JavaScript update listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 * 
	 * Example: First, set a change listening JavaScript function:
	 * ggbApplet.setChangeListener("A", "myJavaScriptFunction");
	 * Then the GeoGebra Applet will call the Javascript function
	 * myJavaScriptFunction("A");
	 * whenever object A changes.	
	 */
	public synchronized void registerObjectUpdateListener(String objName, String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;		
		GeoElement geo = app.getKernel().lookupLabel(objName);
		if (geo == null) return;
				
		// init view
		initJavaScriptView();
		
		// init map and view
		if (updateListenerMap == null) {
			updateListenerMap = new HashMap<GeoElement, String>();			
		}
		
		// add map entry
		updateListenerMap.put(geo, JSFunctionName);		
		Application.debug("registerUpdateListener: object: " + objName + ", function: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see setChangeListener
	 */
	public synchronized void unregisterObjectUpdateListener(String objName) {
		if (updateListenerMap != null) {
			GeoElement geo = app.getKernel().lookupLabel(objName);
			if (geo != null) {
				updateListenerMap.remove(geo);
				Application.debug("unregisterUpdateListener for object: " + objName);
			}
		}
	}			
	
	/**
	 * Implements the View interface for
	 * Java to JavaScript communication, see
	 * addChangeListener() and removeChangeListener()
	 */	
	private class JavaToJavaScriptView implements View {
		
		/**
		 * Calls all registered add listeners.
		 * @see #registerAddListener(String)
		 */
		public void add(GeoElement geo) {
			if (addListeners.size()>0 && geo.isLabelSet()) { 	
				Object [] args = { geo.getLabel() };
				notifyListeners(addListeners, args);
			}
		}
		
		/**
		 * Calls all registered remove listeners.
		 * @see #registerRemoveListener(String)
		 */
		public void remove(GeoElement geo) {
			if (removeListeners.size()>0 && geo.isLabelSet()) {  
				Object [] args = { geo.getLabel() };
				notifyListeners(removeListeners, args);						
			}			
		}
		
		/**
		 * Calls all registered clear listeners.
		 * @see #registerClearListener(String)
		 */
		public void clearView() {
			/* 
			 * This code would make sense for a "reload" 
			 * 
			// try to keep all update listeners
			if (updateListenerMap != null) {			
				HashMap newGeoJSfunMap = new HashMap(); 
				
				// go through all geos and update their maps
				Iterator it = updateListenerMap.keySet().iterator();
				while (it.hasNext()) {
					// try to find new geo with same label
					GeoElement oldGeo = (GeoElement) it.next();				
					GeoElement newGeo = kernel.lookupLabel(oldGeo.getLabel());
					
					if (newGeo != null)
						// add mapping to new map
						newGeoJSfunMap.put(newGeo,(String) updateListenerMap.get(oldGeo));				
				}
				
				// use new map
				updateListenerMap.clear();
				updateListenerMap = newGeoJSfunMap;			
			}
			*/
			
			app.getGgbApi().lastGeoElementsIteratorSize = 0;	//ulven 29.08.05: should have been a method...
			updateListenerMap = null;			
			if (clearListeners.size()>0) {  				
				notifyListeners(clearListeners, null);						
			}
		}
		
		/**
		 * Calls all registered rename listeners.
		 * @see #registerRenameListener(String)
		 */
		public void rename(GeoElement geo) {						
			if (renameListeners.size()>0 && geo.isLabelSet()) {
				Object [] args = { geo.getOldLabel(), geo.getLabel() };
				notifyListeners(renameListeners, args);				
			}			
		}
		
		/**
		 * Calls all JavaScript functions (listeners) using 
		 * the specified arguments.
		 */
		private synchronized void notifyListeners(ArrayList<String> listeners, Object [] args) {	
			if (!listenersEnabled) return;
			int size = listeners.size();
			for (int i=0; i < size; i++) {
				String jsFunction = (String) listeners.get(i);										
				callJavaScript(jsFunction, args);					
			}			
		}	
																	
		/**
		 * Calls all registered update and updateObject listeners.
		 * @see #registerUpdateListener(String)
		 */
		public synchronized void update(GeoElement geo) {			
			geo.runUpdateScripts();
			if (!listenersEnabled) return;
			// update listeners
			if (updateListeners.size()>0 && geo.isLabelSet()) {
				Object [] args = { geo.getLabel() };
				notifyListeners(updateListeners, args);	
			}
			
			// updateObject listeners
			if (updateListenerMap != null) {			
				String jsFunction = (String) updateListenerMap.get(geo);		
				if (jsFunction != null) {	
					Object [] args = { geo.getLabel() };
					callJavaScript(jsFunction, args);
				}
			}
			
		}
		

		final public void updateVisualStyle(GeoElement geo) {
			update(geo);
		}
				
		public void updateAuxiliaryObject(GeoElement geo) {
			update(geo);
		}				
					
		public void reset() {							
		}
		
		public void setMode(int mode) {
			// don't react..
		}
				
    	public void repaintView() {
    		// no repaint should occur here: views that are
    		// part of the applet do this on their own    		
    	}

		public int getViewID() {
			return Application.VIEW_NONE;
		}
	}
		
	public synchronized void initJavaScriptView() {
		if (javaToJavaScriptView == null) {
			javaToJavaScriptView = new JavaToJavaScriptView();
			app.getKernel().attach(javaToJavaScriptView); // register view
			initJavaScript();
		}
	}
	
	public synchronized void initJavaScriptViewWithoutJavascript() {
		if (javaToJavaScriptView == null) {
			javaToJavaScriptView = new JavaToJavaScriptView();
			app.getKernel().attach(javaToJavaScriptView); // register view			
		}
	}
	
	public synchronized void initJavaScript() {
		
		if (app.isApplet()) {
			app.getApplet().initJavaScript();
		}
	}
	
	public void callJavaScript(String jsFunction, Object [] args) {		
		if (app.isApplet() && app.useBrowserForJavaScript()) {
			app.getApplet().callJavaScript(jsFunction, args);
		} else {

			
			StringBuilder sb = new StringBuilder();
			sb.append(jsFunction);
			sb.append("(");
			for (int i = 0 ; i < args.length ; i++) {
				sb.append('"');
				sb.append(args[i].toString());
				sb.append('"');
				if (i < args.length - 1) sb.append(",");
			}
			sb.append(");");
			
			Application.debug(sb.toString());
			
			CallJavaScript.evalScript(app, sb.toString(), null);

		}
	}
	
	public void disableListeners() {
		listenersEnabled = false;
	}

	public void enableListeners() {
		listenersEnabled = true;
	}
	
	USBFunctions usb = null;
	
	public USBFunctions getUSBFunctions() {
		if (usb == null) usb = new USBFunctions(this);
		
		return usb;
	}

	public void notifyDraw(String label, double[] x, double[] y) {			
		if (!listenersEnabled || penListeners == null || penListeners.size() ==0) 
			return;
		int n = x.length;
		StringBuilder params = new StringBuilder("(\"");
		params.append(label);
		params.append("\",new Array(");
		for(int i =0;i<n;i++)
			params.append(x[i]+(i!=n-1?",":"),new Array("));
		for(int i =0;i<n;i++)
				params.append(y[i]+(i!=n-1?",":"))"));
		
		int size = penListeners.size();
		for (int i=0; i < size; i++) {							
			CallJavaScript.evalScript(app, (String) penListeners.get(i)+ 
					params.toString(),null);
			
		}		
		
		
	}


}
