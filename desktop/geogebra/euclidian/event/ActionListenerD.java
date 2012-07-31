package geogebra.euclidian.event;

import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.euclidian.event.ActionListenerI;


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
