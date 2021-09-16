package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;

/**
 * Utility class for CSS events
 */
public class CSSEvents {

	private static native void runOnEvent(Runnable runnable, String eventName, Element root,
			String classname) /*-{
		var reClass = RegExp(classname);
		var callback = function() {
			root.removeEventListener(eventName, callback);
			if (root.className.match(reClass)) {
				root.className = root.className.replace(reClass, "");
				if (runnable) {
					runnable.@java.lang.Runnable::run()();
				}
			}
		};
		if ((root.style.animation || root.style.animation === "")
				&& root.className.match(reClass)) {

			root.addEventListener(eventName, callback);
			return;
		}
		$wnd.setTimeout(callback, 0);
	}-*/;

	/**
	 * @param runnable
	 *            transition callback
	 * @param root
	 *            the transition element
	 * @param classname
	 *            class to be checked for transition and removed afterwards
	 */
	public static void runOnTransition(Runnable runnable, Element root, String classname) {
		runOnEvent(runnable, "transitionend", root, classname);
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
		runOnEvent(runnable, "animationend", root, classname);
	}
}
