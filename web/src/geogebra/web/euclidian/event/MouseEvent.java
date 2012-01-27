package geogebra.web.euclidian.event;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;

import java.util.LinkedList;

import com.google.gwt.dom.client.NativeEvent;

public class MouseEvent extends AbstractEvent {

	public static LinkedList<MouseEvent> pool = new LinkedList<MouseEvent>();
	private NativeEvent event;
	private Integer id;

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

	private MouseEvent(NativeEvent event) {
		this.event = event;
	}

	public static AbstractEvent wrapEvent(NativeEvent nativeEvent) {
		if(!pool.isEmpty()){
			MouseEvent wrap = pool.getLast();
			wrap.event = nativeEvent;
			pool.removeLast();
			return wrap;
		}
		return new MouseEvent(nativeEvent);
	}

	@Override
	public Point getPoint() {

		return new Point(event.getClientX(), event.getClientY());
	}

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
		MouseEvent.pool.add(this);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getX() {
		return event.getClientX();
	}

	@Override
	public int getY() {
		return event.getClientY();
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
		return event.getRotation();
	}

	@Override
	public boolean isMiddleClick() {
		return (event.getButton() == NativeEvent.BUTTON_MIDDLE);
	}

}
