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
 * Handles pointer events in Euclidian view(or MSPointer events in case of IE10)
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

	/**
	 * Mutable representation of pointer events
	 */
	private static class PointerState {
		public double x;
		public double y;
		public double id;

		public PointerState(NativePointerEvent e) {
			id = e.getPointerId();
			x = e.getX();
			y = e.getY();
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
		tc.getOffsets().closePopups();
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
	}

	@ExternalAccess
	private void onPointerMove(NativePointerEvent e) {
		if (first != null && second != null) {
			if (second.id == e.getPointerId()) {
				second.x = e.getX();
				second.y = e.getY();
				twoPointersMove(first, second);
			} else {
				first.x = e.getX();
				first.y = e.getY();
			}
		} else {
			this.tc.onPointerEventMove(convertEvent(e));
		}
		if (!"INPUT".equals(e.getTarget().getTagName())) {
			e.preventDefault();
		}
		checkMoveLongTouch();
	}

	@ExternalAccess
	private void onPointerDown(NativePointerEvent e, Element element) {
		setCapture(element);
		if (first != null && second != null) {
			return;
		}
		if (first != null) {
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
		PointerEvent ex = new PointerEvent(e.getX(), e.getY(), types(e.getPointerType()), off,
				false);
		adjust(ex, e);
		return ex;
	}

	@ExternalAccess
	private void onMouseUpOrOut(NativePointerEvent event, Element element, boolean out) {
		if (pointerCapture != element && !out) {
			return;
		}
		if (first != null && first.id == event.getPointerId()) {
			first = null;
		} else {
			second = null;
		}
		if (!out && second == null && first == null) {
			setCapture(null);
		}
		if (!out) {
			singleUp(convertEvent(event));
		}
		setPointerType(event.getPointerType(), false);
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
		var fix = function(name) {
			return $wnd.PointerEvent ? name.toLowerCase() : "MS" + name;
		};

		element
				.addEventListener(
						fix("PointerMove"),
						function(e) {
							zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerMove(*)(e);
						});

		element
				.addEventListener(
						fix("PointerDown"),
						function(e) {
							zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onPointerDown(*)(e, element);
						});
		function removePointer(out){
			return function(e) {
				zoomer.@org.geogebra.web.html5.euclidian.PointerEventHandler::onMouseUpOrOut(*)(e, element, out);
			};
		}

		element.addEventListener(fix("PointerOut"), removePointer(true));
		$wnd.addEventListener(fix("PointerUp"), removePointer(false));
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
