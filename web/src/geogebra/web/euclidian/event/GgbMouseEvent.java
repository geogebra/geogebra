package geogebra.web.euclidian.event;

import geogebra.web.kernel.gawt.Point;

import com.google.gwt.event.dom.client.HasNativeEvent;

public class  GgbMouseEvent {

	private boolean altKeyDown;
	private boolean metaKeyDown;
	private boolean controlKeyDown;
	private int clickCount;
	private boolean shiftKeyDown;
	private int x;
	private int y;
	private String type;
	
	public GgbMouseEvent() {
		altKeyDown = false;
		metaKeyDown = false;
		controlKeyDown = false;
	}
	
	public GgbMouseEvent(HasNativeEvent event) {
		altKeyDown = event.getNativeEvent().getAltKey();
		metaKeyDown = event.getNativeEvent().getMetaKey();
		controlKeyDown = event.getNativeEvent().getCtrlKey();
		x = event.getNativeEvent().getClientX();
		y = event.getNativeEvent().getClientY();
		type = event.getNativeEvent().getType();
		setClickCount(type);
	}

	public GgbMouseEvent(int eventx, int eventy) {
		altKeyDown = false;
		metaKeyDown = false;
		controlKeyDown = false;
		x = eventx;
		y = eventy;
		type = "touchevent";
	}

	private void setClickCount(String type) {
		
		if (type.indexOf("doubleclick") > -1) {
			clickCount = 2;
		} else if (type.indexOf("click") > -1) {
			clickCount = 1;
		} else {
			clickCount = 0;
		}
		
	}

	public boolean isAltKeyDown() {
		return altKeyDown;
	}

	public boolean isMetaKeyDown() {
		return metaKeyDown;
	}

	public boolean isControlKeyDown() {
		return controlKeyDown;
	}

	public Point getPoint() {
		return new Point(x,y);
	}

	public int getClickCount() {
		return clickCount;
	}

	public boolean isShiftKeyDown() {
		return shiftKeyDown;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWheelRotation() {
		return 0;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void translatePoint(int x, int y) {
		this.x += x;
		this.y += y;
	}


}
