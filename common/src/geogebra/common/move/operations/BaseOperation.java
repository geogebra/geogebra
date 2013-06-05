package geogebra.common.move.operations;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.models.BaseModel;
import geogebra.common.move.views.BaseView;

/**
 * @author gabor
 * 
 * Base class for all operations in Common
 *
 */
public abstract class BaseOperation {
	
	/**
	 * The Common view component to operate on (if exists)
	 * (eg. common.move.views.OfflineView)
	 */
	protected BaseView view = null;
	/**
	 * The Common model component to operate on (if exists)
	 * (eg. common.move.models.LoginModel)
	 */
	protected BaseModel model = null;
	/**
	 * The Common event component to operate on (if exists)
	 * (eg. common.move.views.OfflineEventPool)
	 */
	protected BaseEvent event = null;
	
	/**
	 * @return the Common View to operate on
	 */
	public BaseView getView() {
		return view;
	}
	/**
	 * @param view Common view to operate on
	 * 
	 * Sets the Common view to operate on
	 */
	public void setView(BaseView view) {
		this.view = view;
	}
	/**
	 * @return the Common model to operate on
	 */
	public BaseModel getModel() {
		return model;
	}
	/**
	 * @param model the model to operate on
	 * 
	 * Sets the Common model to operate on
	 */
	public void setModel(BaseModel model) {
		this.model = model;
	}
	/**
	 * @return the Common event to operate on
	 */
	public BaseEvent getEvent() {
		return event;
	}
	/**
	 * @param event Common event to operate on
	 * Sets the common event to operate on
	 */
	public void setEvent(BaseEvent event) {
		this.event = event;
	}
	
}
