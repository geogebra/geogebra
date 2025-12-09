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

import org.geogebra.regexp.shared.RegExp;
import org.gwtproject.dom.client.Element;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import jsinterop.base.Js;

/**
 * Utility class for CSS events
 */
public class CSSEvents {

	private static void runOnEvent(Runnable runnable, String eventName, elemental2.dom.Element root,
			String classname) {
		RegExp reClass = RegExp.compile(classname);
		EventListener callback = new EventListener() {
			@Override
			public void handleEvent(Event event) {
				root.removeEventListener(eventName, this);
				if (reClass.exec(root.className.toString()) != null) {
					root.className = root.className.toString().replace(classname, "");
					if (runnable != null) {
						runnable.run();
					}
				}
			}
		};

		if (reClass.exec(root.className.toString()) != null) {
			root.addEventListener(eventName, callback);
			return;
		}

		DomGlobal.setTimeout((_0) -> callback.handleEvent(null), 0);
	}

	/**
	 * @param runnable
	 *            animation callback
	 * @param root
	 *            animated element
	 * @param classname
	 *            class to be checked for the animation and removed afterwards
	 */
	public static void runOnAnimation(Runnable runnable, Element root,
			String classname) {
		runOnEvent(runnable, "animationend", Js.uncheckedCast(root), classname);
	}
}
