package org.geogebra.common.move.views;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gabor Base of all views
 * @param <T>
 *            type of handlers this view contains
 * 
 */
public abstract class BaseView<T> {

	/**
	 * Views that need to be notified about events.
	 */
	protected List<T> viewComponents;

	/**
	 * Protected constructor.
	 */
	protected BaseView() {
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
}
