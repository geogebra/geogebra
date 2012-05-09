package geogebra.euclidian.event;

import geogebra.common.main.AbstractApplication;

import java.util.LinkedList;

public class ActionEvent extends geogebra.common.euclidian.event.ActionEvent{

	private java.awt.event.ActionEvent event;

	private ActionEvent(java.awt.event.ActionEvent e) {
		this.event = e;
	}
	
	public static geogebra.euclidian.event.ActionEvent wrapEvent(java.awt.event.ActionEvent e) {

		return new ActionEvent(e);
	}
	

}
