package geogebra.html5.util;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;

/**
 * Simple static methods helping event handling
 */
public abstract class EventUtil {
	
	public static boolean isTouchEvent(DomEvent<?> event) {
		return isTouchEvent(event.getNativeEvent());
	}
	
	public static boolean isTouchEvent(NativeEvent event) {
		return event.getType().contains("touch");
	}
	
	public static int getTouchOrClickClientX(NativeEvent event) {
		if (isTouchEvent(event)) {
			return event.getChangedTouches().get(0).getClientX();
		} else {
			return event.getClientX();
		}
	}
	
	public static int getTouchOrClickClientY(NativeEvent event) {
		if (isTouchEvent(event)) {
			return event.getChangedTouches().get(0).getClientY();
		} else {
			return event.getClientY();
		}
	}
	
	public static int getTouchOrClickClientX(DomEvent<?> event) {
		return getTouchOrClickClientX(event.getNativeEvent());
	}
	
	public static int getTouchOrClickClientY(DomEvent<?> event) {
		return getTouchOrClickClientY(event.getNativeEvent());
	}
}
