package org.geogebra.desktop.euclidian.event;

import org.geogebra.common.euclidian.event.ActionListener;
import org.geogebra.common.euclidian.event.ActionListenerI;

public class ActionListenerD extends ActionListener implements
		java.awt.event.ActionListener {

	public ActionListenerD(ActionListenerI listener) {
		setListenerClass(listener);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		org.geogebra.desktop.euclidian.event.ActionEventD event = org.geogebra.desktop.euclidian.event.ActionEventD
				.wrapEvent(e);
		wrapActionPerformed(event);
	}

}
