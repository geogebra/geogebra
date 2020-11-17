package org.geogebra.web.html5.euclidian;

import java.util.Locale;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;

import com.google.gwt.dom.client.Element;

/**
 * Handles pointer events in Euclidian view
 * 
 * @author Zbynek
 *
 */
public class PointerEventHandler {

	private final IsEuclidianController tc;
	private static Element pointerCapture;
	private HasOffsets off;

	private @CheckForNull PointerState first;
	private @CheckForNull PointerState second;
	private @CheckForNull PointerState third;

	/**
	 * Mutable representation of pointer events
	 */
	private static class PointerState {
		public double x;
		public double y;
		public double id;

		public PointerState(NativePointerEvent e) {
			id = e.getPointerId();
			x = e.getClientX();
			y = e.getClientY();
		}
	}

	/**
	 * @param tc
	 *            euclidian controller
	 * @param off
	 *            offset provider
	 */
	public PointerEventHandler(IsEuclidianController tc, @Nonnull HasOffsets off) {
		this.tc = tc;
		this.off = off;
	}

	private void twoPointersDown(PointerState pointer1, PointerState pointer2) {
		tc.getLongTouchManager().cancelTimer();
		tc.setExternalHandling(true);
		tc.twoTouchStart(touchEventX(pointer1), touchEventY(pointer1),
				touchEventX(pointer2), touchEventY(pointer2));
	}

	private void twoPointersMove(PointerState pointer1, PointerState pointer2) {
		this.tc.twoTouchMove(touchEventX(pointer1),	touchEventY(pointer1),
				touchEventX(pointer2), touchEventY(pointer2));
	}

	private int touchEventX(PointerState touch) {
		return off.touchEventX((int) touch.x);
	}

	private int touchEventY(PointerState touch) {
		return off.touchEventY((int) touch.y);
	}

	private void singleDown(PointerEvent e) {
		tc.getOffsets().closePopups(e);
		tc.onPointerEventStart(e);
	}

	private static void adjust(PointerEvent event, NativePointerEvent nativeEvent) {
		if (nativeEvent.getAltKey()) {
			event.setAlt(true);
		}
		if (nativeEvent.getShiftKey()) {
			event.setShift(true);
		}
		if (nativeEvent.getCtrlKey()) {
			event.setControl(true);
		}
		if (nativeEvent.getButton() == 2) {
			event.setIsRightClick(true);
		}
	}

	private void singleUp(PointerEvent e) {
		tc.getLongTouchManager().cancelTimer();
		tc.onPointerEventEnd(e);
	}

	private void setPointerType(String type, boolean pointerDown) {
		tc.getOffsets().calculateEnvironment();
		tc.setDefaultEventType(types(type), pointerDown);
	}

	private void startLongTouch(PointerState touchState) {
		if (tc.getMode() == EuclidianConstants.MODE_MOVE) {
			tc.getLongTouchManager().scheduleTimer(tc, touchEventX(touchState),
					touchEventY(touchState));
		}
	}

	private void checkMoveLongTouch() {
		if (tc.isDraggingBeyondThreshold()) {
			this.tc.getLongTouchManager().cancelTimer();
		}
	}

	/**
	 * Reset the pointers
	 */
	public void reset() {
		first = null;
		second = null;
		third = null;
	}

	@ExternalAccess
	private void onPointerMove(NativePointerEvent e) {
		if (first != null && second != null) {
			if (second.id == e.getPointerId()) {
				second.x = e.getClientX();
				second.y = e.getClientY();
				twoPointersMove(first, second);
			} else if (first.id == e.getPointerId()) {
				first.x = e.getClientX();
				first.y = e.getClientY();
			}
		} else if (match(first, e) || match(second, e)
				|| "mouse".equals(e.getPointerType())) {
			this.tc.onPointerEventMove(convertEvent(e));
		}
		if (!"INPUT".equals(e.getTarget().getTagName())) {
			e.preventDefault();
		}
		checkMoveLongTouch();
	}

	private boolean match(PointerState pointerState, NativePointerEvent event) {
		return pointerState != null && pointerState.id == event.getPointerId();
	}

	@ExternalAccess
	private void onPointerDown(NativePointerEvent e, Element element) {
		if (first != null && second != null && third != null) {
			reset();
			return;
		}
		setCapture(element);
		if (first != null && second != null) {
			third = new PointerState(e);
			return;
		} else if (first != null) {
			second = new PointerState(e);
		} else {
			first = new PointerState(e);
		}
		// prevent touch but not mouse: make sure focus is moved
		if (!"mouse".equals(e.getPointerType())) {
			e.preventDefault();
		}
		setPointerType(e.getPointerType(), true);
		if (first != null && second != null) {
			twoPointersDown(first, second);
		} else {
			singleDown(convertEvent(e));
			if ("touch".equals(e.getPointerType()) && first != null) {
				startLongTouch(first);
			}
		}
	}

	private PointerEvent convertEvent(NativePointerEvent e) {
		PointerEvent ex = new PointerEvent(e.getClientX(), e.getClientY(),
				types(e.getPointerType()), off, false);
		adjust(ex, e);
		return ex;
	}

	@ExternalAccess
	private void onPointerUp(NativePointerEvent event, Element element) {
		if (pointerCapture != element) {
			return;
		}
		resetPointer(event);
		if (second == null && first == null) {
			setCapture(null);
		}
		if (match(third, event)) {
			return;
		}
		singleUp(convertEvent(event));
		setPointerType(event.getPointerType(), false);
	}

	@ExternalAccess
	private void onPointerOut(NativePointerEvent event) {
		resetPointer(event);
		setPointerType(event.getPointerType(), false);
	}

	private void resetPointer(NativePointerEvent event) {
		if (match(first, event)) {
			first = null;
		} else if (match(second, event)) {
			second = null;
		} else if (match(third, event)) {
			third = null;
		} else {
			reset();
		}
	}

	/**
	 * @param element
	 *            listening element (EV)
	 * @param zoomer
	 *            event handler
	 */
	public static void attachTo(Element element, PointerEventHandler zoomer) {
		zoomer.reset();
		attachToNative(element, zoomer);
	}

	public static native void attachToNative(Element element,
			PointerEventHandler zoomer) /*-{
		element.addEventListener("pointermove", function(e) {
				zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerMove(*)(e);
		});

		element.addEventListener("pointerdown", function(e) {
				zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerDown(*)(e, element);
		});

		element.addEventListener("pointerout", function(e) {
            zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerOut(*)(e);
        });

        element.addEventListener("pointercanel", function(e) {
            zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerOut(*)(e);
        });

		$wnd.addEventListener("pointerup", function(e) {
            zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerUp(*)(e, element);
        });
	}-*/;

	public static void startCapture(EuclidianViewW view) {
		setCapture(view.getAbsolutePanel().getElement());
	}

	private static void setCapture(Element element) {
		pointerCapture = element;
	}

	private PointerEventType types(String s) {
		try {
			return PointerEventType.valueOf(s.toUpperCase(Locale.US));
		} catch (Exception e) {
			// no logging: too noisy
		}
		return PointerEventType.MOUSE;
	}

}
