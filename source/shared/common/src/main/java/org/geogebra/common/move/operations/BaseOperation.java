package org.geogebra.common.move.operations;

import org.geogebra.common.move.events.GenericEvent;
import org.geogebra.common.move.views.BaseView;

/**
 * @author gabor
 * 
 *         Base class for all operations in Common
 * @param <T>
 *            Type of handlers this operation notifies
 */
public abstract class BaseOperation<T> {

	/**
	 * The Common view component to operate on (if exists)
	 */
	private final BaseView<T> view = new BaseView<>();

	/**
	 * @return the Common View to operate on
	 */
	public BaseView<T> getView() {
		return view;
	}

	protected void dispatchEvent(GenericEvent<T> event) {
		view.onEvent(event);
	}

}
