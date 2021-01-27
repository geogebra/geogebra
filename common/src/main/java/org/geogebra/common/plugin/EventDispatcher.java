package org.geogebra.common.plugin;

import java.util.ArrayList;

import org.geogebra.common.kernel.ClientView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.GeoGebraProfiler;

import com.google.j2objc.annotations.Weak;

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

	@Weak
	private App app;
	private ArrayList<EventListener> listeners = new ArrayList<>();

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
	 * Remove an EventListener object
	 *
	 * @param listener
	 *            the object to remove
	 */
	public void removeEventListener(EventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * For tests only.
	 * @return listeners
	 */
	ArrayList<EventListener> getListeners() {
		return listeners;
	}

	/**
	 * Dispatch an event to all registered event listeners
	 * 
	 * @param evt
	 *            the event to be dispatched
	 */
	public void dispatchEvent(Event evt) {
		boolean affectsSelfGeo = app.getKernel().getConstruction() != null
				&& evt.target != null;
		if (affectsSelfGeo) {
			app.getKernel().getConstruction().setSelfGeo(evt.target);
		}
		for (EventListener listener : listeners) {
			listener.sendEvent(evt);
		}
		if (affectsSelfGeo) {
			app.getKernel().getConstruction().restoreSelfGeo();
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
	private void dispatchBulkEvent(EventType evtType, ArrayList<GeoElement> geos) {
		dispatchEvent(new Event(evtType, null, null, geos));
	}

	/*
	 * Implementation of View
	 */

	@Override
	public void add(GeoElement geo) {
		dispatchEvent(EventType.ADD, geo);
	}

	@Override
	public void remove(GeoElement geo) {
		dispatchEvent(EventType.REMOVE, geo);
	}

	@Override
	public void rename(GeoElement geo) {
		dispatchEvent(EventType.RENAME, geo);
	}

	@Override
	public void update(GeoElement geo) {
		long start = System.currentTimeMillis();
		dispatchEvent(EventType.UPDATE, geo);
		GeoGebraProfiler.addEvent(System.currentTimeMillis() - start);
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		if (prop != GProperty.TEXT_SELECTION) {
			dispatchEvent(EventType.UPDATE_STYLE, geo);
		}
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// not used
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// Ignore
	}

	@Override
	public void repaintView() {
		// Ignore

	}

	@Override
	public void reset() {
		// Ignore

	}

	@Override
	public void clearView() {
		// As I understand it, this happens when a new file is started. This is
		// the time to call the reset() function of the registered event
		// listeners.
		for (EventListener listener : listeners) {
			listener.reset();
		}
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		this.dispatchEvent(new Event(EventType.SET_MODE, null, mode + ""));

	}

	@Override
	public int getViewID() {
		return App.VIEW_EVENT_DISPATCHER;
	}

	@Override
	public boolean hasFocus() {
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

	@Override
	public void renameUpdatesComplete() {
		dispatchEvent(EventType.RENAME_COMPLETE, null);
	}

	@Override
	public void addingPolygon() {
		dispatchEvent(EventType.ADD_POLYGON, null);
	}

	@Override
	public void addPolygonComplete(GeoElement polygon) {
		dispatchEvent(EventType.ADD_POLYGON_COMPLETE, polygon);
	}

	@Override
	public void movingGeos() {
		dispatchEvent(EventType.MOVING_GEOS, null);
	}

	@Override
	public void movedGeos(ArrayList<GeoElement> elms) {
		dispatchBulkEvent(EventType.MOVED_GEOS, elms);
	}

	@Override
	public void deleteGeos(ArrayList<GeoElement> elms) {
		dispatchBulkEvent(EventType.DELETE_GEOS, elms);
	}

	@Override
	public void pasteElms(String pasteXml) {
		dispatchEvent(new Event(EventType.PASTE_ELMS, null, pasteXml));
	}

	@Override
	public void pasteElmsComplete(ArrayList<GeoElement> pastedElms) {
		dispatchBulkEvent(EventType.PASTE_ELMS_COMPLETE, pastedElms);
	}

	@Override
	public void startAnimation(GeoElement geo) {
		dispatchEvent(new Event(EventType.START_ANIMATION, geo));
	}

	@Override
	public void stopAnimation(GeoElement geo) {
		dispatchEvent(new Event(EventType.STOP_ANIMATION, geo));
	}

	@Override
	public void groupObjects(ArrayList<GeoElement> geos) {
		dispatchEvent(new Event(EventType.GROUP_OBJECTS, null, null, geos));
	}

	@Override
	public void ungroupObjects(ArrayList<GeoElement> geos) {
		dispatchEvent(new Event(EventType.UNGROUP_OBJECTS, null, null, geos));
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// not used for this view
	}

}
