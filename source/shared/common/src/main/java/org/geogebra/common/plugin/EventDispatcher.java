package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.ClientView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.algos.AlgoElement;
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
	private final ArrayList<EventListener> listeners = new ArrayList<>();
	protected boolean listenersEnabled = true;

	private final Set<ScriptType> disabledScriptTypes = new HashSet<>();

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
		if (listenersEnabled) {
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
	}

	/**
	 * Disable specified script type to run.
	 *
	 * @param scriptType to disable.
	 */
	public void disableScriptType(ScriptType scriptType) {
		disabledScriptTypes.add(scriptType);
	}

	/**
	 *
	 * @param scriptType to check.
	 * @return if scriptType is allowed to run.
	 */
	public boolean isDisabled(ScriptType scriptType) {
		return disabledScriptTypes.contains(scriptType);
	}

	/**
	 * Disable all listeners.
	 */
	public void disableListeners() {
		listenersEnabled = false;
	}

	/**
	 * Enable all listeners.
	 */
	public void enableListeners() {
		listenersEnabled = true;
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
		if (shouldNotDispatchFor(geo)) {
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
		if ((null != geo) && shouldNotDispatchFor(geo)) {
			return;
		}
		dispatchEvent(new Event(evtType, geo));
	}

	private boolean shouldNotDispatchFor(GeoElement geo) {
		return !geo.isLabelSet() && !geo.isGeoCasCell() || geo.isSpotlight();
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
	public void batchAddStarted() {
		dispatchEvent(EventType.BATCH_ADD_STARTED, null);
	}

	@Override
	public void batchAddComplete(GeoElement polygon) {
		dispatchEvent(EventType.BATCH_ADD_COMPLETE, polygon);
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
	public void startAnimation() {
		dispatchEvent(new Event(EventType.START_ANIMATION));
	}

	@Override
	public void stopAnimation() {
		dispatchEvent(new Event(EventType.STOP_ANIMATION));
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
	public void lockTextElement(GeoElement geo) {
		dispatchEvent(new Event(EventType.LOCK_TEXT_ELEMENT, geo));
	}

	@Override
	public void unlockTextElement(GeoElement geo) {
		dispatchEvent(new Event(EventType.UNLOCK_TEXT_ELEMENT, geo));
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// not used for this view
	}

	/**
	 * Notify update listeners about update of a geo and all of its descendants
	 * @param root cascade root
	 */
	public void notifyListenersUpdateCascade(GeoElementND root) {
		TreeSet<GeoElement> updated = new TreeSet<>();
		updated.add(root.toGeoElement());
		if (root.hasAlgoUpdateSet()) {
			for (AlgoElement algo : root.getAlgoUpdateSet()) {
				updated.addAll(Arrays.asList(algo.getOutput()));
			}
		}
		for (GeoElement el: updated) {
			if (el.isLabelSet()) {
				dispatchEvent(new Event(EventType.UPDATE, el));
			}
		}
	}

	/**
	 * @return list of available script types
	 */
	public List<ScriptType> availableTypes() {
		return Arrays.stream(ScriptType.values())
				.filter(t -> !disabledScriptTypes.contains(t)).collect(Collectors.toList());
	}

}
