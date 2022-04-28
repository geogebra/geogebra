package org.geogebra.web.html5.util;

import org.apache.commons.collections15.Predicate;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.web.html5.gui.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;

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
				NativePointerEvent ptrEvent = Js.uncheckedCast(e);
				if (check.evaluate(ptrEvent.getButton())) {
					e.stopPropagation();
				}
			});
		}
	}
}
