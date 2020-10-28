package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

public abstract class ScriptManager implements EventListener {

	@Weak
	protected App app;
	protected boolean listenersEnabled = true;
	protected boolean jsEnabled = true;
	// maps between GeoElement and JavaScript function names
	protected HashMap<GeoElement, JsReference> updateListenerMap;
	protected HashMap<GeoElement, JsReference> clickListenerMap;
	protected final ArrayList<JsReference> addListeners = new ArrayList<>();
	protected final ArrayList<JsReference> storeUndoListeners = new ArrayList<>();
	protected final ArrayList<JsReference> removeListeners = new ArrayList<>();
	protected final ArrayList<JsReference> renameListeners = new ArrayList<>();
	protected final ArrayList<JsReference> updateListeners = new ArrayList<>();
	protected final ArrayList<JsReference> clickListeners = new ArrayList<>();
	protected final ArrayList<JsReference> clearListeners = new ArrayList<>();
	protected final ArrayList<JsReference> clientListeners = new ArrayList<>();
	private boolean keepListenersOnReset = true;

	private ArrayList<JsReference>[] listenerLists() {
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

	private void callListeners(List<JsReference> listeners, Event evt) {
		for (JsReference listener : listeners) {
			callListener(listener, evt);
		}
	}

	private void callListener(JsReference listener, Event evt) {
		if (listener != null) {
			GeoElement geo = evt.target;
			if (geo == null) {
				callListener(listener);
				return;
			}
			String label = geo.getLabel(StringTemplate.defaultTemplate);
			if (evt.type == EventType.RENAME) {
				callListener(listener, geo.getOldLabel(), label);
				return;
			} else if (evt.argument == null) {
				callListener(listener, label);
				return;
			}
			callListener(listener, evt.argument);
		}
	}

	protected final void callListener(JsReference fn, Object... args) {
		try {
			if (fn.getNativeRunnable() != null) {
				callNativeListener(fn.getNativeRunnable(), args);
			} else {
				callListener(fn.getText(), args);
			}
		} catch (Exception e) {
			Log.error("Scripting error " + e.getMessage());
		}
	}

	protected void callNativeListener(Object nativeRunnable, Object[] args) {
		// in desktop and web
	}

	protected void callListener(String fn, Object[] args) {
		// implemented in web and desktop
	}

	protected void callClientListeners(List<JsReference> listeners, Event evt) {
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

		for (ArrayList<JsReference> a : listenerLists()) {
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
	public synchronized void registerAddListener(Object JSFunctionName) {
		registerGlobalListener(addListeners, JSFunctionName);
	}

	/**
	 * Registers a JavaScript function as an add listener for the applet's
	 * construction. Whenever a new object is created in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the newly created object as a single argument.
	 */
	public synchronized void registerStoreUndoListener(Object JSFunctionName) {
		if (!app.isUndoActive()) {
			app.getKernel().setUndoActive(true);
			app.getKernel().initUndoInfo();
		}
		registerGlobalListener(storeUndoListeners, JSFunctionName);
	}

	public synchronized void unregisterStoreUndoListener(Object JSFunctionName) {
		storeUndoListeners.remove(JsReference.fromNative(JSFunctionName));
	}

	private void registerGlobalListener(ArrayList<JsReference> listenerList,
			Object jsFunctionName) {
		if (jsFunctionName == null || isEmptyString(jsFunctionName)) {
			return;
		}

		// init list
		if (listenerList != null) {
			listenerList.add(JsReference.fromNative(jsFunctionName));
		}
	}

	private boolean isEmptyString(Object jsFunctionName) {
		return (jsFunctionName instanceof String
				&& ((String) jsFunctionName).length() == 0);
	}

	/**
	 * Removes a previously registered add listener
	 * 
	 * @see #registerAddListener(Object)
	 */
	public synchronized void unregisterAddListener(Object JSFunctionName) {
		addListeners.remove(JsReference.fromNative(JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a remove listener for the applet's
	 * construction. Whenever an object is deleted in the GeoGebraApplet's
	 * construction, the JavaScript function jsFunction is called using the
	 * name of the deleted object as a single argument.
	 */
	public synchronized void registerRemoveListener(Object jsFunction) {
		registerGlobalListener(removeListeners, jsFunction);
	}

	/**
	 * Removes a previously registered remove listener
	 * 
	 * @see #registerRemoveListener(Object)
	 */
	public synchronized void unregisterRemoveListener(Object jsFunction) {
		removeListeners.remove(JsReference.fromNative(jsFunction));
	}

	/**
	 * Registers a JavaScript function as a clear listener for the applet's
	 * construction. Whenever the construction in the GeoGebraApplet's is
	 * cleared (i.e. all objects are removed), the JavaScript function
	 * JSFunctionName is called using no arguments.
	 */
	public synchronized void registerClearListener(Object JSFunctionName) {
		registerGlobalListener(clearListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered clear listener
	 * 
	 * @see #registerClearListener(Object)
	 */
	public synchronized void unregisterClearListener(Object JSFunctionName) {
			clearListeners.remove(JsReference.fromNative(JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a rename listener for the applet's
	 * construction. Whenever an object is renamed in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the deleted object as a single argument.
	 */
	public synchronized void registerRenameListener(Object JSFunctionName) {
		registerGlobalListener(renameListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered rename listener.
	 * 
	 * @see #registerRenameListener(Object)
	 */
	public synchronized void unregisterRenameListener(Object JSFunctionName) {
		renameListeners.remove(JsReference.fromNative(JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as an update listener for the applet's
	 * construction. Whenever any object is updated in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the updated object as a single argument.
	 */
	public synchronized void registerUpdateListener(Object JSFunctionName) {
		registerGlobalListener(updateListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered update listener.
	 * 
	 * @see #registerRemoveListener(Object)
	 */
	public synchronized void unregisterUpdateListener(Object JSFunctionName) {
		updateListeners.remove(JsReference.fromNative(JSFunctionName));
	}

	/**
	 * Registers a JavaScript function as a click listener for the applet's
	 * construction. Whenever any object is click in the GeoGebraApplet's
	 * construction, the JavaScript function JSFunctionName is called using the
	 * name of the clicked object as a single argument.
	 */
	public synchronized void registerClickListener(Object JSFunctionName) {
		registerGlobalListener(clickListeners, JSFunctionName);
	}

	/**
	 * Removes a previously registered click listener.
	 * 
	 * @see #registerRemoveListener(Object)
	 */
	public synchronized void unregisterClickListener(Object JSFunctionName) {
		clickListeners.remove(JsReference.fromNative(JSFunctionName));
	}

	/**
	 * Registers a JS function to be notified of client events.
	 * 
	 * @param jsFunctionName
	 *            client listener name
	 */
	public synchronized void registerClientListener(Object jsFunctionName) {
		registerGlobalListener(clientListeners, jsFunctionName);
	}

	/**
	 * @param jsFunctionName
	 *            client listener name
	 */
	public synchronized void unregisterClientListener(Object jsFunctionName) {
		clientListeners.remove(JsReference.fromNative(jsFunctionName));
	}

	/**
	 * Registers a JavaScript listener for an object. Whenever the object with
	 * the given name changes, a JavaScript function named JSFunctionName is
	 * called using the name of the changed object as the single argument. If
	 * objName previously had a mapping JavaScript function, the old value is
	 * replaced.
	 */
	private synchronized HashMap<GeoElement, JsReference> registerObjectListener(
			HashMap<GeoElement, JsReference> map0, String objName,
			Object JSFunctionName) {
		if (JSFunctionName == null || isEmptyString(JSFunctionName)) {
			return map0;
		}
		GeoElement geo = app.getKernel().lookupLabel(objName);
		if (geo == null) {
			return map0;
		}

		HashMap<GeoElement, JsReference> map = map0;
		if (map == null) {
			map = new HashMap<>();
		}
		Log.debug(JSFunctionName);
		map.put(geo, JsReference.fromNative(JSFunctionName));
		return map;
	}

	/**
	 * Removes a previously set object listener for the given object.
	 */
	private synchronized void unregisterObjectListener(
			HashMap<GeoElement, JsReference> map, String objName) {
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
	public void registerObjectUpdateListener(String objName, Object fName) {
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
	public void registerObjectClickListener(String objName, Object fName) {
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
	public ArrayList<JsReference> getAddListeners() {
		return addListeners;
	}

	/**
	 * @return strore undo listeners
	 */
	public ArrayList<JsReference> getStoreUndoListeners() {
		return storeUndoListeners;
	}

	/**
	 * @return remove listeners
	 */
	public ArrayList<JsReference> getRemoveListeners() {
		return removeListeners;
	}

	/**
	 * @return rename listeners
	 */
	public ArrayList<JsReference> getRenameListeners() {
		return renameListeners;
	}

	/**
	 * @return update listeners
	 */
	public ArrayList<JsReference> getupdateListeners() {
		return updateListeners;
	}

	/**
	 * @return clear listeners
	 */
	public ArrayList<JsReference> getClearListeners() {
		return clearListeners;
	}

	/**
	 * @return object update listeners
	 */
	public HashMap<GeoElement, JsReference> getUpdateListenerMap() {
		if (updateListenerMap == null) {
			updateListenerMap = new HashMap<>();
		}
		return updateListenerMap;
	}

	/**
	 * @return object click listeners
	 */
	public HashMap<GeoElement, JsReference> getClickListenerMap() {
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
		for (ArrayList<JsReference> a : listenerLists()) {
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

	private HashMap<GeoElement, JsReference> rebuildListenerMap(
			HashMap<GeoElement, JsReference> listenerMap) {

		if (listenerMap == null) {
			return null;
		}

		HashMap<GeoElement, JsReference> map = new HashMap<>();
		for (Map.Entry<GeoElement, JsReference> entry: listenerMap.entrySet()) {
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
