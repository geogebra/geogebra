package geogebra.awt.event;

import java.util.LinkedList;

import geogebra.common.main.AbstractApplication;
import geogebra.awt.event.FocusEvent;

public class FocusEvent extends geogebra.common.awt.event.FocusEvent{

	public static LinkedList<FocusEvent> pool = new LinkedList<FocusEvent>();
	private java.awt.event.FocusEvent event;

	private FocusEvent(java.awt.event.FocusEvent e) {
		AbstractApplication.debug("possible missing release()");
		this.event = e;
	}
	
	public static geogebra.awt.event.FocusEvent wrapEvent(java.awt.event.FocusEvent e) {
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
