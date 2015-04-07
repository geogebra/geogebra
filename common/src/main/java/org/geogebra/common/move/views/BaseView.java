package org.geogebra.common.move.views;

import java.util.ArrayList;

/**
 * @author gabor Base of all views
 * @param <T>
 *            type of handlers this view contains
 * 
 */
public abstract class BaseView<T> {

	/**
	 * 
	 */
	protected ArrayList<T> viewComponents = null;

	/**
	 * called from child objects.
	 */
	protected BaseView() {

	}

	/**
	 * @param view
	 *            Renderable view
	 * 
	 *            Removes a view from the views list
	 */
	public void remove(T view) {
		if (viewComponents != null) {
			if (viewComponents.contains(view)) {
				viewComponents.remove(view);
			}
		}
	}

	/**
	 * @param view
	 *            Renderable view
	 * 
	 *            Adds new view to the view's list
	 */
	public final void add(T view) {
		if (this.viewComponents == null) {
			this.viewComponents = new ArrayList<T>();
		}
		if (!this.viewComponents.contains(view)) {
			this.viewComponents.add(view);
		}
	}
}
