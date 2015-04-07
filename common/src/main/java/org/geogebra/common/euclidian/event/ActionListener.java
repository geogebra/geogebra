package org.geogebra.common.euclidian.event;

public abstract class ActionListener {

	private ActionListenerI listenerClass;

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
