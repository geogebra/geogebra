package geogebra.common.plugin;

import geogebra.common.kernel.StringTemplate;
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
	protected HashMap<GeoElement, String> clickListenerMap;
	protected ArrayList<String> addListeners = new ArrayList<String>();
	protected ArrayList<String>	removeListeners  = new ArrayList<String>();
	protected ArrayList<String>	renameListeners  = new ArrayList<String>();
	protected ArrayList<String>	updateListeners  = new ArrayList<String>();
	protected ArrayList<String>	clickListeners  = new ArrayList<String>();
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
			callListeners(clickListeners, evt);
			if (clickListenerMap != null) {
				callListener(clickListenerMap.get(evt.target), evt);
			}
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
		if (listeners.isEmpty()) {
			return;
		}
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
	public void reset() {
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
			updateListenerMap = null;
		}
		
		if (clickListenerMap != null) {
			clickListenerMap = null;
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
						
		initJavaScript();
		
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
		}	
	}
	
	/**
	 * Registers a JavaScript function as a click listener for the applet's construction.
	 * Whenever any object is click in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the clicked object as a single argument. 	
	 */
	public synchronized void registerClickListener(String JSFunctionName) {
		registerGlobalListener(clickListeners,JSFunctionName,"registerUpdateListener");
	}
	
	/**
	 * Removes a previously registered click listener.
	 * @see #registerRemoveListener(String) 
	 */
	public synchronized void unregisterClickListener(String JSFunctionName) {
		if (clickListeners != null) {
			clickListeners.remove(JSFunctionName);
		}	
	}
	
	/**
	 * Registers a JavaScript listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 */
	private synchronized HashMap<GeoElement, String> registerObjectListener(
			HashMap<GeoElement, String> map,
			String objName,
			String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0) {
			return map;	
		}
		GeoElement geo = app.getKernel().lookupLabel(objName);
		if (geo == null) {
			return map;
		}
		initJavaScript();
		if (map == null) {
			map = new HashMap<GeoElement, String>();			
		}
		map.put(geo, JSFunctionName);
		return map;
	}
	
	/**
	 * Removes a previously set object listener for the given object.
	 */
	private synchronized void unregisterObjectListener(			
			HashMap<GeoElement, String> map,
			String objName) {
		if (map != null) {
			GeoElement geo = app.getKernel().lookupLabel(objName);
			if (geo != null) {
				map.remove(geo);
			}
		}
	}			
	
	/**
	 * Register a JavaScript function that will run when an object is updated
	 * @param objName the name of the target object
	 * @param fName the name of the JavaScript function
	 */
	public void registerObjectUpdateListener(String objName, String fName) {
		updateListenerMap = registerObjectListener(updateListenerMap, objName, fName);
	}
	
	/**
	 * Unregister any JavaScript function that runs when an object is updated
	 * @param objName the name of the target object
	 */
	public void unregisterObjectUpdateListener(String objName) {
		unregisterObjectListener(updateListenerMap, objName);
	}
	
	/**
	 * Register a JavaScript function that will run when an object is clicked
	 * @param objName the name of the target object
	 * @param fName the name of the JavaScript function
	 */
	public void registerObjectClickListener(String objName, String fName) {
		clickListenerMap = registerObjectListener(clickListenerMap, objName, fName);
	}
	
	/**
	 * Unregister any JavaScript function that runs when an object is clicked
	 * @param objName the name of the target object
	 */
	public void unregisterObjectClickListener(String objName) {
		unregisterObjectListener(clickListenerMap, objName);
	}
	
	public void ggbOnInit() {
		app.callAppletJavaScript("ggbOnInit", null);
	}

	public synchronized void initJavaScript() {
	}

	abstract public void callJavaScript(String jsFunction, Object [] args);
}


