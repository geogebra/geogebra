/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.euclidian;

import java.util.Locale;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
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

		public PointerState(elemental2.dom.PointerEvent e) {
			id = e.pointerId;
			x = e.offsetX / off.getZoomLevel();
			y = e.offsetY / off.getZoomLevel();
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
		tc.twoTouchStart(pointer1.x, pointer1.y, pointer2.x, pointer2.y);
	}

	private void twoPointersMove(PointerState pointer1, PointerState pointer2) {
		this.tc.twoTouchMove(pointer1.x, pointer1.y, pointer2.x, pointer2.y);
	}

	private void singleDown(PointerEvent e) {
		tc.getOffsets().closePopups(e);
		tc.onPointerEventStart(e);
	}

	private static void adjust(PointerEvent event, elemental2.dom.PointerEvent nativeEvent) {
		event.setAlt(nativeEvent.altKey);
		event.setShift(nativeEvent.shiftKey);
		event.setControl(nativeEvent.ctrlKey);
		event.setButton(nativeEvent.button);
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

	private void onPointerMove(elemental2.dom.PointerEvent e, Element element) {
		if (isPenStrokeInterrupted(e)) {
			onPointerDown(e, element);
		}
		if (first != null && second != null) {
			if (second.id == e.pointerId) {
				second.x = e.offsetX / off.getZoomLevel();
				second.y = e.offsetY / off.getZoomLevel();
				twoPointersMove(first, second);
			} else if (first.id == e.pointerId) {
				first.x = e.offsetX / off.getZoomLevel();
				first.y = e.offsetY / off.getZoomLevel();
			}
		} else if (match(first, e) || match(second, e)
				|| "mouse".equals(e.pointerType)) {
			this.tc.onPointerEventMove(convertEvent(e));
		}
		if (!"INPUT".equals(Js.<elemental2.dom.Element>uncheckedCast(e.target).tagName)) {
			e.preventDefault();
		}
		checkMoveLongTouch();
	}

	/*
	 * If type is "pen", move can only happen if the pen is touching the surface.
	 * In case we lost all pointers and we're getting pen movement,
	 * we assume that the last "pointerout" event was a glitch (happens on iPad).
	 */
	private boolean isPenStrokeInterrupted(elemental2.dom.PointerEvent event) {
		return first == null && second == null && third == null
				&& lastOutId == event.pointerId && "pen".equals(event.pointerType);
	}

	private boolean match(PointerState pointerState, elemental2.dom.PointerEvent event) {
		return pointerState != null && pointerState.id == event.pointerId;
	}

	private void onPointerDown(elemental2.dom.PointerEvent e, Element element) {
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
		if (!"mouse".equals(e.pointerType)) {
			e.preventDefault();
		}
		setPointerType(e.pointerType, true);
		if (first != null && second != null) {
			twoPointersDown(first, second);
		} else {
			singleDown(convertEvent(e));
			if ("touch".equals(e.pointerType) && first != null) {
				startLongTouch(first);
			}
		}
	}

	private PointerEvent convertEvent(elemental2.dom.PointerEvent e) {
		PointerEvent ex = new PointerEvent(e.offsetX / off.getZoomLevel(),
				e.offsetY / off.getZoomLevel(), types(e.pointerType), off);
		adjust(ex, e);
		return ex;
	}

	private void onPointerUp(elemental2.dom.PointerEvent event, Element element) {
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
		setPointerType(event.pointerType, false);
		CopyPasteW.stopCollectingCopyCalls();
	}

	private void onPointerOut(elemental2.dom.PointerEvent event) {
		lastOutId = event.pointerId;
		resetPointer(event);
		setPointerType(event.pointerType, false);
	}

	private void resetPointer(elemental2.dom.PointerEvent event) {
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

	/**
	 * Make the view capture all pointer events.
	 * @param view graphics view
	 */
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
