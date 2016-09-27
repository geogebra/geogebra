package org.geogebra.common.euclidian.event;

public class ActionListener {

	private ActionListenerI listenerClass;

	public ActionListener() {
		this(null);
	}

	public ActionListener(ActionListenerI listener) {
		listenerClass = listener;
	}

	public void wrapActionPerformed(ActionEvent event) {
		listenerClass.actionPerformed(event);
	}

	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(ActionListenerI listenerClass) {
		this.listenerClass = listenerClass;
	}
}
