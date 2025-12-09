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

package org.geogebra.web.html5.util;

import java.util.function.Predicate;

import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.DomEvent;

import elemental2.dom.PointerEvent;
import jsinterop.base.Js;

/**
 * Simple static methods helping event handling.
 */
public final class EventUtil {

	private EventUtil() {
		// utility class
	}

	/**
	 * @param event
	 *            the event to be checked
	 * @return True if the event is a touch event.
	 */
	public static boolean isTouchEvent(DomEvent<?> event) {
		return isTouchEvent(event.getNativeEvent());
	}

	/**
	 * @param event
	 *            the event to be checked
	 * @return True if the event is a touch event.
	 */
	public static boolean isTouchEvent(NativeEvent event) {
		return event.getType().contains("touch");
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The x coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientX(NativeEvent event) {
		if (isTouchEvent(event)) {
			return event.getChangedTouches().get(0).getClientX();
		}
		return event.getClientX();
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The y coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientY(NativeEvent event) {
		if (isTouchEvent(event)) {
			return event.getChangedTouches().get(0).getClientY();
		}
		return event.getClientY();
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The x coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientX(DomEvent<?> event) {
		return getTouchOrClickClientX(event.getNativeEvent());
	}

	/**
	 * @param event
	 *            click or touch event
	 * @return The y coordinate of the event (in case of touch the coordinate is
	 *         taken from the first touch).
	 */
	public static int getTouchOrClickClientY(DomEvent<?> event) {
		return getTouchOrClickClientY(event.getNativeEvent());
	}

	/**
	 * Stop propagating all pointer events
	 * @param element target element
	 */
	public static void stopPointer(Element element) {
		stopPointerEvents(element, evt -> true);
	}

	/**
	 * Stops propagating pointer events such that the button matches a predicate.
	 * (we're checking the button instead od the whole event because of ClassCast
	 * for native types in lambdas...)
	 * @param element target element
	 */
	public static void stopPointerEvents(Element element, Predicate<Integer> check) {
		for (String evtName : new String[]{"pointerup", "pointerdown"}) {
			Dom.addEventListener(element, evtName, e -> {
				PointerEvent ptrEvent = Js.uncheckedCast(e);
				if (check.test(ptrEvent.button)) {
					e.stopPropagation();
				}
			});
		}
	}
}
