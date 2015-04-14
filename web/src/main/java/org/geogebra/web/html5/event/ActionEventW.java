package org.geogebra.web.html5.event;

import org.geogebra.common.euclidian.event.ActionEvent;

import com.google.gwt.user.client.Event;

public class ActionEventW extends ActionEvent {

	private Event event;

	public ActionEventW(com.google.gwt.user.client.Event e) {
		this.event = e;
	}

	public static ActionEventW wrapEvent(com.google.gwt.user.client.Event e) {

		return new ActionEventW(e);
	}
	
	public Event getEvent(){
		return event;
	}
}
