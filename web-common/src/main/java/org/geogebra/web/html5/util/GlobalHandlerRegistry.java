package org.geogebra.web.html5.util;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;

import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import jsinterop.base.Js;

public class GlobalHandlerRegistry {
	private final ArrayList<HandlerRegistration> handlers = new ArrayList<>();

	/**
	 * @param registration handler registration from GWT event system
	 */
	public void add(HandlerRegistration registration) {
		handlers.add(registration);
	}

	/**
	 * Use JS addEventListener directly to add a listener
	 * @param target event target
	 * @param type event type
	 * @param listener listener
	 */
	public void addEventListener(EventTarget target, String type, EventListener listener) {
		target.addEventListener(type, listener, false);
		add(() -> target.removeEventListener(type, listener));
	}

	/**
	 * Use JS addEventListener directly to add a listener
	 * @param target event target
	 * @param type event type
	 * @param listener listener
	 */
	public void addEventListener(Object target, String type, EventListener listener) {
		EventTarget el = Js.uncheckedCast(target);
		addEventListener(el, type, listener);
	}

	/**
	 * Remove all listeners
	 */
	public void removeAllListeners() {
		for (HandlerRegistration registration: handlers) {
			registration.removeHandler();
		}
		handlers.clear();
	}
}
