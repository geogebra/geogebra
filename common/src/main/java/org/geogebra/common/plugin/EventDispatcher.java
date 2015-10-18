package org.geogebra.common.plugin;

import java.util.ArrayList;

import org.geogebra.common.kernel.ClientView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.GeoGebraProfiler;

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
public class EventDispatcher implements ClientView {

	private App app;
	private ArrayList<EventListener> listeners = new ArrayList<EventListener>();

	/**
	 * @param app
	 *            application
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
		app.getKernel().getConstruction().setSelfGeo(evt.target);
		for (EventListener listener : listeners) {
			listener.sendEvent(evt);
		}
		app.getKernel().getConstruction().setSelfGeo(null);
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
		if ((null != geo) && !geo.isLabelSet() && !geo.isGeoCasCell()) {
			return;
		}
		dispatchEvent(new Event(evtType, geo));
	}

	/**
	 * @param evtType
	 *            event type
	 * @param geos
	 *            multiple targets
	 */
	public void dispatchBulkEvent(EventType evtType, ArrayList<GeoElement> geos) {
		dispatchEvent(new Event(evtType, null, null, geos));
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
		long start = System.currentTimeMillis();
		dispatchEvent(EventType.UPDATE, geo);
		GeoGebraProfiler.addEvent(System.currentTimeMillis() - start);
	}

	public void updateVisualStyle(GeoElement geo) {
		dispatchEvent(EventType.UPDATE_STYLE, geo);
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

	public void setMode(int mode, ModeSetter m) {
		this.dispatchEvent(new Event(EventType.SET_MODE, null, mode + ""));

	}

	public int getViewID() {
		return App.VIEW_EVENT_DISPATCHER;
	}

	public boolean hasFocus() {
		return false;
	}

	public boolean isShowing() {
		return false;
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void renameUpdatesComplete() {
		dispatchEvent(EventType.RENAME_COMPLETE, null);
	}

	public void addingPolygon() {
		dispatchEvent(EventType.ADD_POLYGON, null);
	}

	public void addPolygonComplete(GeoElement polygon) {
		dispatchEvent(EventType.ADD_POLYGON_COMPLETE, polygon);
	}

	public void movingGeos() {
		dispatchEvent(EventType.MOVING_GEOS, null);
	}

	public void movedGeos(ArrayList<GeoElement> elms) {
		dispatchBulkEvent(EventType.MOVED_GEOS, elms);
	}

	public void deleteGeos(ArrayList<GeoElement> elms) {
		dispatchBulkEvent(EventType.DELETE_GEOS, elms);
	}

	public void pasteElms() {
		dispatchEvent(EventType.PASTE_ELMS, null);
	}

	public void pasteElmsComplete(ArrayList<GeoElement> pastedElms) {
		dispatchEvent(EventType.PASTE_ELMS_COMPLETE, null);
	}

	public boolean suggestRepaint() {
		return false;
		// not used for this view
	}

}
