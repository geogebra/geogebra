package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.script.JsScript;
import org.geogebra.common.util.debug.Log;

public abstract class ScriptManager implements EventListener {

	protected App app;
	protected boolean listenersEnabled = true;
	protected boolean jsEnabled = true;
	// maps between GeoElement and JavaScript function names
	protected HashMap<GeoElement, JsScript> updateListenerMap;
	protected HashMap<GeoElement, JsScript> clickListenerMap;
	protected final ArrayList<JsScript> addListeners = new ArrayList<>();
	protected final ArrayList<JsScript> storeUndoListeners = new ArrayList<>();
	protected final ArrayList<JsScript> removeListeners = new ArrayList<>();
	protected final ArrayList<JsScript> renameListeners = new ArrayList<>();
	protected final ArrayList<JsScript> updateListeners = new ArrayList<>();
	protected final ArrayList<JsScript> clickListeners = new ArrayList<>();
	protected final ArrayList<JsScript> clearListeners = new ArrayList<>();
	protected final ArrayList<JsScript> clientListeners = new ArrayList<>();
	private boolean keepListenersOnReset = true;

	private ArrayList<JsScript>[] listenerLists() {
		return new ArrayList[] { addListeners, storeUndoListeners,
				removeListeners, renameListeners, updateListeners,
				clickListeners, clearListeners, clientListeners };
	}

	/**
	 * For tests only.
	 */
	ScriptManager() {

	}

	/**
	 * @param app
	 *            application
	 */
	public ScriptManager(@Nonnull App app) {
		this.app = app;
		app.getEventDispatcher().addEventListener(this);
	}

