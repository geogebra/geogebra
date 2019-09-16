package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	protected ArrayList<JsScript> addListeners = new ArrayList<>();
	protected ArrayList<JsScript> storeUndoListeners = new ArrayList<>();
	protected ArrayList<JsScript> removeListeners = new ArrayList<>();
	protected ArrayList<JsScript> renameListeners = new ArrayList<>();
	protected ArrayList<JsScript> updateListeners = new ArrayList<>();
	protected ArrayList<JsScript> clickListeners = new ArrayList<>();
	protected ArrayList<JsScript> clearListeners = new ArrayList<>();
	protected ArrayList<JsScript> clientListeners = new ArrayList<>();

	private ArrayList[] listenerLists() {
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
	public ScriptManager(App app) {
		this.app = app;
		app.getEventDispatcher().addEventListener(this);
	}

	@Override
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
		case CLEAR:
			callListeners(clearListeners, evt);
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
		case UNDO:
		case REDO:
		case OPEN_MENU:
		case OPEN_DIALOG:
		case EXPORT:
		case ADD_MACRO:
		case REMOVE_MACRO:
		case EDITOR_KEY_TYPED:
		case EDITOR_START:
		case EDITOR_STOP:
		case ALGEBRA_PANEL_SELECTED:
		case TOOLS_PANEL_SELECTED:
		case TABLE_PANEL_SELECTED:
		case SIDE_PANEL_OPENED:
		case SIDE_PANEL_CLOSED:
		case VIEW_CHANGED_2D:
		case VIEW_CHANGED_3D:
		case MOUSE_DOWN:
		case DRAG_END:
			callClientListeners(clientListeners, evt);
			break;
		// TODO case CLEAR
		default:
			Log.debug("Unknown event type");
		}
	}

	private void callListeners(List<JsScript> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}
		for (JsScript listener : listeners) {
			callListener(listener, evt);
		}
	}

	/**
	 * This method is package-private for tests only.
	 * @param listeners listeners
	 * @param evt event
	 */
	void callClientListeners(List<JsScript> listeners, Event evt) {
		if (listeners.isEmpty()) {
			return;
		}

		ArrayList<String> args = new ArrayList<>();
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
			callJavaScript(listener.getText(), args.toArray(new String[0]), evt.jsonArgument);
		}
	}

	public void callJavaScript(String jsFunction, String[] arguments, Map<String, Object> jsonArgument) {
		callJavaScript(jsFunction, arguments);
	}

	private void callListener(JsScript listener, Event evt) {
		if (listener != null) {
			String fn = listener.getText();
			GeoElement geo = evt.target;
			if (geo == null) {
				callJavaScript(fn, (String) null, null);
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
	@Override
	public void reset() {

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

	private void registerGlobalListener(ArrayList<JsScript> listenerList,
			String jSFunctionName) {
		if (jSFunctionName == null || jSFunctionName.length() == 0) {
			return;
		}

		initJavaScript();

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
		registerGlobalListener(removeListeners, JSFunctionName);
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
		registerGlobalListener(clearListeners, JSFunctionName);
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
		registerGlobalListener(renameListeners, JSFunctionName);
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
		registerGlobalListener(updateListeners, JSFunctionName);
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
		registerGlobalListener(clickListeners, JSFunctionName);
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
		if (clientListeners != null) {
			clientListeners.remove(JsScript.fromName(app, jsFunctionName));
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
			HashMap<GeoElement, JsScript> map0, String objName,
			String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0) {
			return map0;
		}
		GeoElement geo = app.getKernel().lookupLabel(objName);
		if (geo == null) {
			return map0;
		}
		initJavaScript();
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

	public synchronized void initJavaScript() {
		// overridden in platforms
	}

	abstract public void callJavaScript(String jsFunction, String[] args);

	/**
	 * @param jsFunction
	 *            function name
	 * @param arg0
	 *            first argument
	 * @param arg1
	 *            second argument
	 */
	public void callJavaScript(String jsFunction, String arg0, String arg1) {
		if (arg0 == null) {
			callJavaScript(jsFunction, new String[0]);
			return;
		}
		if (arg1 == null) {
			callJavaScript(jsFunction, new String[] { arg0 });
			return;
		}
		callJavaScript(jsFunction, new String[] { arg0, arg1 });
	}

	// ------ getters for listeners -------------

	/**
	 * @return add listeners
	 */
	public ArrayList<JsScript> getAddListeners() {
		if (addListeners == null) {
			addListeners = new ArrayList<>();
		}
		return addListeners;
	}

	/**
	 * @return strore undo listeners
	 */
	public ArrayList<JsScript> getStoreUndoListeners() {
		if (storeUndoListeners == null) {
			storeUndoListeners = new ArrayList<>();
		}
		return storeUndoListeners;
	}

	/**
	 * @return remove listeners
	 */
	public ArrayList<JsScript> getRemoveListeners() {
		if (removeListeners == null) {
			removeListeners = new ArrayList<>();
		}
		return removeListeners;
	}

	/**
	 * @return rename listeners
	 */
	public ArrayList<JsScript> getRenameListeners() {
		if (renameListeners == null) {
			renameListeners = new ArrayList<>();
		}
		return renameListeners;
	}

	/**
	 * @return update listeners
	 */
	public ArrayList<JsScript> getupdateListeners() {
		if (updateListeners == null) {
			updateListeners = new ArrayList<>();
		}
		return updateListeners;
	}

	/**
	 * @return clear listeners
	 */
	public ArrayList<JsScript> getClearListeners() {
		if (clearListeners == null) {
			clearListeners = new ArrayList<>();
		}
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
		for (ArrayList a : listenerLists()) {
			if (a != null && a.size() > 0) {
				return true;
			}
		}
		return false;
	}

}
