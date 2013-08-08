package geogebra.touch.gui.algebra.events;

import com.google.gwt.event.shared.GwtEvent;

public class FastClickEvent extends GwtEvent<FastClickHandler> {

	private static final Type<FastClickHandler> TYPE = new Type<FastClickHandler>();

	@Override
	public Type<FastClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final FastClickHandler handler) {
		handler.onFastClick(this);
	}

	public static Type<FastClickHandler> getType() {
		return TYPE;
	}
}
