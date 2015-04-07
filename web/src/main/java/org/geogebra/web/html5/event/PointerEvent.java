package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

/**
 * Base implementation of AbstractEvent.
 * 
 * @author Thomas Krismayer
 * 
 */
public class PointerEvent extends AbstractEvent {

	private Element relativeElement;

	private GPoint point = new GPoint(0, 0);
	private PointerEventType type;
	private HasOffsets off;
	private boolean shift, control, alt, meta, right, middle;
	private int clickCount = 1;
	private int evID;
	private HumanInputEvent<?> nativeEvent;

	public PointerEvent(double x, double y, PointerEventType type,
	        HasOffsets off) {
		this.off = off;
		this.point = new GPoint((int) Math.round(x), (int) Math.round(y));
		this.type = type;
		this.evID = off.getEvID();
	}

	@Override
	public int getClickCount() {
		return this.clickCount;
	}

	@Override
	public GPoint getPoint() {
		return new GPoint(getX(), getY());
	}

	@Override
	public int getX() {
		if (this.type == PointerEventType.MOUSE) {
			return off.mouseEventX(this.point.x);
		}
		return off.touchEventX(this.point.x);
	}

	@Override
	public int getY() {
		if (this.type == PointerEventType.MOUSE) {
			return off.mouseEventY(this.point.y);
		}
		return off.touchEventY(this.point.y);
	}

	@Override
	public boolean isAltDown() {
		return this.alt;
	}

	@Override
	public boolean isControlDown() {
		return this.control;
	}

	@Override
	public boolean isMetaDown() {
		return this.meta;
	}

	@Override
	public boolean isMiddleClick() {
		return this.middle;
	}

	@Override
	public boolean isPopupTrigger() {
		return false;
	}

	@Override
	public boolean isRightClick() {
		return this.right;
	}

	/**
	 * set is (or isn't) right click
	 * 
	 * @param flag
	 *            value
	 */
	public void setIsRightClick(boolean flag) {
		this.right = flag;
	}

	@Override
	public boolean isShiftDown() {
		return this.shift;
	}

	@Override
	public void release() {
		if (this.type == PointerEventType.TOUCH) {
			this.off.getTouchEventPool().add(this);
		}
	}

	@Override
	public PointerEventType getType() {
		return this.type;
	}

	private static PointerEvent wrapEvent(int x, int y, PointerEventType type,
	        HasOffsets h, LinkedList<PointerEvent> pool) {
		if (!pool.isEmpty()) {
			PointerEvent wrap = pool.getLast();
			wrap.point = new GPoint(x, y);
			wrap.type = type;
			wrap.evID = h.getEvID();
			pool.removeLast();
			return wrap;
		}
		return new PointerEvent(x, y, type, h);
	}

	private static void setProperties(PointerEvent destination,
	        MouseEvent<?> source) {
		destination.nativeEvent = source;
		destination.alt = source.isAltKeyDown();
		destination.control = source.isControlKeyDown();
		destination.clickCount = "dblclick".equals(source.getNativeEvent()
		        .getType()) ? 2 : 1;
		destination.meta = source.isMetaKeyDown();
		destination.middle = source.getNativeButton() == NativeEvent.BUTTON_MIDDLE;
		destination.right = source.getNativeButton() == NativeEvent.BUTTON_RIGHT;
		destination.shift = source.isShiftKeyDown();
		destination.relativeElement = source.getRelativeElement();
	}

	/**
	 * Wraps the event taking the relative coordinates of the event.
	 * 
	 * @param event
	 *            event to wrap
	 * @param off
	 *            offsets
	 * @return wrapped event
	 */
	public static PointerEvent wrapEvent(MouseEvent<?> event, HasOffsets off) {
		PointerEvent evt = wrapEvent(event.getX(), event.getY(),
		        off.getDefaultEventType(), off, off.getMouseEventPool());
		setProperties(evt, event);
		return evt;
	}

	/**
	 * Wraps the event taking the absolute coordinates of the event.
	 * 
	 * @param event
	 *            event to wrap
	 * @param off
	 *            offsets
	 * @return wrapped event
	 */
	public static PointerEvent wrapEventAbsolute(MouseEvent<?> event,
	        HasOffsets off) {
		int clientX = event.getClientX();
		int clientY = event.getClientY();
		PointerEvent evt = wrapEvent(clientX, clientY,
		        off.getDefaultEventType(), off, off.getMouseEventPool());
		setProperties(evt, event);
		return evt;
	}

	/**
	 * Creates a wrapped event, based on the touch coordinates, with a relative
	 * element.
	 * 
	 * @param touch
	 *            touch
	 * @param off
	 *            offsets
	 * @param relativeElement
	 *            event relative to element
	 * @return wrapped event
	 */
	public static PointerEvent wrapEvent(Touch touch, HasOffsets off,
	        Element relativeElement) {
		PointerEvent event = wrapEvent(touch, off);
		event.relativeElement = relativeElement;
		return event;
	}

	/**
	 * Creates a wrapped event, based on the touch coordinates.
	 * 
	 * @param touch
	 *            touch
	 * @param off
	 *            offsets
	 * @return wrapped event
	 */
	public static PointerEvent wrapEvent(Touch touch, HasOffsets off) {
		return wrapEvent(touch.getClientX(), touch.getClientY(),
		        PointerEventType.TOUCH, off, off.getTouchEventPool());
	}

	/**
	 * Wraps a single touch event.
	 * 
	 * @param event
	 * @param off
	 * @return
	 */
	public static PointerEvent wrapEvent(TouchEvent<?> event, HasOffsets off) {
		JsArray<Touch> touches = null;
		int index = 0;
		if (event instanceof TouchStartEvent) {
			touches = event.getTargetTouches();
		} else if (event instanceof TouchMoveEvent) {
			touches = event.getTargetTouches();
			index = touches.length() - 1;
		} else if (event instanceof TouchEndEvent) {
			touches = event.getChangedTouches();
		}
		PointerEvent e = wrapEvent(touches.get(index), off);
		e.nativeEvent = event;
		return e;
	}

	/**
	 * @return The euclidian view id if the event was fired on it, else 0.
	 */
	public int getEvID() {
		return this.evID;
	}

	/**
	 * This field is only set when the event was created with
	 * {@link PointerEvent#wrapEvent(Touch, HasOffsets, Element)}.
	 * 
	 * @return the event relative to the element
	 */
	public Element getRelativeElement() {
		return relativeElement;
	}

	/**
	 * Returns the event that was wrapped.
	 * 
	 * @return native event
	 */
	public HumanInputEvent<?> getWrappedEvent() {
		return nativeEvent;
	}

}
