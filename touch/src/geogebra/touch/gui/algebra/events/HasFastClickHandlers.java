package geogebra.touch.gui.algebra.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasFastClickHandlers extends HasHandlers {
	HandlerRegistration addFastClickHandler(FastClickHandler handler);
}
