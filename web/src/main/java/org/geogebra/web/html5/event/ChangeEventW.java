package org.geogebra.web.html5.event;

import org.geogebra.common.euclidian.event.ActionEvent;

import com.google.gwt.event.dom.client.ChangeEvent;

public class ChangeEventW extends ActionEvent {
	private ChangeEvent event;

	public ChangeEventW(ChangeEvent e) {
		event = e;
	}

	public static ChangeEventW wrapEvent(ChangeEvent e) {
		return new ChangeEventW(e);
	}
}
