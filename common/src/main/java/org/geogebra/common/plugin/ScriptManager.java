package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.script.JsScript;
import org.geogebra.common.util.debug.Log;

public abstract class ScriptManager implements EventListener {

	protected App app;
	protected boolean listenersEnabled = true;
	// maps between GeoElement and JavaScript function names
	protected HashMap<GeoElement, JsScript> updateListenerMap;
	protected HashMap<GeoElement, JsScript> clickListenerMap;
	protected ArrayList<JsScript> addListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> storeUndoListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> removeListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> renameListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> updateListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> clickListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> clearListeners = new ArrayList<JsScript>();
	protected ArrayList<JsScript> clientListeners = new ArrayList<JsScript>();

	private ArrayList[] listenerLists() {
		return new ArrayList[] { addListeners,
			storeUndoListeners, removeListeners, renameListeners,
			updateListeners, clickListeners, clearListeners, clientListeners };
	}
	public ScriptManager(App app) {
		this.app = app;
		app.getEventDispatcher().addEventListener(this);
	}

	public void sendEvent(Event evt) {
		// TODO get rid of javaToJavaScriptView
		if (!listenersEnabled) {
			return;
		}
		switch (evt.type) {
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
		case STOREUNDO:
			callListeners(storeUndoListeners, evt);
			break;
		case REMOVE:
			callListeners(removeListeners, evt);
			break;
		case RENAME:
			callListeners(renameListeners, evt);
			break;
		case RELATION_TOOL:
		case RENAME_COMPLETE:
		case ADD_POLYGON:
		case ADD_POLYGON_COMPLETE:
		case MOVING_GEOS:
		case MOVED_GEOS:
		case PASTE_ELMS:
		case PASTE_ELMS_COMPLETE:
		case DELETE_GEOS:
		case LOGIN:
		case SET_MODE:
		case UPDATE_STYLE:
		case SHOW_NAVIGATION_BAR:
		case SHOW_STYLE_BAR:
		case PERSPECTIVE_CHANGE:
		case SELECT:
		case DESELECT:
			callClientListeners(clientListeners, evt);
			break;
		// TODO case CLEAR
		default:
			Log.debug("Unknown event type");
		}
	}

	private static Object[] getArguments(Event evt) {
		GeoElement geo = evt.target;
		if (geo == null) {
			return new Object[0];
		}
		String label = geo.getLabel(StringTemplate.defaultTemplate);
		if (evt.type == EventType.RENAME) {
			return new Object[] { geo.getOldLabel(), label };
		} else if (evt.argument == null) {
			return new Object[] { label };
		}
		return new Object[] { label, evt.argument };
	}