	@Override
	public void sendEvent(Event evt) {
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
		case CLEAR:
			callListeners(clearListeners, evt);
			break;
		default:
			callClientListeners(clientListeners, evt);
		}
	}

	private void callListeners(List<JsScript> listeners, Event evt) {
		for (JsScript listener : listeners) {
			callListener(listener, evt);
		}
	}

	private void callListener(JsScript listener, Event evt) {
		if (listener != null) {
			String fn = listener.getText();
			GeoElement geo = evt.target;
			if (geo == null) {
				callListener(fn);
				return;
			}
			String label = geo.getLabel(StringTemplate.defaultTemplate);
			if (evt.type == EventType.RENAME) {
				callListener(fn, geo.getOldLabel(), label);
				return;
			} else if (evt.argument == null) {
				callListener(fn, label);
				return;
			}
			callListener(fn, evt.argument);
		}
	}

	protected void callListener(String fn, String... args) {
		// implemented in web and desktop
	}

	protected void callClientListeners(List<JsScript> listeners, Event evt) {
		// implemented in web and desktop
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
	@Override
	public void reset() {
		if (keepListenersOnReset) {
			return;
		}

		if (updateListenerMap != null) {
			updateListenerMap = null;
		}

		if (clickListenerMap != null) {
			clickListenerMap = null;
		}

		// If undo clicked, mustn't clear the global listeners
		if (!listenersEnabled) {
			return;
		}

		addListeners.clear();

		for (ArrayList<JsScript> a : listenerLists()) {
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
		registerGlobalListener(addListeners, JSFunctionName);
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
		registerGlobalListener(storeUndoListeners, JSFunctionName);
	}

	public synchronized void unregisterStoreUndoListener(String JSFunctionName) {
		storeUndoListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	private void registerGlobalListener(ArrayList<JsScript> listenerList,
			String jSFunctionName) {
		if (jSFunctionName == null || jSFunctionName.length() == 0) {
			return;
		}

		// init list
		if (listenerList != null) {
			listenerList.add(JsScript.fromName(app, jSFunctionName));
		}
	}

	/**
	 * Removes a previously registered add listener
	 * 
	 * @see #registerAddListener(String)
	 */
	public synchronized void unregisterAddListener(String JSFunctionName) {
		addListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a remove listener for the applet's
	 * construction. Whenever an object is deleted in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public synchronized void registerRemoveListener(String JSFunctionName) {
		registerGlobalListener(removeListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered remove listener
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		removeListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a clear listener for the applet's
	 * construction. Whenever the construction in the GeoGebraApplet's is
	 * cleared (i.e. all objects are removed), the JavaScript function
	 * JSFunctionName is called using no arguments.
	 */
	public synchronized void registerClearListener(String JSFunctionName) {
		registerGlobalListener(clearListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered clear listener
	 * 
	 * @see #registerClearListener(String)
	 */
	public synchronized void unregisterClearListener(String JSFunctionName) {
		clearListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a rename listener for the applet's
	 * construction. Whenever an object is renamed in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public synchronized void registerRenameListener(String JSFunctionName) {
		registerGlobalListener(renameListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered rename listener.
	 * 
	 * @see #registerRenameListener(String)
	 */
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		renameListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as an update listener for the applet's
	 * construction. Whenever any object is updated in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	public synchronized void registerUpdateListener(String JSFunctionName) {
		registerGlobalListener(updateListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered update listener.
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		updateListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a click listener for the applet's
	 * construction. Whenever any object is click in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the clicked object as a single argument.
	 */
	public synchronized void registerClickListener(String JSFunctionName) {
		registerGlobalListener(clickListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered click listener.
	 * 
	 * @see #registerRemoveListener(String)
	 */
	public synchronized void unregisterClickListener(String JSFunctionName) {
		clickListeners.remove(JsScript.fromName(app, JSFunctionName));
	}

	/**
	 * Registers a JS function to be notified of client events.
	 * 
	 * @param jsFunctionName
	 *            client listener name
	 */
	public synchronized void registerClientListener(String jsFunctionName) {
		registerGlobalListener(clientListeners, jsFunctionName);
	}

	/**
	 * @param jsFunctionName
	 *            client listener name
	 */
	public synchronized void unregisterClientListener(String jsFunctionName) {
		clientListeners.remove(JsScript.fromName(app, jsFunctionName));
	}

	/**
	 * Registers a JavaScript listener for an object. Whenever the object with
	 * the given name changes, a JavaScript function named JSFunctionName is
	 * called using the name of the changed object as the single argument. If
	 * objName previously had a mapping JavaScript function, the old value is
	 * replaced.
	 */
	private synchronized HashMap<GeoElement, JsScript> registerObjectListener(
			HashMap<GeoElement, JsScript> map0, String objName,
			String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0) {
			return map0;
		}
		GeoElement geo = app.getKernel().lookupLabel(objName);
		if (geo == null) {
			return map0;
		}

		HashMap<GeoElement, JsScript> map = map0;
		if (map == null) {
			map = new HashMap<>();
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

	// ------ getters for listeners -------------

	/**
	 * @return add listeners
	 */
	public ArrayList<JsScript> getAddListeners() {
		return addListeners;
	}

	/**
	 * @return strore undo listeners
	 */
	public ArrayList<JsScript> getStoreUndoListeners() {
		return storeUndoListeners;
	}

	/**
	 * @return remove listeners
	 */
	public ArrayList<JsScript> getRemoveListeners() {
		return removeListeners;
	}

	/**
	 * @return rename listeners
	 */
	public ArrayList<JsScript> getRenameListeners() {
		return renameListeners;
	}

	/**
	 * @return update listeners
	 */
	public ArrayList<JsScript> getupdateListeners() {
		return updateListeners;
	}

	/**
	 * @return clear listeners
	 */
	public ArrayList<JsScript> getClearListeners() {
		return clearListeners;
	}

	/**
	 * @return object update listeners
	 */
	public HashMap<GeoElement, JsScript> getUpdateListenerMap() {
		if (updateListenerMap == null) {
			updateListenerMap = new HashMap<>();
		}
		return updateListenerMap;
	}

	/**
	 * @return object click listeners
	 */
	public HashMap<GeoElement, JsScript> getClickListenerMap() {
		if (clickListenerMap == null) {
			clickListenerMap = new HashMap<>();
		}
		return clickListenerMap;
	}

	public void setGlobalScript() {
		// to be overridden
	}

	/**
	 * @return whether there are some listeners
	 */
	public boolean hasListeners() {
		if (updateListenerMap != null && updateListenerMap.size() > 0) {
			return true;
		}
		if (clickListenerMap != null && clickListenerMap.size() > 0) {
			return true;
		}
		for (ArrayList<JsScript> a : listenerLists()) {
			if (a != null && a.size() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prevents listeners dropping on reset.
	 */
	public void keepListenersOnReset() {
		keepListenersOnReset = true;
	}

	/**
	 * Enables dropping listeners on reset.
	 */
	public void dropListenersOnReset() {
		keepListenersOnReset = false;
		rebuildListenerMap();
	}

	private void rebuildListenerMap() {
		clickListenerMap =  rebuildListenerMap(clickListenerMap);
		updateListenerMap = rebuildListenerMap(updateListenerMap);
	}

	private HashMap<GeoElement, JsScript> rebuildListenerMap(
			HashMap<GeoElement, JsScript> listenerMap) {

		if (listenerMap == null) {
			return null;
		}

		HashMap<GeoElement, JsScript> map = new HashMap<>();
		for (Map.Entry<GeoElement, JsScript> entry: listenerMap.entrySet()) {
			GeoElement oldGeo = entry.getKey();
			GeoElement newGeo = app.getKernel().lookupLabel(oldGeo.getLabelSimple());
			if (newGeo != null) {
				map.remove(oldGeo);
				map.put(newGeo, entry.getValue());
			}
		}
		return map;
	}

	public boolean isJsEnabled() {
		return jsEnabled;
	}

	public void setJsEnabled(boolean jsEnabled) {
		this.jsEnabled = jsEnabled;
	}

}
