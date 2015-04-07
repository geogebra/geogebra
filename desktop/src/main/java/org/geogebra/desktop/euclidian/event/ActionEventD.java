package org.geogebra.desktop.euclidian.event;

import org.geogebra.common.euclidian.event.ActionEvent;

public class ActionEventD extends ActionEvent {

	private java.awt.event.ActionEvent event;

	private ActionEventD(java.awt.event.ActionEvent e) {
		this.event = e;
	}

	public static ActionEventD wrapEvent(java.awt.event.ActionEvent e) {

		return new ActionEventD(e);
	}

}
