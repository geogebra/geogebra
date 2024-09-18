package org.geogebra.web.html5.euclidian;

import java.util.Locale;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.dom.client.Element;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import jsinterop.base.Js;

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
	private double lastOutId;

	/**
	 * Mutable representation of pointer events
	 */
	private class PointerState {
		public double x;
		public double y;
		public double id;

		public PointerState(NativePointerEvent e) {
			id = e.getPointerId();
			x = e.getOffsetX() / off.getZoomLevel();
			y = e.getOffsetY() / off.getZoomLevel();
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
		tc.twoTouchStart(pointer1.x, pointer1.y, pointer2.x, pointer2.y);
	}

	private void twoPointersMove(PointerState pointer1, PointerState pointer2) {
		this.tc.twoTouchMove(pointer1.x, pointer1.y, pointer2.x, pointer2.y);
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
			tc.getLongTouchManager().scheduleTimer(tc, (int) touchState.x,
					(int) touchState.y);
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

	private void onPointerMove(NativePointerEvent e, Element element) {
		if (isPenStrokeInterrupted(e)) {
			onPointerDown(e, element);
		}
		if (first != null && second != null) {
			if (second.id == e.getPointerId()) {
				second.x = e.getOffsetX() / off.getZoomLevel();
				second.y = e.getOffsetY() / off.getZoomLevel();
				twoPointersMove(first, second);
			} else if (first.id == e.getPointerId()) {
				first.x = e.getOffsetX() / off.getZoomLevel();
				first.y = e.getOffsetY() / off.getZoomLevel();
			}
		} else if (match(first, e) || match(second, e)
				|| "mouse".equals(e.getPointerType())) {
			this.tc.onPointerEventMove(convertEvent(e));
		}
		if (!"INPUT".equals(e.getTarget().tagName)) {
			e.preventDefault();
		}
		checkMoveLongTouch();
	}

	/*
	 * If type is "pen", move can only happen if the pen is touching the surface.
	 * In case we lost all pointers and we're getting pen movement,
	 * we assume that the last "pointerout" event was a glitch (happens on iPad).
	 */
	private boolean isPenStrokeInterrupted(NativePointerEvent event) {
		return first == null && second == null && third == null
				&& lastOutId == event.getPointerId() && "pen".equals(event.getPointerType());
	}

	private boolean match(PointerState pointerState, NativePointerEvent event) {
		return pointerState != null && pointerState.id == event.getPointerId();
	}

	private void onPointerDown(NativePointerEvent e, Element element) {
		if (first != null && second != null && third != null) {
			reset();
			return;
		}
		setCapture(element);
		// APPS-5639 In mobile Safari copy only works from pointerup: collect now & run later
		CopyPasteW.startCollectingCopyCalls();
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
		PointerEvent ex = new PointerEvent(e.getOffsetX() / off.getZoomLevel(),
				e.getOffsetY() / off.getZoomLevel(), types(e.getPointerType()), off);
		adjust(ex, e);
		return ex;
	}

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
		CopyPasteW.stopCollectingCopyCalls();
	}

	private void onPointerOut(NativePointerEvent event) {
		lastOutId = event.getPointerId();
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
	 * @param element listening element (EV)
	 * @param globalHandlers global registrations
	 */
	public void attachTo(Element element, GlobalHandlerRegistry globalHandlers) {
		reset();
		// treat as global to avoid memory leak
		globalHandlers.addEventListener(element, "pointermove",
				evt -> onPointerMove(Js.uncheckedCast(evt), element));

		globalHandlers.addEventListener(element, "pointerdown",
				evt -> onPointerDown(Js.uncheckedCast(evt), element));

		globalHandlers.addEventListener(element, "pointerout",
				evt -> onPointerOut(Js.uncheckedCast(evt)));

		globalHandlers.addEventListener(element, "pointercanel",
				evt -> onPointerOut(Js.uncheckedCast(evt)));

		EventListener clickOutsideHandler = evt -> onPointerUp(Js.uncheckedCast(evt), element);
		globalHandlers.addEventListener(DomGlobal.window, "pointerup", clickOutsideHandler);
	}

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
