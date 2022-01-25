package org.geogebra.web.html5.util;

import org.geogebra.regexp.shared.RegExp;

import com.google.gwt.dom.client.Element;

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
	 *            transition callback
	 * @param root
	 *            the transition element
	 * @param classname
	 *            class to be checked for transition and removed afterwards
	 */
	public static void runOnTransition(Runnable runnable, Element root, String classname) {
		runOnEvent(runnable, "transitionend", Js.uncheckedCast(root), classname);
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
