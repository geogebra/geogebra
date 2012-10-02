package geogebra.common.plugin;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class ScriptManager implements EventListener{

	protected App app;
	protected boolean listenersEnabled = true;
	// maps between GeoElement and JavaScript function names
	protected HashMap<GeoElement, String> updateListenerMap;
	protected ArrayList<String> addListeners = new ArrayList<String>();
	protected ArrayList<String>	removeListeners  = new ArrayList<String>();
	protected ArrayList<String>	renameListeners  = new ArrayList<String>();
	protected ArrayList<String>	updateListeners  = new ArrayList<String>();
	protected ArrayList<String>	clearListeners  = new ArrayList<String>();
	
	
	public ScriptManager(App app) {
		this.app = app;
		app.getEventDispatcher().addEventListener(this);
	}
	
	
	public void sendEvent(Event evt) {
		// TODO get rid of javaToJavaScriptView
		if (!listenersEnabled) {
			return;
		}
		switch(evt.type) {
		case CLICK:
			break;
		case UPDATE:
			callListeners(updateListeners, evt);
			if (updateListenerMap != null) {
				callListener(updateListenerMap.get(evt.target), evt);
			}
			break;
		case ADD:
			callListeners(addListeners, evt);
			break;
		case REMOVE:
			callListeners(removeListeners, evt);
			break;
		case RENAME:
			callListeners(renameListeners, evt);
			break;
		// TODO case CLEAR
		default:
			App.debug("Unknown event type");
		}
	}
	
	private static Object[] getArguments(Event evt) {
		GeoElement geo = evt.target;
		String label = geo.getLabel(StringTemplate.defaultTemplate);
		if (evt.type == EventType.RENAME) {
			return new Object[] {geo.getOldLabel(), label};
		} else if (evt.argument == null) {
			return new Object[] {label};
		}
		return new Object[] {label, evt.argument};
	}
	
	private void callListeners(List<String> listeners, Event evt) {
		Object[] args = getArguments(evt);
		for (String listener : listeners) {
			callJavaScript(listener, args);
		}
	}
	
	private void callListener(String listener, Event evt) {
		if (listener != null) {
			callJavaScript(listener, getArguments(evt));
		}
	}
	
	public void disableListeners() {
		listenersEnabled = false;
	}

	public void enableListeners() {
		listenersEnabled = true;
	}

	/*
	 * needed for eg File -> New
	 */
	public void resetListeners() {
		if (addListeners != null) {
			addListeners.clear();
		}

		if (removeListeners != null) {
			removeListeners.clear();
		}

		if (renameListeners != null) {
			renameListeners.clear();
		}

		if (updateListeners != null) {
			updateListeners.clear();
		}

		if (clearListeners != null) {
			clearListeners.clear();
		}
		
		if (updateListenerMap != null) {
			updateListenerMap.clear();
		}
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
		if (jSFunctionName == null || jSFunctionName.length() == 0) {
			return;				
		}
						
		// init view
		initJavaScriptView();
		
		// init list
		if (listenerList == null) {
			listenerList = new ArrayList<String>();			
		}		
		listenerList.add(jSFunctionName);				
		App.debug(string + ": " + jSFunctionName);
		
	}

	/**
	 * Removes a previously registered add listener 
	 * @see #registerAddListener(String) 
	 */
	public synchronized void unregisterAddListener(String JSFunctionName) {
		if (addListeners != null) {
			addListeners.remove(JSFunctionName);
			App.debug("unregisterAddListener: " + JSFunctionName);
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
			App.debug("unregisterRemoveListener: " + JSFunctionName);
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
			App.debug("unregisterClearListener: " + JSFunctionName);
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
			App.debug("unregisterRenameListener: " + JSFunctionName);
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
			App.debug("unregisterUpdateListener: " + JSFunctionName);
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
		App.debug("registerUpdateListener: object: " + objName + ", function: " + JSFunctionName);
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
				App.debug("unregisterUpdateListener for object: " + objName);
			}
		}
	}			

	public synchronized void initJavaScriptView() {
		// TODO check to see if it's already done?
		initJavaScript();
	}
	
	public synchronized void initJavaScriptViewWithoutJavascript() {
		// TODO remove this
	}

	public void ggbOnInit() {
		app.callAppletJavaScript("ggbOnInit", null);
	}

	public synchronized void initJavaScript() {
	}

	abstract public void callJavaScript(String jsFunction, Object [] args);
}


