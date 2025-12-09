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

import java.util.ArrayList;

import org.gwtproject.event.shared.HandlerRegistration;

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
