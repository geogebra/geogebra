package geogebra.common.plugin;

import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * Every event scripting machinery must implement the EventListener interface
 * and register with the application's event dispatcher (via
 * app.getEventDispatcher().addEventListener(...)). It will then obtain events
 * via the sendEvent() method.
 * 
 * EventDispatcher registers itself as a view so it can listen to most event
 * types and forward them to listeners. Click events are handled differently
 * because they are not part of the View interface (the clicked GeoElement is
 * responsible for relaying the event to the event dispatcher)
 * 
 * @author arno
 * 
 */
public class EventDispatcher implements View {

	private App app;
	private ArrayList<EventListener> listeners = new ArrayList<EventListener>();

	/**
	 * @param app
	 */
	public EventDispatcher(App app) {
		this.app = app;
		app.getKernel().attach(this);
	}

	/**
	 * Add a new EventListener object
	 * 
	 * @param listener
	 *            the object that wants to receive notifications of events
	 */
	public void addEventListener(EventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Dispatch an event to all registered event listeners
	 * 
	 * @param evt
	 *            the event to be dispatched
	 */
	public void dispatchEvent(Event evt) {		
		for (EventListener listener : listeners) {
			listener.sendEvent(evt);
		}
	}

	/**
	 * Convenience method for dispatching an event
	 * 
	 * @param evtType
	 *            the type of the event
	 * @param geo
	 *            the target of the event
	 * @param arg
	 *            an extra argument
	 */
	public void dispatchEvent(EventType evtType, GeoElement geo, String arg) {
		if (!geo.isLabelSet()) {
			return;
		}
		dispatchEvent(new Event(evtType, geo, arg));
	}

	/**
	 * Convenience method for dispatching an event
	 * 
	 * @param evtType
	 *            the type of the event
	 * @param geo
	 *            the target of the event
	 */
	public void dispatchEvent(EventType evtType, GeoElement geo) {
		if (!geo.isLabelSet()) {
			return;
		}
		dispatchEvent(new Event(evtType, geo));
	}

	/*
	 * Implementation of View
	 */

	public void add(GeoElement geo) {
		dispatchEvent(EventType.ADD, geo);
	}

	public void remove(GeoElement geo) {
		dispatchEvent(EventType.REMOVE, geo);
	}

	public void rename(GeoElement geo) {
		dispatchEvent(EventType.RENAME, geo);
	}

	public void update(GeoElement geo) {
		dispatchEvent(EventType.UPDATE, geo);
	}

	public void updateVisualStyle(GeoElement geo) {
		// Ignore
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// Ignore
	}

	public void repaintView() {
		// Ignore

	}

	public void reset() {
		// Ignore

	}

	public void clearView() {
		// As I understand it, this happens when a new file is started. This is
		// the time to call the reset() function of the registered event
		// listeners.
		for (EventListener listener : listeners) {
			listener.reset();
		}
	}

	public void setMode(int mode,ModeSetter m) {
		// TODO Could be useful?

	}

	public int getViewID() {
		return App.VIEW_NONE;
	}

	public boolean hasFocus() {
		return false;
	}

	public void repaint() {
		// Ignore

	}

	public boolean isShowing() {
		return false;
	}

}
