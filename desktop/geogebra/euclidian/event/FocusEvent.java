package geogebra.euclidian.event;

import geogebra.common.main.App;

import java.util.LinkedList;

public class FocusEvent extends geogebra.common.euclidian.event.FocusEvent{

	public static LinkedList<FocusEvent> pool = new LinkedList<FocusEvent>();
	private java.awt.event.FocusEvent event;

	private FocusEvent(java.awt.event.FocusEvent e) {
		App.debug("possible missing release()");
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
