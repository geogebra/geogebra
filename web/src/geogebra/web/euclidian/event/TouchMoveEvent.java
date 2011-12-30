package geogebra.web.euclidian.event;

import com.google.gwt.event.dom.client.MouseMoveEvent;

public class TouchMoveEvent extends MouseMoveEvent {

	private int clientX;
	private int clientY;
	private boolean altkey = false;
	public void setClientX(int clientX) {
		this.clientX = clientX;
	}
	public int getClientX() {
		return clientX;
	}
	public void setClientY(int clientY) {
		this.clientY = clientY;
	}
	public int getClientY() {
		return clientY;
	}
	public boolean isAltKeyDown() {
		return altkey;
	}

}
