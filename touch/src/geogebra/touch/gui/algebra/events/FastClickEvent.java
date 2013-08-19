package geogebra.touch.gui.algebra.events;

import com.google.gwt.event.shared.GwtEvent;

public class FastClickEvent extends GwtEvent<FastClickHandler> {
	private static final Type<FastClickHandler> TYPE = new Type<FastClickHandler>();
	
	private boolean isDoubleClick;

	public FastClickEvent(boolean isDoubleClick) {
		this.isDoubleClick = isDoubleClick;
	}

	@Override
	public Type<FastClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final FastClickHandler handler) {
		if(this.isDoubleClick) {
			handler.onDoubleClick();
		}
		else {
			handler.onSingleClick();
		}
	}

	public static Type<FastClickHandler> getType() {
		return TYPE;
	}
}
