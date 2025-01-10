package org.geogebra.common.move.views;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.events.GenericEvent;

/**
 * Simple event bus
 * @param <T>
 *            type of handlers this view contains
 * @author gabor
 */
public class BaseView<T> {

	/**
	 * Views that need to be notified about events.
	 */
	protected List<T> viewComponents;

	/**
	 * Protected constructor.
	 */
	public BaseView() {
		viewComponents = new ArrayList<>();
	}

	/**
	 * @param view
	 *            Renderable view
	 * 
	 *            Removes a view from the views list
	 */
	public void remove(T view) {
		viewComponents.remove(view);
	}

	/**
	 * @param view
	 *            Renderable view
	 * 
	 *            Adds new view to the view's list
	 */
	public final void add(T view) {
		if (!viewComponents.contains(view)) {
			viewComponents.add(view);
		}
	}

	/**
	 * Notifies all view components of an event
	 *
	 * @param event
	 *            The event that occurred.
	 */
	public void onEvent(GenericEvent<T> event) {
		for (T view : viewComponents) {
			event.fire(view);
		}
	}
}
