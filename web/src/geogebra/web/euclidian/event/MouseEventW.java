package geogebra.web.euclidian.event;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;

import java.util.LinkedList;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;

public class MouseEventW extends AbstractEvent {

	
	private NativeEvent event;
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

	private MouseEventW(NativeEvent event, HasOffsets h) {
		this.event = event;
		this.off = h;
	}
	private HasOffsets off;
	public static MouseEventW wrapEvent(NativeEvent nativeEvent,HasOffsets h) {
		LinkedList<MouseEventW> pool = h.getMouseEventPool();
		if(!pool.isEmpty()){
			MouseEventW wrap = pool.getLast();
			wrap.event = nativeEvent;
			pool.removeLast();
			return wrap;
		}
		if (!h.isOffsetsUpToDate()) {
			h.updateOffsets();
		}
		return new MouseEventW(nativeEvent,h);
	}

	@Override
	public GPoint getPoint() {
		return new GPoint(getX(), getY());
	}

	public native int getOffsetX() /*-{
		var thisevent = this.@geogebra.web.euclidian.event.MouseEventW::event;
		if (thisevent.offsetX)
			return thisevent.offsetX;
		else if (thisevent.layerX)
			return thisevent.layerX;
		return -1;
	}-*/;

	public native int getOffsetY() /*-{
		var thisevent = this.@geogebra.web.euclidian.event.MouseEventW::event;
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

	public static EventTarget getTarget(AbstractEvent e) {
		if(e instanceof MouseEventW){
			return ((MouseEventW)e).event.getEventTarget();
		}
		if(e instanceof PointerEvent){
			return ((PointerEvent)e).getTarget();
		}
		return null;
	}

	@Override
	public void release() {
		off.getMouseEventPool().add(this);
	}

	@Override
	public int getX() {
		return Math.round((event.getClientX() - off.getXoffset()) *
							(1 / off.getScaleX()) *
								(1 / off.getWidthScale()));
	}

	@Override
	public int getY() {
		return Math.round((event.getClientY() - off.getYoffset())  *
					(1 / off.getScaleY()) *
						(1 / off.getHeightScale()));
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
		if (event.getType() == "dblclick") {
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
	    MouseEventW e = wrapEvent(nativeEvent,h);
	    e.rotation = deltaY;
	    return e;
    }

	@Override
    public boolean isPopupTrigger() {
	    return false;
    }
	
	@Override
	public PointerEventType getType() {
		return PointerEventType.MOUSE;
	}

}