	private void callListeners(List<JsScript> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}
		for (JsScript listener : listeners) {
			callListener(listener, evt);
		}
	}

	private void callClientListeners(List<JsScript> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(evt.type.getName());
		if (evt.targets != null) {
			for (GeoElement geo : evt.targets) {
				args.add(geo.getLabelSimple());
			}
		} else if (evt.target != null) {
			args.add(evt.target.getLabelSimple());
		} else {
			args.add("");
		}
		if (evt.argument != null) {
			args.add(evt.argument);
		}

		for (JsScript listener : listeners) {
			callJavaScript(listener.getText(), args.toArray());
		}
	}

	private void callListener(JsScript listener, Event evt) {
		if (listener != null) {
			String fn = listener.getText();
			GeoElement geo = evt.target;
			if (geo == null) {
				callJavaScript(fn, null, null);
				return;
			}
			String label = geo.getLabel(StringTemplate.defaultTemplate);
			if (evt.type == EventType.RENAME) {
				callJavaScript(fn, geo.getOldLabel(), label);
				return;
			} else if (evt.argument == null) {
				callJavaScript(fn, label, null);
				return;
			}
			callJavaScript(fn, evt.argument, null);

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

		if (updateListenerMap != null) {
			updateListenerMap = null;
		}

		if (clickListenerMap != null) {
			clickListenerMap = null;
		}

		// If undo clicked, mustn't clear the global listeners
		if (!listenersEnabled)
			return;

		if (addListeners != null) {
			addListeners.clear();
		}

		for (ArrayList a : listenerLists()) {
			if (a != null && a != storeUndoListeners && a.size() > 0) {
				a.clear();
			}
		}
	}

	/**
	 * Registers a JavaScript function as an add listener for the applet's
	 * construction. Whenever a new object is created in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the newly created object as a single argument.
	 */
	public synchronized void registerAddListener(String JSFunctionName) {
		registerGlobalListener(addListeners, JSFunctionName,
				"registerAddListener");
	}

	/**
	 * Registers a JavaScript function as an add listener for the applet's
	 * construction. Whenever a new object is created in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the newly created object as a single argument.
	 */
	public synchronized void registerStoreUndoListener(String JSFunctionName) {
		if (!app.isUndoActive()) {
			app.getKernel().setUndoActive(true);
			app.getKernel().initUndoInfo();
		}
		registerGlobalListener(storeUndoListeners, JSFunctionName,
				"registerStoreUndoListener");

	}

	private void registerGlobalListener(ArrayList<JsScript> listenerList,
			String jSFunctionName, String string) {
		if (jSFunctionName == null || jSFunctionName.length() == 0) {
			return;
		}

		initJavaScript();

		// init list
		if (listenerList != null) {
			listenerList.add(JsScript.fromName(app, jSFunctionName));
		}
		Log.debug(string + " (" + listenerList.size() + ") : " + jSFunctionName);

	}

	/**
	 * Removes a previously registered add listener
	 * 
	 * @see #registerAddListener(String)
	 */
	public synchronized void unregisterAddListener(String JSFunctionName) {
		if (addListeners != null) {
			addListeners.remove(JsScript.fromName(app, JSFunctionName));
			Log.debug("unregisterAddListener: " + JSFunctionName);
		}
	}

	/**
	 * Registers a JavaScript function as a remove listener for the applet's
	 * construction. Whenever an object is deleted in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public synchronized void registerRemoveListener(String JSFunctionName) {
		registerGlobalListener(removeListeners, JSFunctionName,
				"registerRemoveListener");
	}

	/**
	 * Removes a previously registered remove listener
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		if (removeListeners != null) {
			removeListeners.remove(JsScript.fromName(app, JSFunctionName));
			Log.debug("unregisterRemoveListener: " + JSFunctionName);
		}
	}

	/**
	 * Registers a JavaScript function as a clear listener for the applet's
	 * construction. Whenever the construction in the GeoGebraApplet's is
	 * cleared (i.e. all objects are removed), the JavaScript function
	 * JSFunctionName is called using no arguments.
	 */
	public synchronized void registerClearListener(String JSFunctionName) {
		registerGlobalListener(clearListeners, JSFunctionName,
				"registerClearListener");
	}

	/**
	 * Removes a previously registered clear listener
	 * 
	 * @see #registerClearListener(String)
	 */
	public synchronized void unregisterClearListener(String JSFunctionName) {
		if (clearListeners != null) {
			clearListeners.remove(JsScript.fromName(app, JSFunctionName));
			Log.debug("unregisterClearListener: " + JSFunctionName);
		}
	}

	/**
	 * Registers a JavaScript function as a rename listener for the applet's
	 * construction. Whenever an object is renamed in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public synchronized void registerRenameListener(String JSFunctionName) {
		registerGlobalListener(renameListeners, JSFunctionName,
				"registerRenameListener");
	}

	/**
	 * Removes a previously registered rename listener.
	 * 
	 * @see #registerRenameListener(String)
	 */
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		if (renameListeners != null) {
			renameListeners.remove(JsScript.fromName(app, JSFunctionName));
			Log.debug("unregisterRenameListener: " + JSFunctionName);
		}
	}

	/**
	 * Registers a JavaScript function as an update listener for the applet's
	 * construction. Whenever any object is updated in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	public synchronized void registerUpdateListener(String JSFunctionName) {
		registerGlobalListener(updateListeners, JSFunctionName,
				"registerUpdateListener");
	}

	/**
	 * Removes a previously registered update listener.
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		if (updateListeners != null) {
			updateListeners.remove(JsScript.fromName(app, JSFunctionName));
		}
	}

	/**
	 * Registers a JavaScript function as a click listener for the applet's
	 * construction. Whenever any object is click in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the clicked object as a single argument.
	 */
	public synchronized void registerClickListener(String JSFunctionName) {
		registerGlobalListener(clickListeners, JSFunctionName,
				"registerUpdateListener");
	}

	/**
	 * Removes a previously registered click listener.
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public synchronized void unregisterClickListener(String JSFunctionName) {
		if (clickListeners != null) {
			clickListeners.remove(JsScript.fromName(app, JSFunctionName));
		}
	}

	/**
	 * Registers a JS function to be notified of client events.
	 */
	public synchronized void registerClientListener(String JSFunctionName) {
		registerGlobalListener(clientListeners, JSFunctionName,
				"registerClientListener");
	}

	public synchronized void unregisterClientListener(String JSFunctionName) {
		if (clientListeners != null) {
			clientListeners.remove(JsScript.fromName(app, JSFunctionName));
		}
	}

	/**
	 * Registers a JavaScript listener for an object. Whenever the object with
	 * the given name changes, a JavaScript function named JSFunctionName is
	 * called using the name of the changed object as the single argument. If
	 * objName previously had a mapping JavaScript function, the old value is
	 * replaced.
	 */
	private synchronized HashMap<GeoElement, JsScript> registerObjectListener(
			HashMap<GeoElement, JsScript> map, String objName,
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
			map = new HashMap<GeoElement, JsScript>();
		}
		Log.debug(JSFunctionName);
		map.put(geo, JsScript.fromName(app, JSFunctionName));
		return map;
	}

	/**
	 * Removes a previously set object listener for the given object.
	 */
	private synchronized void unregisterObjectListener(
			HashMap<GeoElement, JsScript> map, String objName) {
		if (map != null) {
			GeoElement geo = app.getKernel().lookupLabel(objName);
			if (geo != null) {
				map.remove(geo);
			}
		}
	}

	/**
	 * Register a JavaScript function that will run when an object is updated
	 * 
	 * @param objName
	 *            the name of the target object
	 * @param fName
	 *            the name of the JavaScript function
	 */
	public void registerObjectUpdateListener(String objName, String fName) {
		updateListenerMap = registerObjectListener(updateListenerMap, objName,
				fName);
	}

	/**
	 * Unregister any JavaScript function that runs when an object is updated
	 * 
	 * @param objName
	 *            the name of the target object
	 */
	public void unregisterObjectUpdateListener(String objName) {
		unregisterObjectListener(updateListenerMap, objName);
	}

	/**
	 * Register a JavaScript function that will run when an object is clicked
	 * 
	 * @param objName
	 *            the name of the target object
	 * @param fName
	 *            the name of the JavaScript function
	 */
	public void registerObjectClickListener(String objName, String fName) {
		clickListenerMap = registerObjectListener(clickListenerMap, objName,
				fName);
	}

	/**
	 * Unregister any JavaScript function that runs when an object is clicked
	 * 
	 * @param objName
	 *            the name of the target object
	 */
	public void unregisterObjectClickListener(String objName) {
		unregisterObjectListener(clickListenerMap, objName);
	}

	public abstract void ggbOnInit();

	public synchronized void initJavaScript() {
	}



	abstract public void callJavaScript(String jsFunction, Object[] args);

	public void callJavaScript(String jsFunction, Object arg0,
			Object arg1){
		if (arg0 == null) {
			callJavaScript(jsFunction, new Object[0]);
			return;
		}
		if (arg1 == null) {
			callJavaScript(jsFunction, new Object[] { arg0 });
			return;
		}
		callJavaScript(jsFunction,new Object[]{arg0, arg1});
	}

	// ------ getters for listeners -------------

	public ArrayList<JsScript> getAddListeners() {
		if (addListeners == null) {
			addListeners = new ArrayList<JsScript>();
		}
		return addListeners;
	}

	public ArrayList<JsScript> getStoreUndoListeners() {
		if (storeUndoListeners == null) {
			storeUndoListeners = new ArrayList<JsScript>();
		}
		return storeUndoListeners;
	}

	public ArrayList<JsScript> getRemoveListeners() {
		if (removeListeners == null) {
			removeListeners = new ArrayList<JsScript>();
		}
		return removeListeners;
	}

	public ArrayList<JsScript> getRenameListeners() {
		if (renameListeners == null) {
			renameListeners = new ArrayList<JsScript>();
		}
		return renameListeners;
	}

	public ArrayList<JsScript> getupdateListeners() {
		if (updateListeners == null) {
			updateListeners = new ArrayList<JsScript>();
		}
		return updateListeners;
	}

	public ArrayList<JsScript> getClearListeners() {
		if (clearListeners == null) {
			clearListeners = new ArrayList<JsScript>();
		}
		return clearListeners;
	}

	public HashMap<GeoElement, JsScript> getUpdateListenerMap() {
		if (updateListenerMap == null) {
			updateListenerMap = new HashMap<GeoElement, JsScript>();
		}
		return updateListenerMap;
	}

	public HashMap<GeoElement, JsScript> getClickListenerMap() {
		if (clickListenerMap == null) {
			clickListenerMap = new HashMap<GeoElement, JsScript>();
		}
		return clickListenerMap;
	}

	public void setGlobalScript() {
		// to be overridden
	}


	public boolean hasListeners() {
		// TODO Auto-generated method stub
		if (updateListenerMap != null && updateListenerMap.size() > 0) {
			return true;
		}
		if (clickListenerMap != null && clickListenerMap.size() > 0) {
			return true;
		}
		for (ArrayList a : listenerLists()) {
			if (a != null && a.size() > 0) {
				return true;
			}
		}
		return false;
	}

}
