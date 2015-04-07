package org.geogebra.web.html5.event;

import org.geogebra.common.euclidian.event.ActionEvent;

public class ActionEventW extends ActionEvent {

	private com.google.gwt.user.client.Event event;

	public ActionEventW(com.google.gwt.user.client.Event e) {
		this.event = e;
	}

	public static ActionEventW wrapEvent(com.google.gwt.user.client.Event e) {

		return new ActionEventW(e);
	}
}
