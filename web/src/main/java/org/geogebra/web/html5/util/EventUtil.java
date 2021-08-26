package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;

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
	 * @param element
	 *            element
	 */
	public static native void stopPointer(Element element) /*-{
		if ($wnd.PointerEvent) {
			var evts = [ "PointerDown", "PointerUp" ];
			for ( var k in evts) {
				element.addEventListener(evts[k].toLowerCase(), function(e) {
					e.stopPropagation()
				});
			}
		}
	}-*/;
}
