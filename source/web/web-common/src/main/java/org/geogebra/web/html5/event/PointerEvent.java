package org.geogebra.web.html5.event;

import java.util.LinkedList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.gwtproject.core.client.JsArray;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.client.Touch;
import org.gwtproject.event.dom.client.HumanInputEvent;
import org.gwtproject.event.dom.client.MouseEvent;
import org.gwtproject.event.dom.client.TouchEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;

/**
 * Base implementation of AbstractEvent.
 * 
 * @author Thomas Krismayer
 * 
 */
public class PointerEvent extends AbstractEvent {

	private Element relativeElement;

	private GPoint point;
	private PointerEventType type;
	private HasOffsets off;
	private boolean shift;
	private boolean control;
	private boolean alt;
	private boolean meta;
	private boolean right;
	private boolean middle;
	private int clickCount = 1;
	private int evID;
	private HumanInputEvent<?> nativeEvent;

	/**
	 * @param x
	 *            client x
	 * @param y
	 *            client y
	 * @param type
	 *            pointer type
	 * @param off
	 *            coordinate system
	 */
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
		return this.point.x;
	}

	@Override
	public int getY() {
		return this.point.y;
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
	 *            event
	 * @param off
	 *            offset
	 * @return wrapped event
	 */
	public static PointerEvent wrapEvent(TouchEvent<?> event, HasOffsets off) {
		JsArray<Touch> touches = null;
		int index = 0;
		if (event instanceof TouchStartEvent) {
			touches = event.getTargetTouches();
		} else if (event instanceof TouchMoveEvent) {
			touches = event.getTargetTouches();
			index = touches.length() - 1;
		} else { // assume if (event instanceof TouchEndEvent) {
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

	/**
	 * @param control
	 *            whether control is pressed
	 */
	public void setControl(boolean control) {
		this.control = control;
	}

	/**
	 * @param shift
	 *            whether shift is pressed
	 */
	public void setShift(boolean shift) {
		this.shift = shift;
	}

	/**
	 * @param alt
	 *            whether alt is pressed
	 */
	public void setAlt(boolean alt) {
		this.alt = alt;
	}

}
