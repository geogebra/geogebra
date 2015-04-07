package org.geogebra.web.phone.gui.event;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class EventUtil {
	private static final EventBus eventBus = GWT.create(SimpleEventBus.class);

	public static HandlerRegistration addViewChangeHandler(
			Type<ViewChangeHandler> type, ViewChangeHandler handler) {
		return eventBus.addHandler(type, handler);
	}

	public static void fireEvent(Event<?> event) {
		eventBus.fireEvent(event);
	}
}
