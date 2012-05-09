package geogebra.euclidian.event;

import geogebra.common.euclidian.event.ActionEvent;

public class ActionListener extends geogebra.common.euclidian.event.ActionListener
implements java.awt.event.ActionListener{

	public ActionListener(Object listener) {
		setListenerClass(listener);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		geogebra.euclidian.event.ActionEvent event = geogebra.euclidian.event.ActionEvent.wrapEvent(e);
		wrapActionPerformed(event);
	}
	



}
