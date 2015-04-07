package org.geogebra.common.move.operations;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.models.BaseModel;
import org.geogebra.common.move.views.BaseEventView;
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
	 * The Common view component to operate on (if exists) (eg.
	 * common.move.views.OfflineView)
	 */
	protected BaseView<T> view = null;
	/**
	 * The Common model component to operate on (if exists) (eg.
	 * common.move.models.LoginModel)
	 */
	protected BaseModel model = null;

	/**
	 * @return the Common View to operate on
	 */
	public BaseView<T> getView() {
		return view;
	}

	/**
	 * @param view
	 *            Common view to operate on
	 * 
	 *            Sets the Common view to operate on
	 */
	public void setView(BaseView<T> view) {
		this.view = view;
	}

	/**
	 * @return the Common model to operate on
	 */
	public BaseModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to operate on
	 * 
	 *            Sets the Common model to operate on
	 */
	public void setModel(BaseModel model) {
		this.model = model;
	}

	/**
	 * Informs the view and the model about an event
	 * 
	 * @param event
	 *            The Event to trigger
	 */
	public void onEvent(final BaseEvent event) {
		if (model != null) {
			model.onEvent(event);
		}

		if (view != null && view instanceof BaseEventView) {
			((BaseEventView) view).onEvent(event);
		}
	}

}
