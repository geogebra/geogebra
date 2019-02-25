package org.geogebra.common.kernel.batch;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.Reflection;
import org.geogebra.common.util.debug.Log;

import java.util.Iterator;

/**
 * This class can wrap a view, and post the notifications
 * in a batch every DELAY seconds.
 */
public class BatchedUpdateWrapper implements View, GTimerListener {

	private static final int DELAY = 80;

	private final View wrappedView;
	private final Reflection reflection;
	private final EventOptimizedList pendingEvents;
	private final GTimer timer;

	/**
	 * Create a wrapper around View.
	 *
	 * @param wrappedView view to wrap
	 * @param app app
	 */
	public BatchedUpdateWrapper(View wrappedView, App app) {
		this.wrappedView = wrappedView;
		this.reflection = app.createReflection(wrappedView.getClass());

		pendingEvents = new EventOptimizedList();
		timer = app.newTimer(this, DELAY);
	}

	private void addEvent(String name) {
		addEvent(name, (Object[]) null);
	}

	private void addEvent(String name, GeoElementND parameter) {
		Event event = new Event(name, new Object[] { parameter });
		addEvent(event);
	}

	private void addEvent(String name, Object[] parameters) {
		Event event = new Event(name, parameters);
		addEvent(event);
	}

	private void addEvent(Event event) {
		pendingEvents.add(event);
		if (!timer.isRunning()) {
			timer.start();
		}
	}

	@Override
	public void add(GeoElement geo) {
		addEvent("add", geo);
	}

	@Override
	public void remove(GeoElement geo) {
		addEvent("remove", geo);
	}

	@Override
	public void rename(GeoElement geo) {
		addEvent("rename", geo);
	}

	@Override
	public void update(GeoElement geo) {
		addEvent("update", geo);
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		addEvent("updateVisualStyle", new Object[] { geo, prop });
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		addEvent("updateHighlight", geo);
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		addEvent("updateAuxiliaryObject", geo);
	}

	@Override
	public void repaintView() {
		addEvent("repaintView");
	}

	@Override
	public boolean suggestRepaint() {
		return wrappedView.suggestRepaint();
	}

	@Override
	public void reset() {
		wrappedView.reset();
	}

	@Override
	public void clearView() {
		pendingEvents.clear();
		wrappedView.clearView();
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		wrappedView.setMode(mode, m);
	}

	@Override
	public int getViewID() {
		return wrappedView.getViewID();
	}

	@Override
	public boolean hasFocus() {
		return wrappedView.hasFocus();
	}

	@Override
	public void startBatchUpdate() {
		wrappedView.startBatchUpdate();
		// always in batch mode
	}

	@Override
	public void endBatchUpdate() {
		wrappedView.endBatchUpdate();
		// always in batch mode
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		addEvent("updatePreviewFromInputBar", new Object[] { geos });
	}

	@Override
	public void onRun() {
		Iterator<Event> iterator = pendingEvents.iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			String name = event.getName();
			Object[] parameters = event.getParameters();
			try {
				reflection.call(wrappedView, name, parameters);
			} catch (Exception e) {
				Log.debug(e);
			}
			iterator.remove();
		}
	}
}
