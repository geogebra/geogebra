package geogebra.euclidian.event;

import geogebra.common.euclidian.event.ActionEvent;
import geogebra.common.euclidian.event.ActionListenerI;


public class ActionListener extends geogebra.common.euclidian.event.ActionListener
implements java.awt.event.ActionListener{

	public ActionListener(ActionListenerI listener) {
		setListenerClass(listener);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		geogebra.euclidian.event.ActionEvent event = geogebra.euclidian.event.ActionEvent.wrapEvent(e);
		wrapActionPerformed(event);
	}
	



}
