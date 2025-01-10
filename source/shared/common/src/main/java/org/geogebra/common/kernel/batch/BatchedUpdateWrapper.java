package org.geogebra.common.kernel.batch;

import java.util.Iterator;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.CheckBeforeUpdateView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.Reflection;
import org.geogebra.common.util.debug.Log;

/**
 * This class can wrap a view, and post the notifications
 * in a batch every DELAY seconds.
 */
public class BatchedUpdateWrapper
		implements CheckBeforeUpdateView, GTimerListener {

	private static final int DELAY = 80;

	private final CheckBeforeUpdateView wrappedView;
	private final Reflection reflection;
	private final EventOptimizedList pendingEvents;
	private final GTimer timer;

	/**
	 * Create a wrapper around View.
	 *
	 * @param wrappedView view to wrap
	 * @param factory factory
	 */
	public BatchedUpdateWrapper(WrappableView wrappedView,
			UtilFactory factory) {
		this.wrappedView = wrappedView;
		wrappedView.setIsWrapped(true);
		this.reflection = factory.newReflection(View.class);

		pendingEvents = new EventOptimizedList();
		timer = factory.newTimer(this, DELAY);
	}

	private void addEvent(String name) {
		addEvent(name, new Object[] {});
	}

	private void addEvent(String name, GeoElement parameter) {
		if (show(parameter)) {
			Event event = new Event(name, new Object[] { parameter });
			addEvent(event);
		}
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
		if (needsUpdateVisualstyle(prop) && show(geo)) {
			addEvent("updateVisualStyle", new Object[] { geo, prop });
		}
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		addEvent("updateHighlight", (GeoElement) geo);
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
		// always in batch mode
	}

	@Override
	public void endBatchUpdate() {
		// always in batch mode
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		addEvent("updatePreviewFromInputBar", new Object[] { geos });
	}

	@Override
	public void onRun() {
		EventOptimizedList copiedEvents = pendingEvents.copy();
		pendingEvents.clear();

		wrappedView.startBatchUpdate();

		Iterator<Event> iterator = copiedEvents.iterator();
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
		wrappedView.endBatchUpdate();
	}

	@Override
	public boolean needsUpdateVisualstyle(GProperty property) {
		return wrappedView.needsUpdateVisualstyle(property);
	}

	@Override
	public boolean show(GeoElement geo) {
		return wrappedView.show(geo);
	}
}
