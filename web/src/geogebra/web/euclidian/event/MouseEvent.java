package geogebra.web.euclidian.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.main.App;
import geogebra.web.euclidian.EuclidianControllerW;

import java.util.LinkedList;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

public class MouseEvent extends AbstractEvent {

	
	private NativeEvent event;
	private Integer id;
	private double rotation;

	/*public static final int MOUSE_CLICKED = 500;
	public static final int MOUSE_DRAGGED = 506;
	public static final int MOUSE_ENTERED = 504;
	public static final int MOUSE_EXITED = 505;
	public static final int MOUSE_FIRST = 500;
	public static final int MOUSE_LAST = 507;
	public static final int MOUSE_MOVED = 503;
	public static final int MOUSE_PRESSED = 501;
	public static final int MOUSE_RELEASED = 502;
	public static final int MOUSE_WHEEL = 507;*/

	private MouseEvent(NativeEvent event, HasOffsets h) {
		this.event = event;
		this.off = h;
	}
	private HasOffsets off;
	public static MouseEvent wrapEvent(NativeEvent nativeEvent,HasOffsets h) {
		LinkedList<MouseEvent> pool = h.getMouseEventPool();
		if(!pool.isEmpty()){
			MouseEvent wrap = pool.getLast();
			wrap.event = nativeEvent;
			pool.removeLast();
			return wrap;
		}
		if (!h.isOffsetsUpToDate()) {
			h.updateOffsets();
		}
		return new MouseEvent(nativeEvent,h);
	}

	@Override
	public GPoint getPoint() {
		return new GPoint(event.getClientX() - off.getXoffset(), event.getClientY() - off.getYoffset());
	}

	public native int getOffsetX() /*-{
		var thisevent = this.@geogebra.web.euclidian.event.MouseEvent::event;
		if (thisevent.offsetX)
			return thisevent.offsetX;
		else if (thisevent.layerX)
			return thisevent.layerX;
		return -1;
	}-*/;

	public native int getOffsetY() /*-{
		var thisevent = this.@geogebra.web.euclidian.event.MouseEvent::event;
		if (thisevent.offsetY)
			return thisevent.offsetY;
		else if (thisevent.layerY)
			return thisevent.layerY;
		return -1;
	}-*/;

	@Override
	public boolean isAltDown() {
		return event.getAltKey();
	}

	@Override
	public boolean isShiftDown() {
		return event.getShiftKey();
	}

	public static NativeEvent getEvent(AbstractEvent e) {
		return ((MouseEvent)e).event;
	}

	@Override
	public void release() {
		off.getMouseEventPool().add(this);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getX() {
		return event.getClientX() - off.getXoffset();
	}

	@Override
	public int getY() {
		return event.getClientY() - off.getYoffset();
	}

	@Override
	public boolean isRightClick() {
		return (event.getButton() == NativeEvent.BUTTON_RIGHT);
	}

	@Override
	public boolean isControlDown() {
		return event.getCtrlKey();
	}

	@Override
	public int getClickCount() {
		if (event.getType() == "doubleclick") {
			return 2;
		}
		return 1;

	}

	@Override
	public boolean isMetaDown() {
		return event.getMetaKey();
	}

	@Override
	public double getWheelRotation() {
		//AbstractApplication.console("rot:"+event.getRotation());
		return rotation;
	}

	@Override
	public boolean isMiddleClick() {
		return (event.getButton() == NativeEvent.BUTTON_MIDDLE);
	}

	public static AbstractEvent wrapEvent(NativeEvent nativeEvent, int deltaY,HasOffsets h) {
	    MouseEvent e = wrapEvent(nativeEvent,h);
	    e.rotation = deltaY;
	    return e;
    }

	@Override
    public boolean isPopupTrigger() {
	    return false;
    }

}
