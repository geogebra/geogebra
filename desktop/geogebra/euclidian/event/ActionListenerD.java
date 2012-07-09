package geogebra.euclidian.event;

import geogebra.common.euclidian.event.ActionEvent;
import geogebra.common.euclidian.event.ActionListenerI;
import geogebra.common.euclidian.event.ActionListener;


public class ActionListenerD extends ActionListener
implements java.awt.event.ActionListener{

	public ActionListenerD(ActionListenerI listener) {
		setListenerClass(listener);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		geogebra.euclidian.event.ActionEventD event = geogebra.euclidian.event.ActionEventD.wrapEvent(e);
		wrapActionPerformed(event);
	}
	



}
