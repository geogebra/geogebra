package geogebra.euclidian.event;

import java.util.LinkedList;

import geogebra.common.main.AbstractApplication;
import geogebra.euclidian.event.FocusEvent;

public class FocusEvent extends geogebra.common.euclidian.event.FocusEvent{

	public static LinkedList<FocusEvent> pool = new LinkedList<FocusEvent>();
	private java.awt.event.FocusEvent event;

	private FocusEvent(java.awt.event.FocusEvent e) {
		AbstractApplication.debug("possible missing release()");
		this.event = e;
	}
	
	public static geogebra.euclidian.event.FocusEvent wrapEvent(java.awt.event.FocusEvent e) {
		if(!pool.isEmpty()){
			FocusEvent wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new FocusEvent(e);
	}
	
	public void release() {
		FocusEvent.pool.add(this);
	}
}
