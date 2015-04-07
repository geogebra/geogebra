package org.geogebra.web.phone.gui.event;

import org.geogebra.web.phone.gui.view.View;

import com.google.web.bindery.event.shared.Event;

public class ViewChangeEvent extends Event<ViewChangeHandler> {

	private static final Type<ViewChangeHandler> TYPE = new Type<ViewChangeHandler>();
	
	public static Type<ViewChangeHandler> getType() {
		return TYPE;
	}
	
	public View view;
	
	public ViewChangeEvent(View view) {
		this.view = view;
	}

	public View getView() {
		return view;
	}

	@Override
	public Type<ViewChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewChangeHandler handler) {
		handler.onViewChange(this);
	}
}
